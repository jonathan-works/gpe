package br.com.infox.ibpm.task.view;

import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.text.MessageFormat;
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
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.process.definition.variable.constants.VariableConstants;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.ibpm.variable.FragmentConfiguration;
import br.com.infox.ibpm.variable.FragmentConfigurationCollector;
import br.com.infox.ibpm.variable.dao.ListaDadosSqlDAO;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;
import br.com.infox.ibpm.variable.type.ValidacaoDataEnum;
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
    public static final String FRAME_URL_PREFIX = "WEB-INF_taskpages_";

    private Form form;

    private TaskInstance taskInstance;
    
    @Unwrap
    public Form getTaskForm() {
        getTaskInstance();
        if (form != null || taskInstance == null) {
            return form;
        }
        TaskController taskController = taskInstance.getTask().getTaskController();
        List<VariableAccess> list = null;
        if (taskController != null) {
            list = taskController.getVariableAccesses();
        }
        Template buttons = new Template();
        form = new Form();
        form.setHome(TaskInstanceHome.NAME);
        form.setFormId("taskInstance");
        buttons.setId(TASK_BUTTONS);
        form.setButtons(buttons);
        addVariablesToForm(list);
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
                if (var.isReadable() && var.isWritable() && !var.getAccess().hasAccess("hidden")) {
                    String[] tokens = var.getMappedName().split(":");
                    VariableType type = VariableType.valueOf(tokens[0]);
                    if (VariableType.TASK_PAGE.equals(type)){
                        continue;
                    }
                    String name = tokens[1];
                    Object variable = JbpmUtil.getProcessVariable(name + "Modelo");
                    if (variable != null) {
                        FormField ff = new FormField();
                        ff.setFormId(form.getFormId());
                        ff.setId(name + "Modelo");
                        ff.setLabel("Modelo");
                        ff.setType("comboModelos");
                        Map<String, Object> props = getInNewLineMap();
                        props.put("editorId", var.getVariableName() + "-" + taskInstance.getId());
                        props.put("pagePath", MessageFormat.format(VariableConstants.DEFAULT_PATH, "comboModelos"));
                        ff.setProperties(props);
                        form.getFields().add(ff);
                    }
                    String label = JbpmUtil.instance().getMessages()
                            .get(taskInstance.getProcessInstance().getProcessDefinition().getName() + ":" + name);
                    FormField ff = new FormField();
                    ff.setFormId(form.getFormId());
                    ff.setId(var.getVariableName() + "-" + taskInstance.getId());
                    ff.setRequired(var.isRequired() + "");
                    ff.setLabel(label);
                    ff.setType(type.name());
                    form.getFields().add(ff);
                    ff.getProperties().put("pagePath", type.getPath());
                    switch (type) {
                    case PAGE:
                        setPageProperties(name, ff, "seam", "url");
                        break;
                    case FRAME:
                        setPageProperties(FRAME_URL_PREFIX + name, ff, "xhtml", "urlFrame");
                        break;
                    case ENUMERATION_MULTIPLE:
                    case ENUMERATION: {
                        DominioVariavelTarefaManager dominioVariavelTarefaManager = (DominioVariavelTarefaManager) Component
                                .getInstance(DominioVariavelTarefaManager.NAME);
                        Integer id = Integer.valueOf(tokens[2]);
                        DominioVariavelTarefa dominio = dominioVariavelTarefaManager.find(id);
                        List<SelectItem> selectItens = new ArrayList<>();
                        if (dominio.isDominioSqlQuery()) {
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
                    case DATE: {
                        if (tokens.length < 3) {
                            ff.getProperties().put("tipoValidacao", ValidacaoDataEnum.L.name());
                        } else {
                            ff.getProperties().put("tipoValidacao", tokens[2]);
                        }
                    }
                        break;
                    case FRAGMENT: {
                        if (tokens.length >= 3) {
                            FragmentConfiguration fragmentConfiguration = BeanManager.INSTANCE.getReference(FragmentConfigurationCollector.class)
                                    .getByCode(tokens[2]);
                            ff.getProperties().put("fragmentPath", fragmentConfiguration.getPath());
                            ff.getProperties().put("config", fragmentConfiguration);
                        }
                    }
                        break;
                    default:
                        break;
                    }
                }
            }
        }
    }

    private void setPageProperties(String name, FormField ff, final String suffix,
            final String propType) {
        final String url = format("/{0}.{1}", name.replaceAll("_", "/"), suffix );
        ff.getProperties().put(propType, url);
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
