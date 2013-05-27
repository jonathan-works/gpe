package br.com.infox.ibpm.home;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.persistence.Query;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Node.NodeType;
import org.richfaces.event.ItemChangeEvent;

import br.com.infox.ibpm.entity.Agrupamento;
import br.com.infox.ibpm.entity.Tarefa;
import br.com.infox.ibpm.entity.TarefaEvento;
import br.com.infox.ibpm.entity.TarefaEventoAgrupamento;
import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.infox.ibpm.jbpm.fitter.NodeFitter;
import br.com.infox.ibpm.jbpm.handler.TaskHandler;
import br.com.infox.ibpm.jbpm.handler.TransitionHandler;
import br.com.infox.ibpm.type.TarefaEventoEnum;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(TarefaEventoHome.NAME)
public class TarefaEventoHome extends AbstractTarefaEventoHome<TarefaEvento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tarefaEventoHome";
	private List<Tarefa> tarefaOrigemList;
	private List<Agrupamento> agrupamentos;
	private List<Agrupamento> registrados;
	private Boolean enableEvents = false;
	
	public static TarefaEventoHome instance() {
		return ComponentUtil.getComponent(TarefaEventoHome.NAME);
	}
		
	public TarefaEventoEnum[] getTarefaEventoItems() {
		if(tarefaEventoItems == null || tarefaEventoItems.length == 0) {
			if(ProcessBuilder.instance().getNodeFitter().getCurrentNode().getNodeType() == NodeType.Task) {
				if(isManaged()) {
					return TarefaEventoEnum.values();
				}
				String s = "select o from TarefaEvento o where o.tarefa = :tarefa";
				Query q = getEntityManager().createQuery(s).setParameter("tarefa", getTarefaAtual());
				List<TarefaEventoEnum> listEnum = new ArrayList<TarefaEventoEnum>();
				TarefaEventoEnum[] tee = TarefaEventoEnum.values();
				for (int i=0;i<tee.length;i++) {
					listEnum.add(tee[i]);
				}
				for (TarefaEvento te : (List<TarefaEvento>) q.getResultList()) {
					if (te.getEvento() == TarefaEventoEnum.RT) {
						listEnum.remove(TarefaEventoEnum.RT);
					} else if (te.getEvento() == TarefaEventoEnum.ST) {
						listEnum.remove(te.getEvento());
					}
				}
				tarefaEventoItems = new TarefaEventoEnum[listEnum.size()];
				int count = 0;
				for (TarefaEventoEnum tarefaEventoEnum : listEnum) {
					tarefaEventoItems[count] = tarefaEventoEnum;
					count++;
				}
			} else {
				tarefaEventoItems = new TarefaEventoEnum[1];
				tarefaEventoItems[0] = TarefaEventoEnum.ET;
			}
		}
		return tarefaEventoItems;
	}

	public void carregarAgrupamentos() {
		if(instance.getEvento().equals(TarefaEventoEnum.ET)) {
			StringBuilder sb = new StringBuilder();
			sb.append("select distinct a from Agrupamento a inner join ")
			  .append("a.eventoAgrupamentoList eal where ")
			  .append("eal.evento.eventoList.size = 0 and ")
			  .append("eal.multiplo = false");
			agrupamentos = getEntityManager().createQuery(sb.toString())
											 .getResultList();
		} else {
			agrupamentos = getEntityManager().createQuery("select o from Agrupamento o")
											 .getResultList();
		}
		if (this.registrados == null) {
			this.registrados = new ArrayList<Agrupamento>();
		} else {
			this.registrados.clear();
		}
		for (TarefaEventoAgrupamento aet : instance.getTarefaEventoAgrupamentoList()) {
			registrados.add(aet.getAgrupamento()); 
		}
	}
	
	@Observer(NodeFitter.SET_CURRENT_NODE_EVENT)
	public void onSetCurrentNode() {
		setTab("search");
		newInstance();
		tarefaAtual = null;
		tarefaOrigemList = null;
		canRegister();
	}
	
	@Override
	public String update() {
		instance.getTarefaEventoAgrupamentoList().clear();
		getEntityManager().createQuery("delete from TarefaEventoAgrupamento " +
									   "tea where tea.tarefaEvento = :tarefaEvento")
						  .setParameter("tarefaEvento", instance)
						  .executeUpdate();
		for (Agrupamento ae : registrados) {
			TarefaEventoAgrupamento tea = new TarefaEventoAgrupamento();
			tea.setAgrupamento(ae);
			tea.setTarefaEvento(instance);
			instance.getTarefaEventoAgrupamentoList().add(tea);
			getEntityManager().persist(tea);
		}
		EntityUtil.flush();
		FacesMessages.instance().add("Registros associados com sucesso!");
		return "updated";
	}
	
	public void setUpdate(TarefaEvento et) {
		setTab("form");
		instance = et;
	}

	public List<Agrupamento> getAgrupamentos() {
		if (agrupamentos == null) {
			carregarAgrupamentos();
		}
		return agrupamentos;
	}

	public void setAgrupamentos(List<Agrupamento> agrupamentos) {
		this.agrupamentos = agrupamentos;
	}

	public void setRegistrados(List<String> registrados) {
		if (this.registrados == null) {
			this.registrados = new ArrayList<Agrupamento>();
		} else {
			this.registrados.clear();
		}
		for (String s : registrados) {
			for (Agrupamento ae : agrupamentos) {
				if (ae.getAgrupamento().equals(s)) {
					this.registrados.add(ae);
					break;
				}
			}
		}
	}

	public List<Agrupamento> getRegistrados() {
		return registrados;
	}
	
	private void canRegister() {
		TaskHandler task = ProcessBuilder.instance().getTaskFitter().getCurrentTask();
		if(task == null) {
			enableEvents = false;
			return;
		}
		StringBuilder count = new StringBuilder();
		count.append("select count(t) from Tarefa t ")
			 .append("where t.fluxo.fluxo = :fluxo and t.tarefa = :nomeTarefa");
		Query q = getEntityManager().createQuery(count.toString());
		q.setParameter("nomeTarefa", task.getTask().getName());
		q.setParameter("fluxo", task.getTask().getProcessDefinition().getName());
		if((Long) q.getSingleResult() == 0) {
			enableEvents = false;
			return;
		}
		enableEvents = true;
	}
	
	public Boolean getEnableEvents() {
		return enableEvents;
	}

	public void setEnableEvents(Boolean enableEvents) {
		this.enableEvents = enableEvents;
	}

	public List<Tarefa> getTarefaOrigemList() {
		ProcessBuilder builder = ProcessBuilder.instance();
		if(tarefaOrigemList == null) {
			String fluxo = builder.getInstance().getName();
			tarefaOrigemList = new ArrayList<Tarefa>();
			String query = "select t from Tarefa t where t.tarefa = :tarefaOrigem " +
					"and t.fluxo.fluxo = :fluxo";
			Query q = getEntityManager().createQuery(query);
			for (TransitionHandler th : builder.getTransitionFitter().getArrivingTransitions()) {
				q.setParameter("tarefaOrigem", th.getTransition().getFrom().getName());
				q.setParameter("fluxo", fluxo);
				Tarefa t = EntityUtil.getSingleResult(q);
				if(t != null) {
					tarefaOrigemList.add(t);
				}
			}
		}
		return tarefaOrigemList;
	}

	public void setTarefaOrigemList(List<Tarefa> tarefaOrigemList) {
		this.tarefaOrigemList = tarefaOrigemList;
	}
	
	@Override
	public void processItemChange(ItemChangeEvent event) throws AbortProcessingException {
		if (event.getNewItemName().equals("agrupamentoTarefa")){
			carregarAgrupamentos();
		}
		super.processItemChange(event);
	}

}