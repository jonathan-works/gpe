package br.com.infox.epp.access.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.access.manager.UsuarioLocalizacaoManager;

@Name(PerfilCrudAction.NAME)
public class PerfilCrudAction extends AbstractCrudAction<UsuarioLocalizacao, UsuarioLocalizacaoManager> {

    public static final String NAME = "perfilCrudAction";
    private static final long serialVersionUID = 1L;

}
