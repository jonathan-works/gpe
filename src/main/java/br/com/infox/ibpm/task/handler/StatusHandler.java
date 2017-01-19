package br.com.infox.ibpm.task.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoSearch;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

public class StatusHandler implements ActionHandler, CustomAction {

	private static final LogProvider LOG = Logging.getLogProvider(StatusHandler.class);
	private static final long serialVersionUID = 1L;
	private String codigoStatusProcesso;

	public StatusHandler() {}
	
	public StatusHandler(String s) {
		Pattern pattern = Pattern.compile("<statusProcesso>(.+?)</statusProcesso>");
		Matcher matcher = pattern.matcher(s);
		if (matcher.find()) {
			this.codigoStatusProcesso = matcher.group(1);
		}
	}
	
	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		ProcessoManager processoManager = ComponentUtil.getComponent(ProcessoManager.NAME);
		StatusProcessoSearch statusProcessoSearch = BeanManager.INSTANCE.getReference(StatusProcessoSearch.class);
		try {
			StatusProcesso status = statusProcessoSearch.getStatusByName(codigoStatusProcesso);
			Processo processo = processoManager.find(Integer.parseInt(executionContext.getContextInstance().getVariable("processo").toString(), 10));
			setStatusProcesso(processo, status);
			processoManager.flush();
		} catch (Exception e) {
			LOG.error("Falha na atribuição de status do processo", e);
		}
	}

    @Override
    public String parseJbpmConfiguration(String configuration) {
        Pattern pattern = Pattern.compile("(<statusProcesso>.+?</statusProcesso>)");
        Matcher matcher = pattern.matcher(configuration);
        if (matcher.find()) {
            configuration = matcher.group(1);
        }
        return configuration;
    }
    
    private void setStatusProcesso(Processo processo, StatusProcesso status) throws DAOException {
        MetadadoProcesso statusMetadado = processo.getMetadado(EppMetadadoProvider.STATUS_PROCESSO);
        MetadadoProcessoManager metadadoManager = ComponentUtil.getComponent(MetadadoProcessoManager.NAME);
        if (statusMetadado != null) {
            statusMetadado.setValor(status.getIdStatusProcesso().toString());
            metadadoManager.update(statusMetadado);
        } else {
        	MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(processo);
        	statusMetadado = metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.STATUS_PROCESSO, status.getIdStatusProcesso().toString());
            metadadoManager.persist(statusMetadado);
        }
        processo.getMetadadoProcessoList().add(statusMetadado);
    }
    
    public String getCodigoStatusProcesso() {
		return this.codigoStatusProcesso;
	}

}