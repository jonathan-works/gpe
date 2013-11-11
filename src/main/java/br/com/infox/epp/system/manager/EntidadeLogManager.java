package br.com.infox.epp.system.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.EntidadeLogDAO;

@Name(EntidadeLogManager.NAME)
@AutoCreate
public class EntidadeLogManager extends GenericManager{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "entidadeLogManager";
	
	@In private EntidadeLogDAO entidadeLogDAO;
	
	public List<UsuarioLogin> getUsuariosQuePossuemRegistrosDeLog(){
		return entidadeLogDAO.getUsuariosQuePossuemRegistrosDeLog();
	}
	
	public List<String> getEntidadesQuePodemPossuirLog(){
		return entidadeLogDAO.getEntidadesQuePodemPossuirLog();
	}
	
}
