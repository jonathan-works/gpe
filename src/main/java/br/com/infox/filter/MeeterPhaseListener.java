package br.com.infox.filter;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.event.PhaseEvent;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

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
    private boolean producao;
    
    @PostConstruct
    public void init() {
        producao = "true".equals(Contexts.getApplicationContext().get("producao"));
    }

    @Observer("org.jboss.seam.beforePhase")
    public void beforePhase(PhaseEvent event) {
        if (!producao) {
            time = new Date().getTime();
            System.out.println("Entrou: " + event.getPhaseId());
        }
    }

    @Observer("org.jboss.seam.afterPhase")
    public void afterPhase(PhaseEvent event) {
        if (!producao) {
            System.out.println("Saiu: " + event.getPhaseId() + " - "
                    + (new Date().getTime() - time)+ " [ " + event.getFacesContext().getExternalContext().getRequestServletPath() + " ]");
            time = 0;
        }
    }

}
