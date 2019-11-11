package br.com.infox.epp.jsf.components.icon;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;

@ResourceDependencies({
    @ResourceDependency(name="fonts/material-design-icons/material-icons.css"),
    @ResourceDependency(library="primefaces", name="fa/font-awesome.css"),
    @ResourceDependency(library="simple-line-icons", name="css/simple-line-icons.css")
})
@FacesComponent(Icon.COMPONENT_TYPE)
public class Icon extends javax.faces.component.UIOutput {

    static final String COMPONENT_TYPE = "br.com.infox.epp.jsf.Icon";
    static final String COMPONENT_FAMILY = "br.com.infox.epp.jsf";
    static final String RENDERER_TYPE = "br.com.infox.epp.jsf.IconRenderer";

    public Icon() {
        super();
        setRendererType(RENDERER_TYPE);
    }
    
    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    
    protected enum PropertyKeys {
        style, styleClass, text, showText, textStyleClass, onclick, materialDesignIcon, type;
        String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        public String toString() {
            return ((toString != null) ? toString : super.toString());
        }
    }

    public String getText() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.text);
    }

    public void setText(String text) {
        getStateHelper().put(PropertyKeys.text, text);
    }

    public Boolean getShowText() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showText, Boolean.FALSE);
    }

    public void setShowText(Boolean showText) {
        getStateHelper().put(PropertyKeys.showText, showText);
    }

    public String getTextStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.textStyleClass);
    }

    public void setTextStyleClass(String textStyleClass) {
        getStateHelper().put(PropertyKeys.textStyleClass, textStyleClass);
    }

    public String getOnclick() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onclick);
    }

    public void setOnclick(String onclick) {
        getStateHelper().put(PropertyKeys.onclick, onclick);
    }
    
    public String getType(){
        return (String) getStateHelper().eval(PropertyKeys.type, "fa");
    }
    public void setType(String type){
        getStateHelper().put(PropertyKeys.type, type);
    }
    
    @Deprecated
    public Boolean getMaterialDesignIcon() {
        return "mdl".equalsIgnoreCase(getType());
    }
    @Deprecated
    public void setMaterialDesignIcon(Boolean materialDesignIcon) {
        setType(Boolean.TRUE.equals(materialDesignIcon)?"mdl":"fa");
    }

    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style);

    }

    public void setStyle(java.lang.String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass);

    }

    public void setStyleClass(java.lang.String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

}
