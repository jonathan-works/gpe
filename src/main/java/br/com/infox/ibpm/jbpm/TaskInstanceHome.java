/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.jbpm;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.ibpm.component.tree.AutomaticEventsTreeHandler;
import br.com.infox.ibpm.component.tree.TarefasTreeHandler;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.home.Authenticator;
import br.com.infox.ibpm.home.ProcessoHome;
import br.com.infox.ibpm.jbpm.actions.ModeloDocumentoAction;
import br.com.infox.ibpm.search.Reindexer;
import br.com.infox.ibpm.search.SearchHandler;
import br.com.infox.ibpm.service.AssinaturaDocumentoService;
import br.com.infox.search.Indexer;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.Util;
import br.com.itx.exception.AplicationException;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;


@Name(TaskInstanceHome.NAME)
@Scope(ScopeType.CONVERSATION)
@Install(precedence=Install.FRAMEWORK)
@BypassInterceptors
public class TaskInstanceHome implements Serializable {

	private static final String MSG_USUARIO_SEM_ACESSO = "Você não pode mais efetuar transações " +
								"neste registro, verifique se ele não foi movimentado";

	private static final LogProvider log = Logging.getLogProvider(TaskInstanceHome.class);
	
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "taskInstanceHome"; 

	//TODO mudar o nome dessa variavel e o conteudo ficar #{HIDDEN_TRANSITION}
	//     criar um Factory para essa variável, de aplicaçao, retornando true
	private static final String OCCULT_TRANSITION = "#{true}";
	
	private TaskInstance taskInstance;
	
	private Map<String, Object> instance;

	private String variavelDocumento;
	
	private Long taskId;

	private List<Transition> availableTransitions;

	private List<Transition> leavingTransitions;

	private ModeloDocumento modeloDocumento;
	
	private String varName;
	
	private String name;
	
	private Boolean assinar = Boolean.FALSE;

	private TaskInstance currentTaskInstance;
	
	public static final String UPDATED_VAR_NAME = "isTaskHomeUpdated";
	
	public void createInstance() {
		taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
		if (instance == null && taskInstance != null) {
			instance = new HashMap<String, Object>();
			TaskController taskController = taskInstance.getTask().getTaskController();
			if (taskController != null) {
			List<VariableAccess> list = taskController.getVariableAccesses();
				for (VariableAccess var : list) {
					String type = var.getMappedName().split(":")[0];
					String name = var.getMappedName().split(":")[1];
					Object variable = JbpmUtil.instance().getConteudo(var, taskInstance);
					String modelo = (String) ProcessInstance.instance().getContextInstance().getVariable(name + "Modelo");
					Boolean assinado = Boolean.FALSE;
					Integer id = (Integer) taskInstance.getVariable(var.getMappedName());
					if (id != null){
						AssinaturaDocumentoService documentoService = new AssinaturaDocumentoService();
						assinado = documentoService.isDocumentoAssinado(id);
					}
					Boolean isEditor = JbpmUtil.isTypeEditor(type);
					if (isEditor){
						if ((id != null) && (!assinado) && var.isWritable()){
							ProcessoHome.instance().carregarDadosFluxo(id);
							instance.put(name, variable);
						}
					}
					if (modelo != null) {
						variavelDocumento = name;
						if (variable == null) {
							String s = modelo.split(",")[0].trim();
							modeloDocumento = EntityUtil.getEntityManager().find(
									ModeloDocumento.class, Integer.parseInt(s));
							variable = ModeloDocumentoAction.instance()
								.getConteudo(modeloDocumento);
							if (variable != null) {
								ProcessoHome.instance().getProcessoDocumentoBin().setModeloDocumento(variable.toString());
							}
						}
					}
					if (!isEditor){
						instance.put(name, variable);
					}
					
					if ("form".equals(type)) {
					 	varName = name;
					 	if (null != variable) {
						 	AbstractHome<?> home = ComponentUtil.getComponent(name + "Home");
						 	home.setId(variable);
					 	}
					}
				}
				//Atualizar as transições possiveis. Isso é preciso, pois as condições das transições são avaliadas antes 
				//deste metodo ser executado.
				updateTransitions();
			
			}
		}
	}

	public Map<String, Object> getInstance() {
		createInstance();
		return instance;
	}
	 
	//Método que será chamado pelo botão "Assinar Digitalmente"
	public void assinarDocumento(){
		assinar = Boolean.TRUE;
		this.update();
	}
	
	
	
