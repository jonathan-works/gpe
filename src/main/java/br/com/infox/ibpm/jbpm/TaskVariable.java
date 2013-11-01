package br.com.infox.ibpm.jbpm;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.ibpm.manager.ProcessoDocumentoManager;
import br.com.infox.util.constants.FloatFormatConstants;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

final class TaskVariable {
    
    private VariableAccess variableAccess;
    private String name;
    private String type;
    private Object variable;
    private TaskInstance taskInstance;
    
    private static final LogProvider LOG = Logging.getLogProvider(TaskVariable.class);
    
    public TaskVariable (VariableAccess variableAccess, TaskInstance taskInstance){
        this.variableAccess = variableAccess;
        this.type = variableAccess.getMappedName().split(":")[0];
        this.name = variableAccess.getMappedName().split(":")[1];
        this.taskInstance = taskInstance;
    }

    public String getName() {
        return name;
    }
    
    public String getMappedName(){
        return variableAccess.getMappedName();
    }

    public String getType() {
        return type;
    }
    
    public boolean isEditor(){
        return type.startsWith("textEditCombo") || type.equals("textEditSignature");
    }
    
    public boolean isForm(){
        return "form".equals(type);
    }
    
    public boolean isWritable(){
        return variableAccess.isWritable();
    }
    
    public boolean isMonetario(){
        return "numberMoney".equals(type) && (variable != null)
                && (variable.getClass().equals(Float.class));
    }
    
    public void formatVariableMonetaria(){
        variable = String.format(FloatFormatConstants._2F, variable);
    }

    public Object getVariable() {
        return variable;
    }

    public void setVariable(Object variable) {
        this.variable = variable;
    }
    
    public boolean hasVariable(){
        return variable != null;
    }
    
    public void setVariablesHome(){
        AbstractHome<?> home = ComponentUtil.getComponent(getName() + "Home");
        home.setId(getVariable());
    }
    
    public void retrieveHomes(){
        if (hasVariable()){
            setVariablesHome();
        }
    }
    
    private Object getConteudo(){
        Object variable = taskInstance.getVariable(getMappedName());
        if (isEditor()){
            Integer idProcessoDocumento = (Integer) variable;
            if (idProcessoDocumento != null){
                ProcessoDocumentoManager processoDocumentoManager = ComponentUtil.getComponent(ProcessoDocumentoManager.NAME);
                Object modeloDocumento = processoDocumentoManager.getModeloDocumentoByIdProcessoDocumento(idProcessoDocumento);
                if (modeloDocumento != null) {
                    return modeloDocumento;
                } else {
                    LOG.warn("ProcessoDocumento não encontrado: " + idProcessoDocumento);
                }
            }
        }
        return variable;
    }
    
    public void searchAndAssignConteudoToVariable(){
        variable = getConteudo();
    }
    
}
