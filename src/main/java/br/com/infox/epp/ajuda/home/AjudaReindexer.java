package br.com.infox.epp.ajuda.home;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.ajuda.manager.AjudaManager;

@Name(AjudaReindexer.NAME)
@Scope(ScopeType.PAGE)
public class AjudaReindexer implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "ajudaReindexer";

    @In
    private AjudaManager ajudaManager;

    public void reindex() {
        ajudaManager.reindexarAjuda();
    }

}
