package br.com.infox.ibpm.task.view;

import static br.com.infox.ibpm.process.definition.variable.constants.VariableConstants.DEFAULT_PATH;
import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.variable.FragmentConfiguration;
import br.com.infox.ibpm.variable.FragmentConfigurationCollector;
import br.com.infox.ibpm.variable.VariableDominioEnumerationHandler;
import br.com.infox.ibpm.variable.components.FrameDefinition;
import br.com.infox.ibpm.variable.components.VariableDefinitionService;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaSearch;
import br.com.infox.ibpm.variable.dao.ListaDadosSqlDAO;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
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
    
    private VariableDefinitionService variableDefinitionService = Beans.getReference(VariableDefinitionService.class);

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
                    Variable variable = new Variable(var, taskInstance);
                    VariableType type = VariableType.valueOf(var.getType());//FIXME ver aqui se esse type estpa certo
                    if (variable.type == VariableType.FRAME) {
                        FormField formField = createFormField(variable);
                        FrameDefinition frame = variableDefinitionService.getFrame(variable.name);
                        String url = frame.getXhtmlPath();
                        formField.getProperties().put("urlFrame", url);
                        formField.getProperties().put("readonly", true);
                        formField.getProperties().put("pagePath", variable.type.getPath());
                        form.getFields().add(formField);
                        continue;
                    } else if (variable.type == VariableType.TASK_PAGE || variable.value == null) {
                        continue;
                    }
                    
                    FormField ff = createFormField(variable);
                    Map<String, Object> properties = ff.getProperties();
                    properties.put("pagePath", variable.type.getPath());
                    
                    switch (variable.type) {
                        case EDITOR:
                        {
                            properties.put("pagePath", format(DEFAULT_PATH,"textEditComboReadonly"));
                            try {
                                Documento documento = documentoManager().find(Integer.parseInt(variable.value.toString(), 10));
                                if (documento != null) {
                                    properties.put("modeloDocumentoRO", documento.getDocumentoBin().getModeloDocumento());
                                    properties.put("tipoProcessoDocumentoRO", documento.getClassificacaoDocumento());
                                }
                            } catch (NumberFormatException e) {
                                LOG.error("Identificador de Documento inválido", e);
                            }
                        }
                        break;
                        case ENUMERATION:
                        case ENUMERATION_MULTIPLE:
                        {
                            ff.setValue(variable.value);
                            DominioVariavelTarefaSearch dominioVariavelTarefaSearch = Beans.getReference(DominioVariavelTarefaSearch.class);
                            DominioVariavelTarefa dominio = dominioVariavelTarefaSearch.findByCodigo(VariableDominioEnumerationHandler.fromJson(var.getConfiguration()).getCodigoDominio());

                            List<SelectItem> selectItens = new ArrayList<>();
                            if (dominio.isDominioSqlQuery()){
                            	ListaDadosSqlDAO listaDadosSqlDAO = ComponentUtil.getComponent(ListaDadosSqlDAO.NAME);
                            	selectItens.addAll(listaDadosSqlDAO.getListSelectItem(dominio.getDominio(), taskInstance));
                            } else {
                            	String[] itens = dominio.getDominio().split(";");
                            	for (String item : itens) {
                            		String[] pair = item.split("=");
                            		selectItens.add(new SelectItem(pair[1], pair[0]));
                            	}
                            }
                            ff.getProperties().put("items", selectItens);
                        }
                        break;
                        case FILE:
                        {
                            Documento documento = documentoManager().find(variable.value);
                            ff.setValue(documento.getDescricao());
                            ff.getProperties().put("classificacaoDocumento", documento.getClassificacaoDocumento().getDescricao());
                        }
                        break;
                        case FRAGMENT:{
                            ff.setValue(variable.value);
                            if (variable.configuration != null) {
                                FragmentConfiguration fragmentConfiguration = Beans.getReference(FragmentConfigurationCollector.class).getByCode(variable.configuration);
                                Map<String, Object> map = ff.getProperties();
                                map.put("fragmentConfig", fragmentConfiguration);
                                map.put("fragmentPath", fragmentConfiguration.getPath());
                            }
                        }
                        break;
                        default:
                        {
                            ff.setValue(variable.value);
                        }
                        break;
                    }
                    
                    properties.put("readonly", true);
                    form.getFields().add(ff);
                }
            }
        }
        return form;
    }
    
    private FormField createFormField(Variable var) {
        FormField ff = new FormField();
        ff.setFormId(form.getFormId());
        ff.setFormHome(form.getHomeName());
        ff.setId(var.variableAccess.getVariableName());
        ff.setRequired(String.valueOf(var.variableAccess.isRequired()));
        ff.setLabel(var.variableAccess.getLabel());
        ff.setType(var.type.name());
        return ff;
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
    
    private static class Variable {
        VariableAccess variableAccess;
        String name;
        VariableType type;
        Object value;
        String configuration;
        
        public Variable(VariableAccess variableAccess, TaskInstance taskInstance) {
            this.variableAccess = variableAccess;
            String[] tokens = variableAccess.getMappedName().split(":");
            this.type = VariableType.valueOf(tokens[0]);
            this.name = tokens[1];
            this.value = taskInstance.getVariable(variableAccess.getVariableName());
            if (tokens.length == 3) {
                configuration = tokens[2];
            }
        }
    }
}