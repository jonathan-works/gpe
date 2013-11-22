package br.com.infox.epp.access.dao;

import java.util.Date;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.itx.util.EntityUtil;

@Name(BloqueioUsuarioDAO.NAME)
@AutoCreate
public class BloqueioUsuarioDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "bloqueioUsuarioDAO";
	
	public BloqueioUsuario getBloqueioUsuarioMaisRecente(UsuarioLogin usuarioLogin){
		String hql = "select o from BloqueioUsuario o where o.idBloqueioUsuario = " +
						"(select max(b.idBloqueioUsuario) from BloqueioUsuario b where b.usuario = :usuario)";
		Query query = EntityUtil.createQuery(hql).setParameter("usuario", usuarioLogin);
		return EntityUtil.getSingleResult(query);
	}
	
	public void desfazerBloqueioUsuario(BloqueioUsuario bloqueioUsuario) {
		desbloquearUsuario(bloqueioUsuario.getUsuario());
		gravarDesbloqueio(bloqueioUsuario);
	}
	
	private void desbloquearUsuario(UsuarioLogin usuarioLogin){
		String queryDesbloqueio = "update public.tb_usuario set in_bloqueio=false where id_usuario = :usuario";
		EntityUtil.getEntityManager().createNativeQuery(queryDesbloqueio)
			.setParameter("usuario", usuarioLogin.getIdPessoa())
			.executeUpdate();
	}
	
	private void gravarDesbloqueio(BloqueioUsuario bloqueioUsuario){
		String queryDataDesbloqueio = 
				"UPDATE BloqueioUsuario b SET b.dataDesbloqueio = :hoje " +
				"WHERE b.idBloqueioUsuario = :bloqueio";
		EntityUtil.getEntityManager().createQuery(queryDataDesbloqueio)
			.setParameter("hoje", new Date())
			.setParameter("bloqueio", bloqueioUsuario.getIdBloqueioUsuario())
			.executeUpdate();
	}

}
