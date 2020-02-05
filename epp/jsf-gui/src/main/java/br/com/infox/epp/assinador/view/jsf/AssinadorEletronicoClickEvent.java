package br.com.infox.epp.assinador.view.jsf;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

import lombok.Getter;

class AssinadorEletronicoClickEvent extends FacesEvent implements AssinadorEvent {
	
	@Getter
    private String password;

	public AssinadorEletronicoClickEvent(UIComponent component, String password) {
        super(component);
        this.password = password;
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
        return ActionType.CLICK_ASSINATURA_ELETRONICA;
    }
}