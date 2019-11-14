package br.com.infox.ibpm.variable.components;

public interface TaskpageDefinition extends ParameterizedComponentDefinition {
    
    void setController(Class<? extends AbstractTaskPageController> controllerClass);
    
    Class<? extends AbstractTaskPageController> getControllerClass();

}
