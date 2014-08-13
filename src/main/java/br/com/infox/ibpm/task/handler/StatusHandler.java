package br.com.infox.ibpm.task.handler;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class StatusHandler implements ActionHandler {

	private static final long serialVersionUID = 1L;
	private String statusProcesso;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		executionContext.getTaskInstance().getId();

	}

}
