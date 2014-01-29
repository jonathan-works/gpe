package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.Natureza;

@Name(NaturezaCrudAction.NAME)
public class NaturezaCrudAction extends AbstractCrudAction<Natureza> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    public static final String NAME = "naturezaCrudAction";

}
