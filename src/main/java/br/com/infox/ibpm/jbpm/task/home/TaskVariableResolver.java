package br.com.infox.ibpm.jbpm.task.home;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.Messages;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.infox.ibpm.jbpm.util.JbpmUtil;

final class TaskVariableResolver extends TaskVariable {
    
    private Object value;
    
    public TaskVariableResolver(VariableAccess variableAccess, TaskInstance taskInstance) {
        super(variableAccess, taskInstance);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void resolveWhenMonetario(){
        if ("numberMoney".equals(type) && value != null) {
            String val = String.valueOf(value);
            try {
                value = Float.parseFloat(val);
            } catch (NumberFormatException e) {
                value = Float.parseFloat(val.replace(".", "")
                        .replace(",", "."));
            }
        }
    }
    
    public void resolveWhenEditor(boolean assinar){
        Integer valueInt = salvarProcessoDocumento(assinar);
        if (valueInt != 0) {
            this.value = valueInt;
            atribuirValorDaVariavelNoContexto();
            if (assinar) {
                FacesMessages.instance().add(Messages.instance().get("assinatura.assinadoSucesso"));
            }
        }
    }
    
    public boolean isEditor(){
        return type.startsWith("textEditCombo") || type.equals("textEditSignature");
    }
    
    private String getLabel() {
        return JbpmUtil.instance().getMessages().get(name);
    }
    
    private Integer getIdDocumento(){
        if (taskInstance.getVariable(variableAccess.getMappedName()) != null) {
            return (Integer) taskInstance.getVariable(variableAccess.getMappedName());
        } else {
            return null;
        }
    }
    
    private Integer salvarProcessoDocumento(boolean assinar){
        return ProcessoHome.instance()
        .salvarProcessoDocumentoFluxo(value, getIdDocumento(), assinar, getLabel());
    }
    
    public void atribuirValorDaVariavelNoContexto(){
        Contexts.getBusinessProcessContext().set(variableAccess.getMappedName(), value);
    }
    
    private Object getValueFromMapaDeVariaveis(Map<String, Object> mapaDeVariaveis) {
        if (mapaDeVariaveis == null) {
            return null;
        }
        Set<Entry<String, Object>> entrySet = mapaDeVariaveis.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            if (entry.getKey().split("-")[0].equals(name)
                    && entry.getValue() != null) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public void assignValueFromMapaDeVariaveis(Map<String, Object> mapaDeVariaveis){
        value = getValueFromMapaDeVariaveis(mapaDeVariaveis);
    }
}
