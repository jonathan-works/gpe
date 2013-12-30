package br.com.infox.epp.system.facade;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;

@Name(SystemFacade.NAME)
@Scope(ScopeType.CONVERSATION)
public class SystemFacade {
    
    public static final String NAME = "systemFacade";
    
    @In private ParametroManager parametroManager;
    
    public String exportarPDF(){
        final Parametro parametro = parametroManager.getParametro("exportarPDF");
        if (parametro == null) {
            return Boolean.FALSE.toString();
        }
        return parametro.getValorVariavel();
    }
    
    public String exportarXLS(){
        final Parametro parametro = parametroManager.getParametro("exportarXLS");
        if (parametro == null) {
            return Boolean.FALSE.toString();
        }
        return parametro.getValorVariavel();
    }

}
