package br.com.infox.epp.ajuda.home;

import java.io.Serializable;

import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.ajuda.manager.AjudaManager;
import br.com.infox.epp.cdi.seam.ContextDependency;

@Name(AjudaReindexer.NAME)
@Scope(ScopeType.CONVERSATION)
@ContextDependency
public class AjudaReindexer implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "ajudaReindexer";

    @Inject
    private AjudaManager ajudaManager;

    public void reindex() {
        ajudaManager.reindexarAjuda();
    }

}