	public Object getValueFromInstanceMap(String key) {
		if (instance == null) {
			return null;
		}
		Set<Entry<String,Object>> entrySet = instance.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			if (entry.getKey().split("-")[0].equals(key) && entry.getValue() != null) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	public void update() {
		modeloDocumento = null;
		taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
		
		if((taskInstance != null) && (taskInstance.getTask() != null)) {
			TaskController taskController = taskInstance.getTask().getTaskController();
			if (taskController != null) {
				List<VariableAccess> list = taskController.getVariableAccesses();
				for (VariableAccess var : list) {
					
					String type = var.getMappedName().split(":")[0];
					String name = var.getMappedName().split(":")[1];
					Object value = getValueFromInstanceMap(name);
					
					if (var.isWritable()) {
						if (JbpmUtil.isTypeEditor(type)){
							Integer idDoc = null;
							if (taskInstance.getVariable(var.getMappedName()) != null){
								idDoc = (Integer) taskInstance.getVariable(var.getMappedName());
							}
							String label = JbpmUtil.instance().getMessages().get(name);
							Integer valueInt = ProcessoHome.instance().salvarProcessoDocumentoFluxo(value, idDoc, assinar, label);
							if (valueInt != 0){
								value = valueInt;
								Contexts.getBusinessProcessContext().set(var.getMappedName(), value);
							}
							
							assinar = Boolean.FALSE;
						}
					}
				}
				Contexts.getBusinessProcessContext().flush();
				Util.setToEventContext(UPDATED_VAR_NAME, true);
				updateIndex();
                updateTransitions();
			}
		}
	}
	
	private Boolean checkAccess(){ 
		StringBuilder sb = new StringBuilder();
		sb.append("select o from Processo o where ");
		sb.append("o.idProcesso = :id ");
		sb.append("and o.actorId like :login");
		Query q = EntityUtil.createQuery(sb.toString());
		q.setParameter("id", ProcessoHome.instance().getInstance().getIdProcesso());
		q.setParameter("login", Authenticator.getUsuarioLogado().getLogin());
		if(q.getResultList().isEmpty()){
			FacesMessages.instance().clear();
			throw new AplicationException(MSG_USUARIO_SEM_ACESSO);
		}
		return Boolean.TRUE;
	}
	
	public void update(Object homeObject) {
		if(checkAccess()){
			canDoOperation();
			if (homeObject instanceof AbstractHome<?>) {
				AbstractHome<?> home = (AbstractHome<?>) homeObject;
				home.update();
			}
			update();
			AutomaticEventsTreeHandler.instance().registraEventos();
		}
	}

	public void persist(Object homeObject) {
		if(checkAccess()){
			canDoOperation();
			if (homeObject instanceof AbstractHome<?>) {
				AbstractHome<?> home = (AbstractHome<?>) homeObject;
				Object entity = home.getInstance();
				home.persist();
				Object idObject = EntityUtil.getEntityIdObject(entity);
				home.setId(idObject);
				if (varName != null) {
					instance.put(varName, idObject);
				}
				update();
			}
			AutomaticEventsTreeHandler.instance().registraEventos();
		}
	}

	public void canDoOperation() {
		if(currentTaskInstance == null) {
			currentTaskInstance = org.jboss.seam.bpm.TaskInstance.instance();
		}
		if(currentTaskInstance != null) {
			if(canOpenTask(currentTaskInstance.getId())) {
				return;
			}
			FacesMessages.instance().clear();
			throw new AplicationException(MSG_USUARIO_SEM_ACESSO);
		}
	}
	
	public void updateIndex() {
		String conteudo = Reindexer.getTextoIndexavel(SearchHandler.getConteudo(taskInstance));
		try {
			Indexer indexer = new Indexer();
			Map<String, String> fields = new HashMap<String, String>();
			fields.put("conteudo", conteudo);
			indexer.index(taskInstance.getId() + "", new HashMap<String, String>(), fields);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Observer(Event.EVENTTYPE_TASK_CREATE )
	public void setCurrentTaskInstance(ExecutionContext context) {
		try {
			this.currentTaskInstance = context.getTaskInstance();
		} catch (Exception ex) {
			String action = "atribuir a taskInstance corrente ao currentTaskInstance: ";
			log.warn(action, ex);
			throw new AplicationException(AplicationException.
					createMessage(action+ex.getLocalizedMessage(), 
								  "setCurrentTaskInstance()", 
								  "TaskInstanceHome", 
								  "BPM"));
		}
	}

	public String end(String transition) {
		if(checkAccess()){
			TaskInstance tempTask = org.jboss.seam.bpm.TaskInstance.instance();
			if (currentTaskInstance != null) {
				if(tempTask == null || tempTask.getId() != currentTaskInstance.getId()) {
					FacesMessages.instance().clear();
					throw new AplicationException(MSG_USUARIO_SEM_ACESSO);
				}
			}
			this.currentTaskInstance = null;
			ProcessoHome.instance().setIdProcessoDocumento(null);
			update();
			AutomaticEventsTreeHandler.instance().registraEventos();
			BusinessProcess.instance().endTask(transition);
			if (this.currentTaskInstance == null) {
				Util.setToEventContext("canClosePanel", true);
			} else {
				// verifica se o usuario é da localizacao/papel da swimlane da tarefa criada
				if (canOpenTask(this.currentTaskInstance.getId())) {
					setTaskId(currentTaskInstance.getId());
				} else {
					Util.setToEventContext("canClosePanel", true);
				}
			}
			Util.setToEventContext("taskCompleted", true);
		}
		return null;
	}
	
	/**
	 * Verifica se a tarefa destino da transição apareceria no painel do usuario
	 * @param currentTaskId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean canOpenTask(long currentTaskId) {
		JbpmUtil.getJbpmSession().flush();
		Events.instance().raiseEvent(TarefasTreeHandler.FILTER_TAREFAS_TREE);
		List resultList = EntityUtil.getEntityManager().createQuery(
				"select o.idTaskInstance from SituacaoProcesso o " +
				"where o.idTaskInstance = :ti")
			.setParameter("ti", currentTaskId)
			.getResultList();
		return resultList.size() > 0;
	}

	public void start(long taskId) {
		setTaskId(taskId);
		BusinessProcess.instance().startTask();
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
		BusinessProcess bp = BusinessProcess.instance();
		bp.setTaskId(taskId);
		taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
		if (taskInstance != null) {
			long processId = taskInstance.getProcessInstance().getId();
			bp.setProcessId(processId);
			updateTransitions();
			createInstance();
		}
	}
	
	public List<Transition> getTransitions() {
		if (taskId == null) {
			setTaskId(org.jboss.seam.bpm.TaskInstance.instance().getId());
		}
		List<Transition> list = new ArrayList<Transition>();
		if(availableTransitions != null && availableTransitions.size() == 0 && 
		   taskInstance != null) {
			updateTransitions();
		}
		if (availableTransitions == null) {
			return list;
		}
		// pega da definicao para garantir a mesma ordem do XML
		for (Transition transition : leavingTransitions) {
			//POG temporario devido a falha no JBPM de avaliar as avaliablesTransitions
			if (availableTransitions.contains(transition) && !hasOcculTransition(transition)) {
				list.add(transition);
			}
		}
		return list;
	}
	
	public static boolean hasOcculTransition(Transition transition) {
		return OCCULT_TRANSITION.equals(transition.getCondition());
	}
	
	public void updateTransitions() {
		availableTransitions = taskInstance.getAvailableTransitions();
		leavingTransitions = taskInstance.getTask().getTaskNode().getLeavingTransitions();
	}
	
	/**
	 * Refeita a combobox com as transições utilizando um f:selectItem
	 * pois o componente do Seam (s:convertEntity) estava dando problemas
	 * com as entidades do JBPM.
	 * @return Lista das transições.
	 */
	public List<SelectItem> getTranstionsSelectItems() {
		List<SelectItem> selectList = new ArrayList<SelectItem>();
		for(Transition t : getTransitions()) {
			selectList.add(new SelectItem(t.getName(), t.getName()));
		}
		return selectList;
	}

	public void clear() {
		this.instance = null;
		this.taskInstance = null;
	}

	public ModeloDocumento getModeloDocumento() {
		createInstance();
		return modeloDocumento;
	}

	public void setModeloDocumento(ModeloDocumento modelo) {
		this.modeloDocumento = modelo;
		instance.put(variavelDocumento, ModeloDocumentoAction.instance().getConteudo(modelo));
	}
	
	public String getHomeName() {
		return "taskInstanceHome";
	}

	public String getName() {
		return name;
	}

	public void setName(String transition) {
		this.name = transition;
	}

	public static TaskInstanceHome instance() {
		return (TaskInstanceHome) Component.getInstance(TaskInstanceHome.NAME);
	}

}