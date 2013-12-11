package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.UsuarioLoginQuery.*;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.query.UsuarioLoginQuery;
import br.com.itx.util.EntityUtil;

@Name(UsuarioLoginDAO.NAME)
@AutoCreate
public class UsuarioLoginDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioLoginDAO";
	
	public UsuarioLogin getUsuarioLoginByCpf(String cpf){
		String hql = "select o from UsuarioLogin o where o.cpf = :cpf";
		Query query = EntityUtil.createQuery(hql).setParameter("cpf", cpf);
		return EntityUtil.getSingleResult(query);
	}
	
	public UsuarioLogin getUsuarioLoginByLogin(String login){
		String hql = "select o from UsuarioLogin o where o.login = :login";
		Query query = EntityUtil.createQuery(hql).setParameter("login", login);
		return EntityUtil.getSingleResult(query);
	}
	
	public UsuarioLogin getUsuarioLoginByEmail(String email){
		String hql = "select o from UsuarioLogin o where o.email = :email";
		Query query = EntityUtil.createQuery(hql).setParameter("email", email);
		return EntityUtil.getSingleResult(query);
	}
	
	public UsuarioLogin getUsuarioLogin(UsuarioLogin usuarioLogin){
		return (UsuarioLogin) getEntityManager().find(UsuarioLogin.class, usuarioLogin.getIdUsuarioLogin());
	}
	
	public UsuarioLogin getUsuarioByLoginTaskInstance(Long idTaskInstance, String actorId) {
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put(UsuarioLoginQuery.PARAM_LOGIN, actorId);
		parameters.put(PARAM_ID_TASK_INSTANCE, idTaskInstance);
		return getNamedSingleResult(UsuarioLoginQuery.USUARIO_BY_LOGIN_TASK_INSTANCE, parameters);
	}
	
	public void inativarUsuario(UsuarioLogin usuario) {
		String hql = "UPDATE UsuarioLogin u SET u.ativo = false WHERE u.idUsuarioLogin = " + usuario.getIdUsuarioLogin().toString();
		getEntityManager().createQuery(hql).executeUpdate();
	}
	
	public UsuarioLogin checkUserByLogin(String login) {
        Query query = getEntityManager().createNamedQuery(USUARIO_LOGIN_NAME);
        query.setParameter(PARAM_LOGIN, login);
        return EntityUtil.getSingleResult(query);
    }
	
}
