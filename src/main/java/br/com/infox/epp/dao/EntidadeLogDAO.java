package br.com.infox.epp.dao;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.core.dao.GenericDAO;

@Name(EntidadeLogDAO.NAME)
@AutoCreate
public class EntidadeLogDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "entidadeLogDAO";
	
	@SuppressWarnings("unchecked")
	public List<UsuarioLogin> getUsuariosQuePossuemRegistrosDeLog(){
		String hql = "select o from UsuarioLogin o " +
				"where o.entityLogList.size > 0 " +
				"order by o.idPessoa";
		return (List<UsuarioLogin>) entityManager.createQuery(hql).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getEntidadesQuePodemPossuirLog(){
		String hql = "select distinct o.nomeEntidade as entidade from EntityLog o order by o.nomeEntidade";
		return (List<String>) entityManager.createQuery(hql).getResultList();
	}
	
}
