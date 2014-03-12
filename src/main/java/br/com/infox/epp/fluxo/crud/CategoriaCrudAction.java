package br.com.infox.epp.fluxo.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.manager.CategoriaManager;

@Name(CategoriaCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class CategoriaCrudAction extends AbstractCrudAction<Categoria, CategoriaManager> {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "categoriaCrudAction";

}
