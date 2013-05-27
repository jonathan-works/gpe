package br.com.infox.ibpm.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.dao.UsuarioLoginDAO;

@Name(UsuarioLoginManager.NAME)
@AutoCreate
public class UsuarioLoginManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioLoginManager";
	
	@In private UsuarioLoginDAO usuarioLoginDAO;
	
	public void inserirUsuarioParaPessoaFisicaCadastrada(String login, UsuarioLogin usuarioLogin){
		usuarioLoginDAO.inserirUsuarioParaPessoaFisica(login, usuarioLogin);
	}
	
	public UsuarioLogin getUsuarioLogin(UsuarioLogin usuarioLogin){
		return usuarioLoginDAO.getUsuarioLogin(usuarioLogin);
	}

}
