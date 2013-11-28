package br.com.infox.epp.tarefa.dao;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.itx.util.EntityUtil;

@Name(TarefaDAO.NAME)
@AutoCreate
public class TarefaDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tarefaDAO";

	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<SelectItem> getPreviousNodes(Tarefa tarefa) {
		StringBuilder sql = new StringBuilder();
		sql.append("select max(nodeFrom.id_), nodeFrom.name_ ")
		    .append("from jbpm_transition t ")
		    .append("inner join jbpm_node nodeFrom ON (nodeFrom.id_=t.from_) ")
		    .append("inner join jbpm_task taskTo ON (taskTo.tasknode_=t.to_) ")
		    .append("inner join tb_tarefa_jbpm tjTo ON (tjTo.id_jbpm_task=taskTo.id_) ")
		    .append("where tjTo.id_tarefa=:idTarefa ")
		    .append("group by nodeFrom.name_");
		Query query = getEntityManager().createNativeQuery(sql.toString())
		            .setParameter("idTarefa", tarefa.getIdTarefa());
		
		List<SelectItem> previousTasksItems = new ArrayList<SelectItem>();
		previousTasksItems.add(new SelectItem(null,"Selecione a Tarefa Anterior"));

		for(Object[] obj : (List<Object[]>) query.getResultList()) {
			previousTasksItems.add(new SelectItem(obj[0], obj[1].toString()));
		}
		return previousTasksItems;
	}
	
	/**
	 * Popula a tabela tb_tarefa com todas as tarefas de todos os fluxos, 
	 * considerando como chave o nome da tarefa task.name_
	 */
	public void encontrarNovasTarefas() {
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
