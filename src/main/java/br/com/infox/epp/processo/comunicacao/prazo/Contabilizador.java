package br.com.infox.epp.processo.comunicacao.prazo;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.timer.TaskExpirationInfo;
import br.com.infox.epp.processo.timer.TaskExpirationProcessor;
import br.com.infox.ibpm.process.definition.annotations.DefinitionAvaliable;
import br.com.infox.ibpm.util.JbpmUtil;

@Name(Contabilizador.NAME)
@Scope(ScopeType.STATELESS)
@AutoCreate
@DefinitionAvaliable
public class Contabilizador {
    public static final String NAME = "contabilizador";
    private static final LogProvider LOG = Logging.getLogProvider(Contabilizador.class);
    
    @In
    private ComunicacaoService comunicacaoService;
    @In(value = "org.jboss.seam.bpm.taskInstance")
    private TaskInstance taskInstance;
    @In
    private TaskExpirationProcessor taskExpirationProcessor;
    @In
    private MetadadoProcessoManager metadadoProcessoManager;
    
    public void contabilizarPrazoCiencia(String transition) {
    	Processo comunicacao = JbpmUtil.getProcesso();
    	Date fimPrazo = comunicacaoService.contabilizarPrazoCiencia(comunicacao);
    	
    	contabilizarPrazo(fimPrazo, transition);
    }
    
    public void contabilizarPrazoCumprimento(String transition) {
    	Processo comunicacao = JbpmUtil.getProcesso();
    	Date fimPrazo = comunicacaoService.contabilizarPrazoCumprimento(comunicacao);
    	
    	if (fimPrazo == null) {
    		taskInstance.end(transition);
    	} else {
    		contabilizarPrazo(fimPrazo, transition);
    	}
    }
    
    public void darCiencia(String transition) {
    	Processo comunicacao = JbpmUtil.getProcesso();
    	Date ciencia = new Date();
    	MetadadoProcesso metadado = new MetadadoProcesso();
    	metadado.setProcesso(comunicacao);
    	metadado.setMetadadoType(ComunicacaoService.DATA_CIENCIA);
    	metadado.setClassType(Date.class);
    	metadado.setValor(new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(ciencia));
    	try {
			metadadoProcessoManager.persist(metadado);
		} catch (DAOException e) {
			LOG.error("", e);
			throw new RuntimeException(e);
		}
    	
    	// Falta remover o job antigo
    }
    
    public void darCumprimento(String transition) {
    	
    }
    
    private void contabilizarPrazo(Date fimPrazo, String transition) {
    	TaskExpirationInfo info = new TaskExpirationInfo();
    	info.setTransition(transition);
    	info.setTaskId(taskInstance.getId());
    	info.setExpiration(fimPrazo);
    	
    	taskExpirationProcessor.endTask(info.getExpiration(), info);
    }
}
