package br.com.infox.epp.access.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;

@Name(UsuarioPerfilCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class UsuarioPerfilCrudAction extends AbstractCrudAction<UsuarioPerfil, UsuarioPerfilManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioPerfilCrudAction";
    
    private UsuarioLogin usuarioLogin;

    public void setUsuarioLogin(UsuarioLogin usuarioLogin) {
        this.usuarioLogin = usuarioLogin;
    }
    
    @Override
    protected boolean isInstanceValid() {
        getInstance().setUsuarioLogin(usuarioLogin);
        return super.isInstanceValid();
    }
    
    @Override
    protected void afterSave(String ret) {
        newInstance();
        super.afterSave(ret);
    }

}
