package br.com.infox.epp.dao;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.Agrupamento;
import br.com.infox.ibpm.entity.TarefaEvento;

@Name(AgrupamentoDAO.NAME)
@Scope(ScopeType.EVENT)
public class AgrupamentoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "agrupamentoDao";
	
	@SuppressWarnings("unchecked")
	public List<Agrupamento> getAgrupamentosByTarefaEvento(TarefaEvento tarefaEvento){
		String hql = "select a from Agrupamento a " +
						"inner join a.agrupamentoTarefaList at " +
						"where at.tarefaEvento = :tarefaEvento";
		return (List<Agrupamento>) entityManager.createQuery(hql)
				.setParameter("tarefaEvento", tarefaEvento).getResultList();
	}
	
}
