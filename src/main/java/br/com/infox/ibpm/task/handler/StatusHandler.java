package br.com.infox.ibpm.task.handler;

import static java.text.MessageFormat.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoManager;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.seam.util.ComponentUtil;

@Deprecated // verificar como ficará o status do processo como variável
public class StatusHandler implements ActionHandler, CustomAction {

	private static final LogProvider LOG = Logging.getLogProvider(StatusHandler.class);
	private static final long serialVersionUID = 1L;
	private Integer statusProcesso;

	public StatusHandler() {}
	
	public StatusHandler(String s) {
		Pattern pattern = Pattern.compile("<statusProcesso>(\\d+)</statusProcesso>");
		Matcher matcher = pattern.matcher(s);
		if (matcher.find()) {
			String status = matcher.group(1);
			this.statusProcesso = Integer.parseInt(status);
		}
	}
	
	@Override
	@Deprecated // duas linhas comentadas
	public void execute(ExecutionContext executionContext) throws Exception {
		ProcessoManager processoManager = ComponentUtil.getComponent(ProcessoManager.NAME);
		StatusProcessoManager statusProcessoManager = ComponentUtil.getComponent(StatusProcessoManager.NAME);
		
		try {
			StatusProcesso status = statusProcessoManager.find(statusProcesso);
			Processo processo = processoManager.find(Integer.parseInt(executionContext.getContextInstance().getVariable("processo").toString(), 10));
//			processo.setStatusProcesso(status);
//			
//			processoEpaManager.update(processo);
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
}