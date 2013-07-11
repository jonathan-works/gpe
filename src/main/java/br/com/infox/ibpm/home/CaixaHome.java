package br.com.infox.ibpm.home;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.ibpm.component.tree.TarefasTreeHandler;
import br.com.infox.ibpm.entity.Caixa;
import br.com.infox.ibpm.entity.Tarefa;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.actions.JbpmEventsHandler;
import br.com.infox.ibpm.manager.TarefaManager;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(CaixaHome.NAME)
public class CaixaHome extends AbstractCaixaHome<Caixa> {

	public static final String NAME = "caixaHome";
	public static final String ADD_CAIXA_EVENT = "addCaixaEvent";
	private static final long serialVersionUID = 1L;
	private String nomeNovaCaixa;
	private Integer idTarefaAnterior;
	private Integer idTarefaSearch;
	
	@In private TarefaManager tarefaManager;
	
	@Override
	protected boolean beforePersistOrUpdate() {
		if(idTarefaAnterior != null) {
			instance.setTarefaAnterior(getEntityManager().find(Tarefa.class, idTarefaAnterior));
		}else{
			instance.setTarefaAnterior(null);
		}
		
		return super.beforePersistOrUpdate();
	}

	public List<SelectItem> getPreviousTasks() {
		return getPreviousTasks(instance.getTarefa());
	}
	
	public List<SelectItem> getPreviousTasks(Integer idTarefa) {
		return getPreviousTasks(EntityUtil.find(Tarefa.class, idTarefa));
	}
	
	@SuppressWarnings("unchecked")
    public List<SelectItem> getPreviousTasks(Tarefa tarefa) {
		List<SelectItem> previousTasksItems = new ArrayList<SelectItem>();
		Session session = JbpmUtil.getJbpmSession();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT taskFrom.id_, taskFrom.name_ ")
		   .append("FROM public.jbpm_transition transFrom ")
		   .append("join public.jbpm_node nodeFrom on transFrom.from_ = nodeFrom.id_ ")
		   .append("join public.jbpm_node nodeTo on transFrom.to_ = nodeTo.id_ ")
		   .append("join jbpm_task taskFrom on taskFrom.tasknode_ = nodeFrom.id_ ")
		   .append("join jbpm_task taskTo on taskTo.tasknode_ = nodeTo.id_ ")
		   .append("where taskTo.id_ = :idTask order by 2");
		Query query = session.createSQLQuery(sql.toString());
		Tarefa t = getEntityManager().find(Tarefa.class, tarefa.getIdTarefa());
		query.setParameter("idTask", t.getLastIdJbpmTask());
		previousTasksItems.add(new SelectItem(null,"Selecione a Tarefa Anterior"));
		String arg = "select t from Tarefa t where t.tarefa = :tarefa and t.fluxo = :fluxo";
		javax.persistence.Query q = getEntityManager().createQuery(arg);
		for(Object[] obj : (List<Object[]>) query.list()) {
			q.setParameter("tarefa", obj[1].toString());
			q.setParameter("fluxo", tarefa.getFluxo());
			Tarefa tarefaAnterior = EntityUtil.getSingleResult(q);

			previousTasksItems.add(new SelectItem(tarefaAnterior.getIdTarefa(), tarefaAnterior.getTarefa()));
		}
		return previousTasksItems;
	}

	
	public void setNomeNovaCaixa(String nomeNovaCaixa) {
		this.nomeNovaCaixa = nomeNovaCaixa;
	}

	public String getNomeNovaCaixa() {
		return nomeNovaCaixa;
	}
	
	@Override
	public void newInstance() {
		nomeNovaCaixa = null;
		super.newInstance();
	}
	
	public void addCaixa(int idTarefa) {
		instance = createInstance();
		instance.setNomeCaixa(nomeNovaCaixa);
		instance.setTarefa(getEntityManager().find(Tarefa.class, idTarefa));
		String persist = super.persist();
		if(persist != null && !"".equals(persist)) {
			EntityUtil.flush();
			JbpmEventsHandler.updatePostDeploy();
		}
		TarefasTreeHandler tree = getComponent("tarefasTree");
		tree.clearTree();
		getEntityManager().clear();
		newInstance();
	}
	
	public static CaixaHome instance() {
		return ComponentUtil.getComponent(NAME);
	}

	public Integer getIdTarefaSearch() {
		return idTarefaSearch;
	}

	public void setIdTarefaSearch(Integer idTarefaSearch) {
		this.idTarefaSearch = idTarefaSearch;
	}

	public Integer getIdTarefaAnterior() {
		if(isManaged()) {
			if(instance.getTarefaAnterior() != null) {
				idTarefaAnterior = instance.getTarefaAnterior().getIdTarefa();
			}
		}
		return idTarefaAnterior;
	}

	public void setIdTarefaAnterior(Integer idTarefaAnterior) {
		this.idTarefaAnterior = idTarefaAnterior;
	}
}