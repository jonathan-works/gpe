package br.com.infox.ibpm.task.view;

import static br.com.infox.core.constants.WarningConstants.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.ibpm.variable.VariableHandler;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;

/**
 * Gera um formulario a partir do controller da tarefa atual (taskInstance) Para
 * a geracao correta o atributo mapped-name deve seguir o padrao:
 * 
 * tipo:nome_da_variavel
 * 
 * Onde: - tipo é o nome do componente de formulario para o campo -
 * nome_da_variavel é como sera armazenada no contexto. Serve também para gerar
 * o label (Nome da variavel)
 * 
 * Esse formulario contem apenas campos que possam ser escritos (access=write),
 * para os outros campos é usada a classe TaskInstanceView
 * 
 * @author luizruiz
 * 
 */

@Name(TaskInstanceForm.NAME)
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class TaskInstanceForm implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "taskInstaceForm";
    public static final String TASK_BUTTONS = "taskButtons";

    private Form form;

    private TaskInstance taskInstance;

    @SuppressWarnings(UNCHECKED)
    @Unwrap
    public Form getTaskForm() {
        getTaskInstance();
        if (form != null || taskInstance == null) {
            return form;
        }
        TaskController taskController = taskInstance.getTask().getTaskController();
        Template buttons = new Template();
        List<VariableAccess> list = null;
        if (taskController != null) {
            list = taskController.getVariableAccesses();
            for (VariableAccess var : list) {
                if (var.isReadable() && var.isWritable()) {
                    String[] tokens = var.getMappedName().split(":");
                    String type = tokens[0];
                    String name = tokens[1];
                    if ("form".equals(type)) {
                        String formName = name + "Form";
                        form = (Form) Component.getInstance(formName);
                        if (form != null) {
                            buttons.setId(TASK_BUTTONS);
                            form.setButtons(buttons);
                            form.setHome(name + "Home");
                        } else {
                            FacesMessages.instance().add(StatusMessage.Severity.INFO, "O form '"
                                    + formName + "' não foi encontrado.");
                        }
                        return form;
                    }
                }
            }
        }
        if (form == null) {
            form = new Form();
            form.setHome(TaskInstanceHome.NAME);
            form.setFormId("taskInstance");
            buttons.setId(TASK_BUTTONS);
            form.setButtons(buttons);
            addVariablesToForm(list);
        }
        return form;
    }

    /**
     * Adiciona as variaveis da list informada ao form que está sendo criado.
     * 
     * @param list - Lista das variavéis que desejam ser adicionadas ao form.
     */
    private void addVariablesToForm(List<VariableAccess> list) {
        if (list != null) {
            for (VariableAccess var : list) {
                if (var.isReadable() && var.isWritable()) {
                    String[] tokens = var.getMappedName().split(":");
                    String type = tokens[0];
                    String name = tokens[1];
                    Object variable = JbpmUtil.getProcessVariable(name
                            + "Modelo");
                    if (variable != null) {
                        FormField ff = new FormField();
                        ff.setFormId(form.getFormId());
                        ff.setId(name + "Modelo");
                        ff.setLabel("Modelo");
                        ff.setType("comboModelos");
                        Map<String, Object> props = getInNewLineMap();
                        props.put("editorId", var.getVariableName() + "-"
                                + taskInstance.getId());
                        ff.setProperties(props);
                        form.getFields().add(ff);
                    }
                    FormField ff = new FormField();
                    ff.setFormId(form.getFormId());
                    ff.setId(var.getVariableName() + "-" + taskInstance.getId());
                    ff.setRequired(var.isRequired() + "");
                    ff.setLabel(VariableHandler.getLabel(name));
                    ff.setType(type);
                    form.getFields().add(ff);
                    if ("page".equals(type) || "frame".equals(type)) {
                        String url = name.replaceAll("_", "/");
                        url = "/" + url
                                + ("page".equals(type) ? ".seam" : ".xhtml");
                        String urlParamName = "page".equals(type) ? "url" : "urlFrame";
                        Map<String, Object> props = new HashMap<String, Object>();
                        props.put(urlParamName, url);
                        ff.setProperties(props);
                    } else if ("enumeracao".equals(type)) {
                        DominioVariavelTarefaManager dominioVariavelTarefaManager = (DominioVariavelTarefaManager) Component.getInstance(DominioVariavelTarefaManager.NAME);
                        Integer id = Integer.valueOf(tokens[2]);
                        DominioVariavelTarefa dominio = dominioVariavelTarefaManager.find(id);

                        String[] itens = dominio.getDominio().split(";");
                        List<SelectItem> selectItens = new ArrayList<>();
                        for (String item : itens) {
                            String[] pair = item.split("=");
                            selectItens.add(new SelectItem(pair[0], pair[1]));
                        }
                        ff.getProperties().put("items", selectItens);
                    }
                }
            }
        }
    }

    private void getTaskInstance() {
        TaskInstance newInstance = org.jboss.seam.bpm.TaskInstance.instance();
        if (newInstance == null || !newInstance.equals(taskInstance)) {
            form = null;
        }
        taskInstance = newInstance;
    }

    public Map<String, Object> getInNewLineMap() {
        Map<String, Object> mapProperties = new HashMap<String, Object>();
        mapProperties.put("inNewLine", "true");
        return mapProperties;
    }

}
