package br.com.infox.epp.access.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.dao.UsuarioPerfilDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;

@Name(UsuarioPerfilManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class UsuarioPerfilManager extends
        Manager<UsuarioPerfilDAO, UsuarioPerfil> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioPerfilManager";

    @In
    private UsuarioLoginManager usuarioLoginManager;

    public List<PerfilTemplate> getPerfisPermitidos(Localizacao localizacao) {
        return getDao().getPerfisPermitidos(localizacao);
    }

    public List<UsuarioPerfil> listByUsuarioLogin(UsuarioLogin usuarioLogin) {
        return getDao().listByUsuarioLogin(usuarioLogin);
    }

    public UsuarioPerfil getByUsuarioLoginPerfilTemplateLocalizacao(
            UsuarioLogin usuarioLogin, PerfilTemplate perfilTemplate,
            Localizacao localizacao) {
        return getDao().getByUsuarioLoginPerfilTemplateLocalizacao(
                usuarioLogin, perfilTemplate, localizacao);
    }

    public void removeByUsuarioPerfilTemplateLocalizacao(
            UsuarioLogin usuarioLogin, PerfilTemplate perfilTemplate,
            Localizacao localizacao) throws DAOException {
        UsuarioPerfil usuarioPerfil = getDao().getByUsuarioLoginPerfilTemplateLocalizacao(
                usuarioLogin, perfilTemplate, localizacao);
        remove(usuarioPerfil);
        if (listByUsuarioLogin(usuarioLogin).isEmpty()) {
            usuarioLogin.setAtivo(Boolean.FALSE);
            usuarioLoginManager.update(usuarioLogin);
        }
    }

}
