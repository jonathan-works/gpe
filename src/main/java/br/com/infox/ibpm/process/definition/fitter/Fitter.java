package br.com.infox.ibpm.process.definition.fitter;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.seam.util.ComponentUtil;

@Scope(ScopeType.CONVERSATION)
@Transactional
public abstract class Fitter {

    private ProcessBuilder pb;

    public abstract void clear();

    protected ProcessBuilder getProcessBuilder() {
    	if(pb == null || pb.getFluxo() == null)
    		findProcessBuilder();
        return pb;
    }

    @Create
    public void init() {
        findProcessBuilder();
    }

	private void findProcessBuilder() {
		pb = ComponentUtil.getComponent(ProcessBuilder.NAME);
	}

}
