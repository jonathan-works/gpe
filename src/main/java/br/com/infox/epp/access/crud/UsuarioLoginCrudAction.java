package br.com.infox.epp.access.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.type.UsuarioEnum;

@Name(UsuarioLoginCrudAction.NAME)
public class UsuarioLoginCrudAction extends AbstractCrudAction<UsuarioLogin, UsuarioLoginManager> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioLoginCrudAction";

    @Override
    public void newInstance() {
        super.newInstance();
        final UsuarioLogin usuarioLogin = getInstance();
        usuarioLogin.setBloqueio(Boolean.FALSE);
        usuarioLogin.setProvisorio(Boolean.FALSE);
    }

    public UsuarioEnum[] getTiposDeUsuario() {
        return UsuarioEnum.values();
    }

}
