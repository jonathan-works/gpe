package br.com.infox.jbpm;

import static org.jboss.seam.annotations.Install.DEPLOYMENT;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jbpm.JbpmContext;

import br.com.infox.cdi.producer.JbpmContextProducer;

@Scope(ScopeType.EVENT)
@Name("org.jboss.seam.bpm.jbpmContext")
@BypassInterceptors
@Install(precedence = DEPLOYMENT, dependencies = "org.jboss.seam.bpm.jbpm")
public class ManagedJbpmContext {

    @Unwrap
    public JbpmContext getJbpmContext() {
        return JbpmContextProducer.getJbpmContext();
    }

}
