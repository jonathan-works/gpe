package br.com.infox.filter;

import java.util.Date;

import javax.faces.event.PhaseEvent;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * Classe para medição de tempo das fases do ciclos de vida JSF
 * 
 * Para habilitar remova o comentário dos observer
 * 
 * @author luiz
 * 
 */
@Name("meeterPhaseListener")
@BypassInterceptors
public class MeeterPhaseListener {

    private long time;

    @Observer("org.jboss.seam.beforePhase")
    public void beforePhase(PhaseEvent event) {
        time = new Date().getTime();
        System.out.println("Entrou: " + event.getPhaseId());
    }

    @Observer("org.jboss.seam.afterPhase")
    public void afterPhase(PhaseEvent event) {
        System.out.println("Saiu: " + event.getPhaseId() + " - "
                + (new Date().getTime() - time));
        time = 0;
    }

}
