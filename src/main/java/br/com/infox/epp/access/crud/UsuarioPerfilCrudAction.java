package br.com.infox.epp.access.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;

@Name(UsuarioPerfilCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class UsuarioPerfilCrudAction extends AbstractCrudAction<UsuarioPerfil, UsuarioPerfilManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioPerfilCrudAction";

}
