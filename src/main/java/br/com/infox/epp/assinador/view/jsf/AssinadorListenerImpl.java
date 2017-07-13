package br.com.infox.epp.assinador.view.jsf;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import javax.faces.context.FacesContext;

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.view.AssinadorController;
import br.com.infox.epp.cdi.util.Beans;

public class AssinadorListenerImpl implements AssinadorListener, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public void processEvent(AssinadorEvent assinadorEvent) {
        switch (assinadorEvent.getActionType()) {
        case CLICK:
            clickEvent((AssinadorClickEvent) assinadorEvent);
            break;
        case SIGN:
            signEvent((AssinadorSignEvent) assinadorEvent);
            break;
        case UPDATE:
            updateStatusEvent((AssinadorUpdateEvent) assinadorEvent);
            break;
        default:
            ((Assinador)((AssinadorCompleteEvent)assinadorEvent).getComponent()).setCurrentPhase(SignPhase.BEFORE_CLICK);
            break;
        }
    }

    private void updateStatusEvent(AssinadorUpdateEvent evt) {
        Assinador button = (Assinador) evt.getComponent();
        StatusToken status = Beans.getReference(AssinadorService.class).getStatus(button.getToken());
        button.setStatus(status);
        
        if (SignPhase.AFTER_CLICK.equals(button.getCurrentPhase()))
            button.setCurrentPhase(SignPhase.WAITING_SIGNATURE);
    }

    private void signEvent(AssinadorSignEvent evt) {
        Assinador button = (Assinador) evt.getComponent();
        
        if (SignPhase.WAITING_SIGNATURE.equals(button.getCurrentPhase())) {
            if (button.getSignAction() != null) {
                button.getSignAction().invoke(FacesContext.getCurrentInstance().getELContext(),
                        new Object[] { evt.getToken() });
            } else {
                button.setTokenField(evt.getToken());
                jndi(AssinadorController.class).assinaturasRecebidas(evt.getToken(), button.getCallbackHandler());
            }
            button.setStatus(null);
            button.setToken(null);
            button.setCurrentPhase(null);
        }
    }

    private void clickEvent(AssinadorClickEvent evt) {
        Assinador button = (Assinador) evt.getComponent();
        String tokenValue = jndi(AssinadorController.class).criarGrupoAssinatura(button.getAssinavelProvider());
        button.setToken(tokenValue);
        button.setCurrentPhase(SignPhase.AFTER_CLICK);
    }

    private <T> T jndi(Class<T> type, Annotation... annotations) {
        return Beans.getReference(type, annotations);
    }

}