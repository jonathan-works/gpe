package br.com.infox.epp.access.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.Recurso;
import br.com.infox.epp.access.manager.RecursoManager;

@Name(RecursoCrudAction.NAME)
public class RecursoCrudAction extends AbstractCrudAction<Recurso, RecursoManager> {
    
    private static final long serialVersionUID = 1L;
    public static final String NAME = "recursoCrudAction";
    
}
