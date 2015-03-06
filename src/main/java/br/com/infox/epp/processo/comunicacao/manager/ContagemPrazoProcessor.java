package br.com.infox.epp.processo.comunicacao.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.joda.time.DateTime;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@AutoCreate
@Name(ContagemPrazoProcessor.NAME)
public class ContagemPrazoProcessor {
	
	public static final String NAME = "contagemPrazoProcessor";
	private static final LogProvider LOG = Logging.getLogProvider(ContagemPrazoProcessor.class);
	
	@In
	private ProcessoManager processoManager;
	@In
	private PrazoComunicacaoService prazoComunicacaoService;
	@In
	private ProcessoTarefaManager processoTarefaManager;
	@In
	private SituacaoProcessoDAO situacaoProcessoDAO;
	
	@Asynchronous
	@Transactional
	public QuartzTriggerHandle processContagemPrazoComunicacao(@IntervalCron String cron) {
		try {
			analisarProcessosAguardandoCiencia();
			analisarProcessosAguardandoCumprimento();
		} catch (DAOException e) {
			LOG.error("processContagemPrazoComunicacao", e);
		}
	    return null;
	}
	
	private void analisarProcessosAguardandoCumprimento() throws DAOException {
		List<Processo> processos = processoManager.listProcessosComunicacaoAguardandoCumprimento();
		for (Processo processo : processos) {
		    DateTime dataParaCumprimento = new DateTime(processo.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO));
			if (dataParaCumprimento.isBeforeNow()) {
				movimentarProcessoJBPM(processo);
			}
		}
	}
	
	private void analisarProcessosAguardandoCiencia() throws DAOException {
		List<Processo> processos = processoManager.listProcessosComunicacaoAguardandoCiencia();
		for (Processo processo : processos) {
		    DateTime dataParaCiencia = new DateTime(processo.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CIENCIA));
			if (dataParaCiencia.isBeforeNow()) {
				movimentarProcessoJBPM(processo);
			}
		}
	}
	
	private void movimentarProcessoJBPM(Processo processo) throws DAOException {
		Long idTaskInstance = situacaoProcessoDAO.getIdTaskInstanceByIdProcesso(processo.getIdProcesso());
		if (idTaskInstance == null) {
			LOG.warn("idTaskInstance para o processo " + processo.getNumeroProcesso() + " nulo");
			return;
		}
		TaskInstance taskInstance = ManagedJbpmContext.instance().getTaskInstanceForUpdate(idTaskInstance);
		taskInstance.end();
		atualizarProcessoTarefa(taskInstance);
	}
	
	private void atualizarProcessoTarefa(TaskInstance taskInstance) throws DAOException {
		ProcessoTarefa processoTarefa = processoTarefaManager.getByTaskInstance(taskInstance.getId());
		processoTarefa.setDataFim(taskInstance.getEnd());
		processoTarefaManager.update(processoTarefa);
	}

}
