package br.com.infox.ibpm.dao;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

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
	
}
