package br.com.infox.epp.access.manager;

import java.util.Date;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Name(UsuarioLoginManager.NAME)
@AutoCreate
public class UsuarioLoginManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioLoginManager";
	
	@In private UsuarioLoginDAO usuarioLoginDAO;
	
	public boolean usuarioExpirou(final UsuarioLogin usuarioLogin){
	    boolean result = Boolean.FALSE;
	    if (usuarioLogin != null) {
	        final Date dataExpiracao = usuarioLogin.getDataExpiracao();
            result = usuarioLogin.getProvisorio() && dataExpiracao != null && dataExpiracao.before(new Date());
	    }
		return result;
	}
	
	public void inativarUsuario(final UsuarioLogin usuario){
		usuarioLoginDAO.inativarUsuario(usuario);
	}
	
	public UsuarioLogin getUsuarioLoginByEmail(final String email){
		return usuarioLoginDAO.getUsuarioLoginByEmail(email);
	}
	
	public UsuarioLogin getUsuarioLoginByLogin(final String login) {
	    return usuarioLoginDAO.getUsuarioLoginByLogin(login);
	}

}
