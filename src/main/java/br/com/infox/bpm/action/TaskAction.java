package br.com.infox.bpm.action;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Observer;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.ibpm.component.tree.AutomaticEventsTreeHandler;
import br.com.itx.exception.AplicationException;
import br.com.itx.util.EntityUtil;

/**
 * Classe abstrata que prove todos os m�todos comuns a exibi��o de uma 
 * tarefa do fluxo, tais como lista das transi��es ou m�todo para finalizar
 * a tarefa.
 * @author Daniel
 *
 */
public abstract class TaskAction implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final LogProvider LOG = Logging.getLogProvider(TaskAction.class);
	private String name;
	private List<Transition> avaliableTransitions;
	private TaskInstance taskInstance;
	private TaskInstance nextTaskInstance;

	/**
	 * Obt�m a lista de transi��es poss�veis para a taskInstance atual,
	 * verificando se a expression contida dentro do campo condition_
	 * est� true.
	 * @return Lista com as transi��es poss�veis.
	 */
	public List<Transition> getTransitions() {
		if(avaliableTransitions == null) {
			if(getTaskInstance() != null) {
				List<Transition> transitionsList = getTaskInstance().getAvailableTransitions();
				List<Transition> leavingTransitions = getTaskInstance().getTask()
														 .getTaskNode().getLeavingTransitions();
				/* Loop necess�rio para garantir a mesma ordem definida no xml do
				 * processDefinition.
				 */
				avaliableTransitions = new ArrayList<Transition>();
				for (Transition transition : leavingTransitions) {
					if(transitionsList.contains(transition)) {
						avaliableTransitions.add(transition);
					}
				}
			}
		}
		return avaliableTransitions;
	}
	
	public void updateTransitions() {
		avaliableTransitions = null;
		getTransitions();
	}
	
	/**
	 * Testa se � transi��o est� disponivel
	 * @param transitionName
	 * @return
	 */
	public boolean canTransit(String transitionName) {
		updateTransitions();
		for (Transition transition : getTransitions()) {
			if (transition.getName().equals(transitionName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param transition Nome da transi��o que deseja transitar o 
	 * processInstance
	 * @return
	 */
	public void end(String transition) {
		try {
			if (!canTransit(transition)) {
				//melhor lan�ar o AplicationException para dar rollback do que deixar o Jbpm lan�ar o erro.
				throw new AplicationException(
						MessageFormat.format("A transi��o {0} n�o est� dispon�vel para a tarefa {1}", 
								transition, getTaskInstance().getName()));				
			}	
			TaskTransitionAction tta = new TaskTransitionAction();
			tta.canEndTask(getTaskInstance());
			BusinessProcess.instance().endTask(transition);
			AutomaticEventsTreeHandler.instance().registraEventos();
			setTaskInstance(null);
			setAvaliableTransitions(null);
			setTaskInstance(tta.canSeeNextTaskInstance(nextTaskInstance));
		} catch (Exception e) {
			throw new AplicationException(
					MessageFormat.format("Erro ao seguir para a transi��o {0}: {1}", transition,
							e.getMessage()), e);
		}		
	}
	
	/**
	 * Refeita a combobox com as transi��es utilizando um f:selectItem
	 * pois o componente do Seam (s:convertEntity) estava dando problemas
	 * com as entidades do JBPM.
	 * @return Lista das transi��es.
	 */
	public List<SelectItem> getTranstionsSelectItems() {
		List<SelectItem> selectList = new ArrayList<SelectItem>();
		for(Transition t : getTransitions()) {
			selectList.add(new SelectItem(t.getName(), t.getName()));
		}
		return selectList;
	}

	/**
	 * Observer necess�rio para verificar antes de terminar a requisi��o
	 * se o usu�rio ir� poder continuar visualizando essa nova tarefa.
	 * @param context
	 */
	@Observer(Event.EVENTTYPE_TASK_CREATE )
	public void setNewTaskInstance(ExecutionContext context) {
		try {
			nextTaskInstance = context.getTaskInstance();
		} catch (Exception ex) {
			String action = "atribuir a taskInstance corrente ao currentTaskInstance: ";
			LOG.warn(action, ex);
			throw new AplicationException(AplicationException.
					createMessage(action+ex.getLocalizedMessage(), 
								  "setCurrentTaskInstance()", 
								  "TaskInstanceHome", 
								  "BPM"));
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setAvaliableTransitions(List<Transition> avaliableTransitions) {
		this.avaliableTransitions = avaliableTransitions;
	}

	public List<Transition> getAvaliableTransitions() {
		return avaliableTransitions;
	}

	public void setTaskInstance(TaskInstance taskInstance) {
		this.taskInstance = taskInstance;
	}

	public TaskInstance getTaskInstance() {
		if(taskInstance == null) {
			taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
		}
		return taskInstance;
	}
	
	public EntityManager getEntityManager() {
		return EntityUtil.getEntityManager();
	}
	
}