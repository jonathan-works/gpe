package br.com.infox.epp.fluxo.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.manager.NaturezaManager;

@Name(NaturezaCrudAction.NAME)
public class NaturezaCrudAction extends AbstractCrudAction<Natureza, NaturezaManager> {
    
    private static final long serialVersionUID = 1L;
    public static final String NAME = "naturezaCrudAction";

}
