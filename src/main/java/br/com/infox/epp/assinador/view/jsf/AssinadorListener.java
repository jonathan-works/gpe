package br.com.infox.epp.assinador.view.jsf;

import javax.faces.event.FacesListener;

public interface AssinadorListener extends FacesListener {

    void processEvent(AssinadorEvent certificadoDigitalEvent);

}
