package br.com.infox.epp.processo.consulta.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.controller.AbstractController;

@Name(ConsultaExternaAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ConsultaExternaAction extends AbstractController {
    
    public static final String NAME = "consultaExternaAction";
    
}
