package br.com.infox.ibpm.variable.components;

import java.util.ArrayList;
import java.util.List;

import br.com.infox.core.util.StringUtil;

public class AnnotatedTaskpage implements TaskpageDefinition {
	
	private Taskpage taskpage;
	private Class<? extends AbstractTaskPageController> controllerClass;
	
	public AnnotatedTaskpage(Taskpage taskpage) {
		this.taskpage = taskpage;
	}

	@Override
	public String getId() {
		return taskpage.id();
	}

	@Override
	public String getName() {
		return taskpage.name();
	}

	@Override
	public String getDescription() {
		return StringUtil.isEmpty(taskpage.description()) ? null : taskpage.description(); 
	}

	@Override
	public String getXhtmlPath() {
		return taskpage.xhtmlPath();
	}

	@Override
	public List<ParameterDefinition> getParameters() {
		List<ParameterDefinition> definitions = new ArrayList<>();
		for(ParameterVariable parameter : taskpage.parameters()) {
			definitions.add(new AnnotatedParameter(parameter));
		}
		return definitions;
	}

	@Override
	public boolean isDisabled() {
		return taskpage.disabled();
	}
	
	@Override
	public void setController(Class<? extends AbstractTaskPageController> controllerClass) {
	    this.controllerClass = controllerClass;
	}

	@Override
    public Class<? extends AbstractTaskPageController> getControllerClass() {
        return controllerClass;
    }

}
