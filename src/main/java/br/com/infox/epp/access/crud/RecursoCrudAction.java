package br.com.infox.epp.access.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.Recurso;

@Name(RecursoCrudAction.NAME)
public class RecursoCrudAction extends AbstractCrudAction<Recurso> {
    
    public static final String NAME = "recursoCrudAction";
    
}
