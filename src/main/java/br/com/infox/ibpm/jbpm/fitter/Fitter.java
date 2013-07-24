package br.com.infox.ibpm.jbpm.fitter;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Scope;

import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.itx.util.ComponentUtil;

@Scope(ScopeType.CONVERSATION)
public abstract class Fitter {
    protected ProcessBuilder pb;
    
	public abstract void clear();
	
	@Create
	public void init() {
	    pb = ComponentUtil.getComponent(ProcessBuilder.NAME);
	}
	
}
