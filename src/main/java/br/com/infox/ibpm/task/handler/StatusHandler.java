package br.com.infox.ibpm.task.handler;

import static java.text.MessageFormat.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoManager;
import br.com.infox.epp.tarefa.entity.ProcessoEpaTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoEpaTarefaManager;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.seam.util.ComponentUtil;

public class StatusHandler implements ActionHandler {

	private static final long serialVersionUID = 1L;
	private Integer statusProcesso;

	public StatusHandler(String s) {
		Pattern pattern = Pattern.compile("<statusProcesso>(\\d+)</statusProcesso>");
		Matcher matcher = pattern.matcher(s);
		if (matcher.find()) {
			String status = matcher.group(1);
			this.statusProcesso = Integer.parseInt(status);
		}
	}
	
	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		
		ProcessoEpaTarefaManager processoEpaTarefaManager = ComponentUtil.getComponent(ProcessoEpaTarefaManager.NAME);
		ProcessoEpaManager processoEpaManager = ComponentUtil.getComponent(ProcessoEpaManager.NAME);
		StatusProcessoManager statusProcessoManager = ComponentUtil.getComponent(StatusProcessoManager.NAME);
		
		ProcessoEpaTarefa processoEpaTarefa = processoEpaTarefaManager.getByTaskInstance(executionContext.getTaskInstance().getId());
		
		StatusProcesso status = statusProcessoManager.find(statusProcesso);
		ProcessoEpa processoEpa = processoEpaTarefa.getProcessoEpa();
		processoEpa.setStatusProcesso(status);
		
		processoEpaManager.update(processoEpa);
		String varName = format("{0}:{1}", VariableType.STRING, "statusProcesso");
		
		ContextInstance contextInstance = executionContext.getProcessInstance().getContextInstance();
		Token token = executionContext.getToken();
		String nome = status.getNome();
		if (contextInstance.hasVariable(varName, token)) {
			contextInstance.setVariable(varName, nome, token);
		} else {
			contextInstance.createVariable(varName, nome, token);
		}
	}
}