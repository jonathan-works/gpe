package br.com.infox.epp.system.facade;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;

@Scope(ScopeType.CONVERSATION)
@Name(SystemFacade.NAME)
public class SystemFacade implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "systemFacade";

    @In
    private ParametroManager parametroManager;

    public String exportarPDF() {
        final Parametro parametro = parametroManager.getParametro("exportarPDF");
        if (parametro == null) {
            return Boolean.FALSE.toString();
        }
        return parametro.getValorVariavel();
    }

    public String exportarXLS() {
        final Parametro parametro = parametroManager.getParametro("exportarXLS");
        if (parametro == null) {
            return Boolean.FALSE.toString();
        }
        return parametro.getValorVariavel();
    }

}
