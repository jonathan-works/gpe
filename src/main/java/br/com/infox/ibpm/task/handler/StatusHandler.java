package br.com.infox.ibpm.task.handler;

import static java.text.MessageFormat.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoManager;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.seam.util.ComponentUtil;

public class StatusHandler implements ActionHandler, CustomAction {

	private static final LogProvider LOG = Logging.getLogProvider(StatusHandler.class);
	private static final long serialVersionUID = 1L;
	private Integer idStatusProcesso;

	public StatusHandler() {}
	
	public StatusHandler(String s) {
		Pattern pattern = Pattern.compile("<statusProcesso>(\\d+)</statusProcesso>");
		Matcher matcher = pattern.matcher(s);
		if (matcher.find()) {
			String status = matcher.group(1);
			this.idStatusProcesso = Integer.parseInt(status);
		}
	}
	
	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		ProcessoManager processoManager = ComponentUtil.getComponent(ProcessoManager.NAME);
		StatusProcessoManager statusProcessoManager = ComponentUtil.getComponent(StatusProcessoManager.NAME);
		
		try {
			StatusProcesso status = statusProcessoManager.find(idStatusProcesso);
			Processo processo = processoManager.find(Integer.parseInt(executionContext.getContextInstance().getVariable("processo").toString(), 10));
			setStatusProcesso(processo, status);
			
			String varName = format("{0}:{1}", VariableType.STRING, "statusProcesso");
			
			ContextInstance contextInstance = executionContext.getProcessInstance().getContextInstance();
			Token token = executionContext.getToken();
			String nome = status.getNome();
			if (contextInstance.hasVariable(varName, token)) {
				contextInstance.setVariable(varName, nome, token);
			} else {
				contextInstance.createVariable(varName, nome, token);
			}
			processoManager.flush();
		} catch (Exception e) {
			LOG.error("Falha na atribuição de status do processo", e);
		}
	}

    @Override
    public String parseJbpmConfiguration(String configuration) {
        Pattern pattern = Pattern.compile("(<statusProcesso>\\d+</statusProcesso>)");
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
    }

}