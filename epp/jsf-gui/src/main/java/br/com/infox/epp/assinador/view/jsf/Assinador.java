package br.com.infox.epp.assinador.view.jsf;

import java.util.Collection;
import java.util.Collections;

import javax.el.MethodExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.assinador.view.AssinaturaCallback;

@FacesComponent(Assinador.COMPONENT_TYPE)
public class Assinador extends javax.faces.component.UICommand
        implements ClientBehaviorHolder, AssinadorEventSource {

    static final String PARTIAL_BEHAVIOR_EVENT = "javax.faces.behavior.event";
    static final String COMPONENT_TYPE = "br.com.infox.epp.assinador.view.jsf.Assinador";
    static final String COMPONENT_FAMILY = "br.com.infox.epp.assinador.view.jsf";
    static final String RENDERER_TYPE = "br.com.infox.epp.assinador.view.jsf.AssinadorRenderer";

    private enum PropertyKeys {
        assinavelProvider, callbackHandler, tokenField, execute, render, disabled, onbegin, oncomplete, value, signAction, timeout, style, styleClass, onclick, token, status, currentPhase;

        String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        public String toString() {
            return ((this.toString != null) ? this.toString : super.toString());
        }
    }

    public Assinador() {
        setRendererType(RENDERER_TYPE);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public AssinavelProvider getAssinavelProvider() {
        return (AssinavelProvider) getStateHelper().eval(PropertyKeys.assinavelProvider, null);
    }

    public void setAssinavelProvider(AssinavelProvider _assinavelProvider) {
        getStateHelper().put(PropertyKeys.assinavelProvider, _assinavelProvider);
    }

    public AssinaturaCallback getCallbackHandler() {
        return (AssinaturaCallback) getStateHelper().eval(PropertyKeys.callbackHandler, null);
    }

    public void setCallbackHandler(AssinaturaCallback _callbackHandler) {
        getStateHelper().put(PropertyKeys.callbackHandler, _callbackHandler);
    }
    public String getTokenField() {
        return (String) getStateHelper().eval(PropertyKeys.tokenField, null);
    }
    
    public void setTokenField(String _tokenField) {
        getStateHelper().put(PropertyKeys.tokenField, _tokenField);
    }
    SignPhase getCurrentPhase() {
        return (SignPhase) getStateHelper().eval(PropertyKeys.currentPhase, SignPhase.BEFORE_CLICK);
    }
    
    void setCurrentPhase(SignPhase _currentPhase) {
        getStateHelper().put(PropertyKeys.currentPhase, _currentPhase);
    }
    String getToken() {
        return (String) getStateHelper().eval(PropertyKeys.token, null);
    }
    
    void setToken(String _token) {
        getStateHelper().put(PropertyKeys.token, _token);
    }
    StatusToken getStatus() {
        return (StatusToken) getStateHelper().eval(PropertyKeys.status, null);
    }
    
    void setStatus(StatusToken _status) {
        getStateHelper().put(PropertyKeys.status, _status);
    }

    public String getExecute() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.execute, null);
    }

    public void setExecute(String _execute) {
        getStateHelper().put(PropertyKeys.execute, _execute);
    }

    public String getRender() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.render, null);
    }

    public void setRender(String _render) {
        getStateHelper().put(PropertyKeys.render, _render);
    }

    public String getOnbegin() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onbegin, null);
    }

    public void setOnbegin(String onbegin) {
        getStateHelper().put(PropertyKeys.onbegin, onbegin);
    }

    public java.lang.String getOncomplete() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.oncomplete, null);
    }

    public void setOncomplete(java.lang.String _oncomplete) {
        getStateHelper().put(PropertyKeys.oncomplete, _oncomplete);
    }

    public java.lang.String getOnclick() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onclick);

    }

    /**
     * <p>
     * Set the value of the <code>onclick</code> property.
     * </p>
     */
    public void setOnclick(java.lang.String onclick) {
        getStateHelper().put(PropertyKeys.onclick, onclick);
    }

    /**
     * <p>
     * Return the value of the <code>disabled</code> property.
     * </p>
     * <p>
     * Contents: Flag indicating that this element must never receive focus or be included in a subsequent submit. A
     * value of false causes no attribute to be rendered, while a value of true causes the attribute to be rendered as
     * disabled="disabled".
     */
    public boolean isDisabled() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);

    }

    /**
     * <p>
     * Set the value of the <code>disabled</code> property.
     * </p>
     */
    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public MethodExpression getSignAction() {
        return getActionExpression();

    }

    public void setSignAction(MethodExpression action) {
        setActionExpression(action);
    }

    /**
     * <p>
     * Return the value of the <code>onclick</code> property.
     * </p>
     * <p>
     * Contents: Javascript code executed when a pointer button is clicked over this element.
     */
    public java.lang.Number getTimeout() {
        return (java.lang.Number) getStateHelper().eval(PropertyKeys.timeout, 2000);

    }

    /**
     * <p>
     * Set the value of the <code>onclick</code> property.
     * </p>
     */
    public void setTimeout(java.lang.Number timeout) {
        getStateHelper().put(PropertyKeys.timeout, timeout);
    }

    /**
     * <p>
     * Return the value of the <code>style</code> property.
     * </p>
     * <p>
     * Contents: CSS style(s) to be applied when this component is rendered.
     */
    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style);

    }

    /**
     * <p>
     * Set the value of the <code>style</code> property.
     * </p>
     */
    public void setStyle(java.lang.String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    /**
     * <p>
     * Return the value of the <code>styleClass</code> property.
     * </p>
     * <p>
     * Contents: Space-separated list of CSS style class(es) to be applied when this element is rendered. This value
     * must be passed through as the "class" attribute on generated markup.
     */
    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass);

    }

    public void setStyleClass(java.lang.String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    @Override
    public Collection<String> getEventNames() {
        return Collections.unmodifiableCollection(Collections.<String>emptyList());
    }

    @Override
    public String getDefaultEventName() {
        return "click";
    }

    @Override
    public void addAssinadorListener(AssinadorListener listener) {
        super.addFacesListener(listener);
    }

    @Override
    public AssinadorListener[] getAssinadorListeners() {
        return (AssinadorListener[]) super.getFacesListeners(AssinadorListener.class);
    }

    @Override
    public void removeAssinadorListener(AssinadorListener listener) {
        super.removeFacesListener(listener);
    }

}