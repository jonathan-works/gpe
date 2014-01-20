package br.com.infox.epp.ajuda.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.ajuda.entity.Ajuda;

@Name(AjudaCrudAction.NAME)
public class AjudaCrudAction extends AbstractCrudAction<Ajuda> {
    
    public static final String NAME = "ajudaCrudAction";

}
