package br.com.infox.ibpm.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

@Name(SituacaoProcessoDAO.NAME)
@AutoCreate
public class SituacaoProcessoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "situacaoProcessoDAO";
	
	public Long getQuantidadeTarefasAtivasByTaskId(long taskId){
		String hql = "select count(o.idTaskInstance) from SituacaoProcesso o where o.idTaskInstance = :ti";
		return (Long) entityManager.createQuery(hql).setParameter("ti", taskId).getSingleResult();
	}

}
