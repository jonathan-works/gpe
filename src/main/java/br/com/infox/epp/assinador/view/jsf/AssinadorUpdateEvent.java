package br.com.infox.epp.assinador.view.jsf;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

class AssinadorUpdateEvent extends FacesEvent implements AssinadorEvent {
    public AssinadorUpdateEvent(UIComponent component) {
        super(component);
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
        return ActionType.UPDATE;
    }
}