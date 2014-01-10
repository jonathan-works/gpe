package br.com.infox.epp.system.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.system.dao.EntidadeLogDAO;
import br.com.infox.epp.system.entity.EntityLog;

@Name(EntidadeLogManager.NAME)
@AutoCreate
public class EntidadeLogManager extends GenericManager{

	private static final long serialVersionUID = 1L;
    private static final Class<EntityLog> CLASS = EntityLog.class;
	public static final String NAME = "entidadeLogManager";
	
	@In private EntidadeLogDAO entidadeLogDAO;

    public List<EntityLog> findAll() {
        return findAll(CLASS);
    }
    
    public EntityLog find(Integer idEntityLog){
        return find(CLASS, idEntityLog);
    }

	public List<UsuarioLogin> getUsuariosQuePossuemRegistrosDeLog(){
		return entidadeLogDAO.getUsuariosQuePossuemRegistrosDeLog();
	}
	
	public List<String> getEntidadesQuePodemPossuirLog(){
		return entidadeLogDAO.getEntidadesQuePodemPossuirLog();
	}
	
}
