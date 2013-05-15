package br.com.infox.epp.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.UsuarioLoginDAO;

@Name(UsuarioLoginManager.NAME)
@AutoCreate
public class UsuarioLoginManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioLoginManager";
	
	@In private UsuarioLoginDAO usuarioLoginDAO;
	
	public List<UsuarioLogin> getUsuariosQuePossuemRegistrosDeLog(){
		return usuarioLoginDAO.getUsuariosQuePossuemRegistrosDeLog();
	}

}
