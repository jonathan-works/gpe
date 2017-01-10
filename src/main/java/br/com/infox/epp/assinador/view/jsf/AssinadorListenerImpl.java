package br.com.infox.epp.assinador.view.jsf;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URI;

import javax.faces.context.FacesContext;
import javax.ws.rs.core.UriBuilder;

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.assinador.rest.api.TokenAssinaturaResource;
import br.com.infox.epp.assinador.view.AssinadorController;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.ws.factory.RestClientFactory;

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
        case COMPLETE:{
            ((Assinador)((AssinadorCompleteEvent)assinadorEvent).getComponent()).setCurrentPhase(SignPhase.BEFORE_CLICK);
        }
            break;
        default:
            break;
        }
    }

    private void updateStatusEvent(AssinadorUpdateEvent evt) {
        Assinador button = (Assinador) evt.getComponent();
        String baseUrl = jndi(PathResolver.class).getRestBaseUrl();
        URI uri = UriBuilder.fromPath(baseUrl).path("tokenAssinatura").path(button.getToken()).build();
        //FIXME Corrigir problema que ocorre com o {@link br.com.infox.ws.factory.RestClientFactory} ao utilizar o código abaixo. Não está inserindo '/' entre cada path
        // StatusToken status = RestClientFactory.create(baseUrl, TokenAssinaturaRest.class).getBaseResource().getTokenAssinaturaResource(button.getToken()).getStatus(); 
        try {
            StatusToken status = RestClientFactory.create(uri.toURL().toString() + "/", TokenAssinaturaResource.class)
                    .getStatus();
            button.setStatus(status);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

            button.setCurrentPhase(SignPhase.SIGNED);
        }
    }

    private void clickEvent(AssinadorClickEvent evt) {
        Assinador button = (Assinador) evt.getComponent();
        String tokenValue = jndi(AssinadorController.class).criarGrupoAssinatura(button.getAssinavelProvider());
        button.setToken(tokenValue);
        button.setCurrentPhase(SignPhase.AFTER_CLICK);
    }

    private <T> T jndi(Class<T> type, Annotation... annotations) {
        return BeanManager.INSTANCE.getReference(type, annotations);
    }

}