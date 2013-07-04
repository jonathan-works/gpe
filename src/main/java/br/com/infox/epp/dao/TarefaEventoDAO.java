package br.com.infox.epp.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.TarefaEvento;
import br.com.itx.util.EntityUtil;

@Name(TarefaEventoDAO.NAME)
@AutoCreate
public class TarefaEventoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tarefaEventoDAO";
	
	public TarefaEvento getNextTarefaEvento(Processo processo, String task, String fluxo){
		String hql = "select et from ProcessoTarefaEvento o " +
						"inner join o.tarefaEvento et inner join et.tarefa t " +
						"where o.registrado = false and o.processo = :processo and " +
						"t.tarefa = :tarefa and t.fluxo.fluxo = :fluxo " +
						"order by et.evento";
		return (TarefaEvento) EntityUtil.createQuery(hql)
									.setParameter("processo", processo)
									.setParameter("tarefa", task)
									.setParameter("fluxo", fluxo).getSingleResult();
	}

}
