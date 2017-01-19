package br.com.infox.epp.assinador.view.jsf;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

class AssinadorSignEvent extends FacesEvent implements AssinadorEvent {
    private String token;

    public AssinadorSignEvent(UIComponent component, String token) {
        super(component);
        this.setToken(token);
    }

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAppropriateListener(FacesListener listener) {
        return listener instanceof AssinadorListener;
    }

    @Override
    public void processListener(FacesListener listener) {
        ((AssinadorListener) listener).processEvent(this);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SIGN;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}