package br.com.infox.epp.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.Processo;

@Name(ProcessoTarefaEventoDAO.NAME)
@AutoCreate
public class ProcessoTarefaEventoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoTarefaEventoDAO";

	public void destroyProcessoTarefaEvento(Processo processo, String task, String fluxo) {
		String hql = "delete from ProcessoTarefaEvento o " +
						"where o.processo = :processo and exists " +
							"(select 1 from TarefaEvento et " +
							"inner join et.tarefa t where et = o.tarefaEvento and " +
							"t.tarefa = :tarefa and t.fluxo.fluxo = :fluxo)";
		entityManager.createQuery(hql).setParameter("processo", processo)
						.setParameter("tarefa", task)
						.setParameter("fluxo", fluxo)
						.executeUpdate();
	}

}
