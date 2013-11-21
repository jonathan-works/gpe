package br.com.infox.epp.system.facade;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.system.util.ParametroUtil;

@Name(SystemFacade.NAME)
@Scope(ScopeType.CONVERSATION)
public class SystemFacade {
    
    public static final String NAME = "systemFacade";
    
    public String exportarPDF(){
        return ParametroUtil.getParametroOrFalse("exportarPDF");
    }
    
    public String exportarXLS(){
        return ParametroUtil.getParametroOrFalse("exportarXLS");
    }

}
