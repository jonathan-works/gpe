package br.com.infox.ibpm.home;

import javax.persistence.Query;

import br.com.infox.ibpm.entity.Tarefa;
import br.com.infox.ibpm.entity.TarefaEvento;
import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.infox.ibpm.jbpm.handler.TaskHandler;
import br.com.infox.ibpm.type.TarefaEventoEnum;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.EntityUtil;

public abstract class AbstractTarefaEventoHome<T> extends AbstractHome<TarefaEvento> {

	private static final long serialVersionUID = 1L;
	protected Tarefa tarefaAtual;
	protected TarefaEventoEnum[] tarefaEventoItems;
	protected static final String REGISTRA_EVENTO = "#{registraEventoAction.registra()}";
	
	public void setTarefaEventoIdTarefaEvento(Integer id) {
		setId(id);
	}

	public Integer getTarefaEventoIdTarefaEvento() {
		return (Integer) getId();
	}
		
	public String remove(TarefaEvento obj) {
		instance = obj;
		String remove = super.remove();
		if(remove != null && !"".equals(remove)) {
			refreshGrid("tarefaEventoGrid");
		}
		super.newInstance();
		return remove;
	}
	
	public Tarefa getTarefaAtual() {
		TaskHandler task = ProcessBuilder.instance().getCurrentTask();
		if(tarefaAtual != null || task == null || task.getTask() == null) {
			return tarefaAtual;
		}
		String count = "select t from Tarefa t where t.tarefa = :nomeTarefa and t.fluxo.fluxo = :fluxo";
		Query q = getEntityManager().createQuery(count);
		q.setParameter("nomeTarefa", task.getTask().getName());
		q.setParameter("fluxo", task.getTask().getProcessDefinition().getName());
		return tarefaAtual = EntityUtil.getSingleResult(q);
	}
	
	@Override
	public void newInstance() {
		tarefaEventoItems = null;
		super.newInstance();
	}
	
	@Override
	public String persist() {
		instance.setTarefa(getTarefaAtual());
		String persist = super.persist();
		if(persist != null && !"".equals(persist)) {
			tarefaEventoItems = null;
			refreshGrid("tarefaEventoGrid");
		}
		return persist;
	}
	
}