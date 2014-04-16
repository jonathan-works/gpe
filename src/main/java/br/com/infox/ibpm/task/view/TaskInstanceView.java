package br.com.infox.ibpm.task.view;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static br.com.infox.ibpm.process.definition.variable.constants.VariableConstants.DEFAULT_PATH;
import static java.text.MessageFormat.format;

import java.io.Serializable;
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

import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
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
        form.setHome(TaskInstanceHome.NAME);
        Template buttons = new Template();
        buttons.setId("empty");
        form.setButtons(buttons);
        form.setFormId(TaskInstanceView.NAME);

        TaskController taskController = taskInstance.getTask().getTaskController();
        if (taskController != null) {
            List<VariableAccess> list = taskController.getVariableAccesses();

            for (VariableAccess var : list) {
                final boolean isWritable = var.isWritable();
                final boolean isReadable = var.isReadable();
                if (isReadable && !isWritable) {
                    String[] tokens = var.getMappedName().split(":");
                    VariableType type = VariableType.valueOf(tokens[0]);
                    String name = tokens[1];
                    FormField ff = new FormField();
                    ff.setFormId(form.getFormId());
                    ff.setFormHome(form.getHomeName());
                    ff.setId(name);
                    ff.setRequired(var.isRequired() + "");
                    ff.setLabel(var.getVariableName());
                    Object value = taskInstance.getVariable(name);
                    Map<String, Object> properties = ff.getProperties();

                    properties.put("pagePath", type.getPath());
                    switch (type) {
                        case EDITOR:
                        {
                            ff.setType(type.name());
                            properties.put("pagePath", format(DEFAULT_PATH,"textEditComboReadonly"));
                            if (value != null) {
                                ProcessoDocumento processoDocumento = processoDocumentoManager().find((Integer) value);
                                if (processoDocumento != null) {
                                    properties.put("modeloDocumentoRO", processoDocumento.getProcessoDocumentoBin().getModeloDocumento());
                                    properties.put("tipoProcessoDocumentoRO", processoDocumento.getTipoProcessoDocumento());
                                }
                            }
                        }
                            break;
                        case MONETARY:
                        {
                            ff.setType(type.name());
                            ff.setValue(format("{0,number,0.00}", value));
                        }
                            break;
                        case ENUMERATION:
                        {
                            ff.setType(VariableType.STRING.name());
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
                        }
                            break;
                        default:
                        {
                            ff.setType(type.name());
                            ff.setValue(value);
                        }
                            break;
                    }
                    
                    properties.put("readonly", !var.isWritable());
                    if (value == null && !var.isWritable()
                            && VariableType.EDITOR.equals(type)) {
                        properties.put("rendered", false);
                    }
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
