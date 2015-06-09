package br.com.infox.epp.processo.comunicacao.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.ProrrogacaoPrazoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
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
	private ProrrogacaoPrazoService prorrogacaoPrazoService;

	
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
		    if (!prorrogacaoPrazoService.hasPedidoProrrogacaoEmAberto(processo)){
	            prazoComunicacaoService.movimentarComunicacaoPrazoExpirado(processo, ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO);
		    }
		}
	}
	
	private void analisarProcessosAguardandoCiencia() throws DAOException {
		List<Processo> processos = processoManager.listProcessosComunicacaoAguardandoCiencia();
		for (Processo processo : processos) {
			prazoComunicacaoService.movimentarComunicacaoPrazoExpirado(processo, ComunicacaoMetadadoProvider.LIMITE_DATA_CIENCIA);
		}
	}
	
	

}
