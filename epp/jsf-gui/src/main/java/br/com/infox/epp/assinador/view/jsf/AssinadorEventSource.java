package br.com.infox.epp.assinador.view.jsf;

public interface AssinadorEventSource {
    void addAssinadorListener(AssinadorListener listener);

    AssinadorListener[] getAssinadorListeners();

    void removeAssinadorListener(AssinadorListener listener);
}
