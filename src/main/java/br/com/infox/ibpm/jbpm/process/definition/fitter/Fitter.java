package br.com.infox.ibpm.jbpm.process.definition.fitter;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Scope;

import br.com.infox.ibpm.jbpm.process.definition.ProcessBuilder;
import br.com.itx.util.ComponentUtil;

@Scope(ScopeType.CONVERSATION)
public abstract class Fitter {
    
    private ProcessBuilder pb;
    
	public abstract void clear();
	
	protected ProcessBuilder getProcessBuilder() {
        return pb;
    }

    @Create
	public void init() {
	    pb = ComponentUtil.getComponent(ProcessBuilder.NAME);
	}
	
}
