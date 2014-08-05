package br.com.infox.epp.access.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.dao.UsuarioPerfilDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioPerfil;

@Name(UsuarioPerfilManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class UsuarioPerfilManager extends Manager<UsuarioPerfilDAO, UsuarioPerfil> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioPerfilManager";
    
    public List<PerfilTemplate> getPerfisPermitidos(Localizacao localizacao) {
        return getDao().getPerfisPermitidos(localizacao);
    }
    
}
