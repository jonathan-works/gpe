package br.com.infox.ibpm.dao;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.TransactionRequiredException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.Tarefa;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.itx.util.EntityUtil;

@Name(TarefaDAO.NAME)
@AutoCreate
public class TarefaDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tarefaDAO";

	@SuppressWarnings("unchecked")
	public List<SelectItem> getPreviousTasks(Tarefa tarefa) {
		List<SelectItem> previousTasksItems = new ArrayList<SelectItem>();
		Session session = JbpmUtil.getJbpmSession();
		Tarefa t = entityManager.find(Tarefa.class, tarefa.getIdTarefa());
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT taskFrom.id_, taskFrom.name_ ")
		   .append("FROM public.jbpm_transition transFrom ")
		   .append("join public.jbpm_node nodeFrom on transFrom.from_ = nodeFrom.id_ ")
		   .append("join public.jbpm_node nodeTo on transFrom.to_ = nodeTo.id_ ")
		   .append("join jbpm_task taskFrom on taskFrom.tasknode_ = nodeFrom.id_ ")
		   .append("join jbpm_task taskTo on taskTo.tasknode_ = nodeTo.id_ ")
		   .append("where taskTo.id_ = :idTask order by 2");
		Query query = session.createSQLQuery(sql.toString());
		query.setParameter("idTask", t.getLastIdJbpmTask());
		previousTasksItems.add(new SelectItem(null,"Selecione a Tarefa Anterior"));
		String arg = "select t from Tarefa t where t.tarefa = :tarefa and t.fluxo = :fluxo";
		javax.persistence.Query q = entityManager.createQuery(arg);
		for(Object[] obj : (List<Object[]>) query.list()) {
			q.setParameter("tarefa", obj[1].toString());
			q.setParameter("fluxo", tarefa.getFluxo());
			Tarefa tarefaAnterior = EntityUtil.getSingleResult(q);
			previousTasksItems.add(new SelectItem(tarefaAnterior.getIdTarefa(), tarefaAnterior.getTarefa()));
		}
		return previousTasksItems;
	}
	
	/**
	 * Popula a tabela tb_tarefa com todas as tarefas de todos os fluxos, 
	 * considerando como chave o nome da tarefa task.name_
	 */
	public void encontrarNovasTarefas() throws IllegalStateException, TransactionRequiredException{
		String hql = "insert into public.tb_tarefa (id_fluxo, ds_tarefa) " +
						"select f.id_fluxo, t.name_ from jbpm_task t " +
							"inner join jbpm_processdefinition pd on (pd.id_ = t.processdefinition_) " +
							"inner join public.tb_fluxo f on (f.ds_fluxo = pd.name_) " +
							"inner join jbpm_node jn on (t.tasknode_ = jn.id_ and jn.class_ = 'K') " +
							"where pd.id_ = t.processdefinition_ and not exists " +
								"(select 1 from public.tb_tarefa where ds_tarefa = t.name_ and id_fluxo = f.id_fluxo) " +
							"group by f.id_fluxo, t.name_";
		EntityUtil.getEntityManager().createNativeQuery(hql).executeUpdate();
	}
	
}
