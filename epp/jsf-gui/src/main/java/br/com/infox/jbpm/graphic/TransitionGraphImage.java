package br.com.infox.jbpm.graphic;

import org.jbpm.graph.def.Transition;
import org.jbpm.graph.log.TransitionLog;

public class TransitionGraphImage extends GraphImageBean {
    
    private TransitionLog transitionLog;

    public TransitionGraphImage(TransitionLog transitionLog) {
        super(transitionLog.getTransition().getKey(), transitionLog.getToken());
        this.transitionLog = transitionLog;
    }

    public TransitionLog getTransitionLog() {
        return transitionLog;
    }
    
    public Transition getTransition() {
        return transitionLog.getTransition();
    }
    
}
