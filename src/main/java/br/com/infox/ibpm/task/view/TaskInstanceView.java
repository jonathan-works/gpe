package br.com.infox.ibpm.task.view;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.constants.FloatFormatConstants;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.ibpm.variable.VariableHandler;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;
import br.com.infox.seam.util.ComponentUtil;

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
 * Esse formulario contem apenas campos somente leitura (access=read), para os
 * outros campos é usada a classe TaskInstanceForm
 * 
 * @author luizruiz
 * 
 */

@Name(TaskInstanceView.NAME)
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class TaskInstanceView implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "taskInstanceView";

    private Form form;

    private TaskInstance taskInstance;

    @SuppressWarnings(UNCHECKED)
    @Unwrap
    public Form getTaskForm() {
        getTaskInstance();
        if (form != null || taskInstance == null) {
            return form;
        }
        form = new Form();
        form.setHome("taskInstanceHome");
        Template buttons = new Template();
        buttons.setId("empty");
        form.setButtons(buttons);
        form.setFormId("taskInstanceView");

        TaskController taskController = taskInstance.getTask().getTaskController();
        if (taskController != null) {
            List<VariableAccess> list = taskController.getVariableAccesses();

            for (VariableAccess var : list) {
                if (var.isReadable() && !var.isWritable()) {
                    String[] tokens = var.getMappedName().split(":");
                    String type = tokens[0];
                    String name = tokens[1];
                    FormField ff = new FormField();
                    ff.setFormId(form.getFormId());
                    ff.setFormHome(form.getHomeName());
                    ff.setId(var.getVariableName());
                    ff.setRequired(var.isRequired() + "");
                    ff.setLabel(VariableHandler.getLabel(name));
                    Object value = taskInstance.getVariable(var.getVariableName());
                    Map<String, Object> properties = new HashMap<String, Object>();
                    if (type.startsWith("textEdit")) {
                        ff.setType("textEditComboReadonly");
                        if (value != null) {
                            ProcessoDocumento processoDocumento = processoDocumentoManager().find((Integer) value);
                            if (processoDocumento != null) {
                                properties.put("modeloDocumentoRO", processoDocumento.getProcessoDocumentoBin().getModeloDocumento());
                                properties.put("tipoProcessoDocumentoRO", processoDocumento.getTipoProcessoDocumento());
                            }
                        }
                    } else if ("numberMoney".equalsIgnoreCase(type)) {
                        ff.setType(type);
                        ff.setValue(String.format(FloatFormatConstants.F2, value));
                    } else if ("enumeracao".equals(type)) {
                        ff.setType("default");
                        ff.setValue(value);
                        DominioVariavelTarefaManager dominioVariavelTarefaManager = (DominioVariavelTarefaManager) Component.getInstance(DominioVariavelTarefaManager.NAME);
                        Integer id = Integer.valueOf(tokens[2]);
                        DominioVariavelTarefa dominio = dominioVariavelTarefaManager.find(id);

                        String[] itens = dominio.getDominio().split(";");
                        for (String item : itens) {
                            String[] pair = item.split("=");
                            if (pair[0].equals(value)) {
                                ff.setValue(pair[1]);
                                break;
                            }
                        }
                    } else {
                        ff.setType(type);
                        ff.setValue(value);
                    }
                    properties.put("readonly", !var.isWritable());
                    if (value == null && !var.isWritable()
                            && "textEdit".equals(type)) {
                        properties.put("rendered", "false");
                    }
                    ff.setProperties(properties);
                    form.getFields().add(ff);
                }
            }
        }
        return form;
    }

    private void getTaskInstance() {
        TaskInstance newInstance = org.jboss.seam.bpm.TaskInstance.instance();
        if (newInstance == null || !newInstance.equals(taskInstance)) {
            form = null;
        }
        taskInstance = newInstance;
    }

    private ProcessoDocumentoManager processoDocumentoManager() {
        return ComponentUtil.getComponent(ProcessoDocumentoManager.NAME);
    }
}
