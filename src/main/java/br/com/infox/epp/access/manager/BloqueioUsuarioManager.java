package br.com.infox.epp.access.manager;

import java.util.Date;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.access.dao.BloqueioUsuarioDAO;
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Name(BloqueioUsuarioManager.NAME)
@AutoCreate
public class BloqueioUsuarioManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "bloqueioUsuarioManager";
	
	@In private BloqueioUsuarioDAO bloqueioUsuarioDAO;
	
	public Date getDataParaDesbloqueio(UsuarioLogin usuarioLogin){
		assert usuarioLogin.getBloqueio();
		BloqueioUsuario bloqueioUsuario = bloqueioUsuarioDAO.getBloqueioUsuarioMaisRecente(usuarioLogin);
		return bloqueioUsuario.getDataPrevisaoDesbloqueio();
	}
	
	public void desfazerBloqueioUsuario(UsuarioLogin usuarioLogin) {
		BloqueioUsuario bloqueioUsuario = bloqueioUsuarioDAO.getBloqueioUsuarioMaisRecente(usuarioLogin);
		assert bloqueioUsuario.getDataDesbloqueio() != null;
		bloqueioUsuarioDAO.desfazerBloqueioUsuario(bloqueioUsuario);
	}
	
	public boolean liberarUsuarioBloqueado(UsuarioLogin usuarioLogin){
		return getDataParaDesbloqueio(usuarioLogin).before(new Date());
	}
}
