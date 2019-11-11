package br.com.infox.epp.assinador.view.jsf;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;

public class AssinadorHandler extends ComponentHandler {

    public AssinadorHandler(ComponentConfig config) {
        super(config);
    }
    
    @Override
    public void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        super.onComponentCreated(ctx, c, parent);
        Assinador assinador = (Assinador) c;
        assinador.addAssinadorListener(new AssinadorListenerImpl());
    }
    
}
