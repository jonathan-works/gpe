package br.com.infox.epp.system.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.system.dao.EntidadeLogDAO;
import br.com.infox.epp.system.entity.EntityLog;

@Name(EntidadeLogManager.NAME)
@AutoCreate
@Stateless
public class EntidadeLogManager extends Manager<EntidadeLogDAO, EntityLog> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "entidadeLogManager";

    public List<UsuarioLogin> getUsuariosQuePossuemRegistrosDeLog() {
        return getDao().getUsuariosQuePossuemRegistrosDeLog();
    }

    public List<String> getEntidadesQuePodemPossuirLog() {
        return getDao().getEntidadesQuePodemPossuirLog();
    }

}
