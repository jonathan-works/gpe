package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_EMAIL;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_ID_TASK_INSTANCE;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_LOGIN;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_EMAIL;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_LOGIN_TASK_INSTANCE;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_LOGIN_NAME;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.itx.util.EntityUtil;

@Name(UsuarioLoginDAO.NAME)
@AutoCreate
public class UsuarioLoginDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioLoginDAO";
	
	public UsuarioLogin getUsuarioLoginByEmail(String email){
	    Query query  = getEntityManager().createNamedQuery(USUARIO_BY_EMAIL);
	    query.setParameter(PARAM_EMAIL, email);
		return EntityUtil.getSingleResult(query);
	}
	
	public UsuarioLogin getUsuarioByLoginTaskInstance(Long idTaskInstance, String actorId) {
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put(PARAM_LOGIN, actorId);
		parameters.put(PARAM_ID_TASK_INSTANCE, idTaskInstance);
		return getNamedSingleResult(USUARIO_BY_LOGIN_TASK_INSTANCE, parameters);
	}
	
	public void inativarUsuario(UsuarioLogin usuario) {
		String hql = "UPDATE UsuarioLogin u SET u.ativo = false WHERE u.idUsuarioLogin = " + usuario.getIdUsuarioLogin().toString();
		getEntityManager().createQuery(hql).executeUpdate();
	}
	
	public UsuarioLogin getUsuarioLoginByLogin(String login) {
        Query query = getEntityManager().createNamedQuery(USUARIO_LOGIN_NAME);
        query.setParameter(PARAM_LOGIN, login);
        return EntityUtil.getSingleResult(query);
    }
	
}
