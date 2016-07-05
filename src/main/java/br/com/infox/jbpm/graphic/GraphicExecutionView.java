package br.com.infox.jbpm.graphic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.jsf.util.JsfUtil;

@Named
@ViewScoped
public class GraphicExecutionView implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private GraphicExecutionService graphicExecutionService;
    @Inject
    private JsfUtil jsfUtil;
    
    private GraphImageBean selectedGraphImage;
    private Map<String, GraphImageBean> graphImageBeans;
    private TaskInstance taskInstance;
    
    public String getSvg() {
        graphImageBeans = new HashMap<>();
        String svg = graphicExecutionService.performGraphicExecution(taskInstance, graphImageBeans); 
        return svg;
    }

    public void onSelectGraphElement() {
        String key = jsfUtil.getRequestParameter("key");
        selectedGraphImage = graphImageBeans.get(key);
    }
    
    public void onCloseInformacoes() {
        selectedGraphImage = null;
    }
    
    public TaskInstance getTaskInstance() {
        return taskInstance;
    }
    
    public void setTaskInstance(TaskInstance taskInstance) {
        this.taskInstance = taskInstance;
    }

    public GraphImageBean getSelectedGraphImage() {
        return selectedGraphImage;
    }

}
