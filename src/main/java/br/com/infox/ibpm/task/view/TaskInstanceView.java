package br.com.infox.ibpm.task.view;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static br.com.infox.ibpm.process.definition.variable.constants.VariableConstants.DEFAULT_PATH;
import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.util.JbpmUtil;
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
    private static final LogProvider LOG = Logging.getLogProvider(TaskInstanceView.class);
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
                    ff.setId(var.getVariableName());
                    ff.setRequired(var.isRequired() + "");
                    String label = JbpmUtil.instance().getMessages().get(taskInstance.getProcessInstance().getProcessDefinition().getName() + ":" + name);
                    ff.setLabel(label);
                    Object value = taskInstance.getVariable(var.getVariableName());
                    Map<String, Object> properties = ff.getProperties();

                    properties.put("pagePath", type.getPath());
                    switch (type) {
                        case EDITOR:
                        {
                            ff.setType(type.name());
                            properties.put("pagePath", format(DEFAULT_PATH,"textEditComboReadonly"));
                            if (value != null) {
                                try {
                                    Documento processoDocumento = documentoManager().find(Integer.parseInt(value.toString(), 10));
                                    if (processoDocumento != null) {
                                        properties.put("modeloDocumentoRO", processoDocumento.getProcessoDocumentoBin().getModeloDocumento());
                                        properties.put("tipoProcessoDocumentoRO", processoDocumento.getTipoProcessoDocumento());
                                    }
                                } catch (NumberFormatException e) {
                                    LOG.error("Identificador de Processo Documento inválido", e);
                                }
                            }
                        }
                            break;
                        case ENUMERATION:
                        {
                            ff.setType(VariableType.ENUMERATION.name());
                            ff.setValue(value);
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
                            break;
                        case FILE:
                        {
                            ff.setType(type.name());
                            ff.getProperties().put("readonly", true);
                            if (value != null) {
                                Documento documento = documentoManager().find(value);
                                ff.setValue(documento.getDescricao());
                                ff.getProperties().put("classificacaoDocumento", documento.getTipoProcessoDocumento().getTipoProcessoDocumento());
                            } else {
                                ff.setValue("(sem anexo)");
                                ff.getProperties().put("classificacaoDocumento", "(sem anexo)");
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

    private DocumentoManager documentoManager() {
        return ComponentUtil.getComponent(DocumentoManager.NAME);
    }
}
