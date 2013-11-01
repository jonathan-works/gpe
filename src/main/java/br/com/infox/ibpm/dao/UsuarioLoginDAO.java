package br.com.infox.ibpm.dao;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.access.query.UsuarioLoginQuery;
import br.com.infox.core.dao.GenericDAO;
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
	
	public void inserirUsuarioParaPessoaFisica(String login, UsuarioLogin usuarioLogin){
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(UsuarioLogin.TABLE_NAME);
		sb.append(" (id_pessoa, ds_login, ds_senha, ds_assinatura_usuario, ds_cert_chain_usuario, " +
					"in_ldap, in_bloqueio, dt_expiracao_usuario, in_provisorio, in_twitter)");
		sb.append(" values (:idPessoa, :login, :senha, :assinatura, :cert_chain, :ldap, :bloqueio, null, :provisorio, :twitter)");
		Query query = entityManager.createNativeQuery(sb.toString());
		query.setParameter("idPessoa", usuarioLogin.getIdPessoa())
				.setParameter("login", login)
				.setParameter("senha", usuarioLogin.getSenha())
				.setParameter("assinatura", usuarioLogin.getAssinatura())
				.setParameter("cert_chain", usuarioLogin.getCertChain())
				.setParameter("ldap", usuarioLogin.getLdap())
				.setParameter("bloqueio", usuarioLogin.getBloqueio())
				.setParameter("provisorio", usuarioLogin.getProvisorio())
				.setParameter("twitter", usuarioLogin.getTemContaTwitter()).executeUpdate();
		entityManager.flush();
	}
	
	public UsuarioLogin getUsuarioLogin(UsuarioLogin usuarioLogin){
		return (UsuarioLogin) entityManager.find(UsuarioLogin.class, usuarioLogin.getIdPessoa());
	}
	
	public UsuarioLogin getUsuarioByLoginTaskInstance(Long idTaskInstance, String actorId) {
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put(UsuarioLoginQuery.PARAM_LOGIN, actorId);
		parameters.put(UsuarioLogin.PARAM_ID_TASK_INSTANCE, idTaskInstance);
		return getNamedSingleResult(UsuarioLoginQuery.USUARIO_BY_LOGIN_TASK_INSTANCE, parameters);
	}
	
	public void inativarUsuario(UsuarioLogin usuario) {
		String hql = "UPDATE UsuarioLogin u SET u.ativo = false WHERE u.idUsuario = " + usuario.getIdPessoa().toString();
		entityManager.createQuery(hql).executeUpdate();
	}
	
}
