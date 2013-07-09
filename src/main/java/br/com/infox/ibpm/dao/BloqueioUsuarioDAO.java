package br.com.infox.ibpm.dao;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.BloqueioUsuario;
import br.com.itx.util.EntityUtil;

@Name(BloqueioUsuarioDAO.NAME)
@AutoCreate
public class BloqueioUsuarioDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "bloqueioUsuarioDAO";
	
	public BloqueioUsuario getBloqueioUsuarioMaisRecente(UsuarioLogin usuarioLogin){
		String hql = "select o from BloqueioUsuario o where o.dataBloqueio = " +
						"(select max(b.dataBloqueio) from BloqueioUsuario b where b.usuario = :usuario)";
		Query query = EntityUtil.createQuery(hql).setParameter("usuario", usuarioLogin);
		return EntityUtil.getSingleResult(query);
	}

}
