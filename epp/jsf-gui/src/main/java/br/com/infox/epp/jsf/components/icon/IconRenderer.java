package br.com.infox.epp.jsf.components.icon;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import org.apache.commons.lang3.StringUtils;

@FacesRenderer(componentFamily = Icon.COMPONENT_FAMILY, rendererType = Icon.RENDERER_TYPE)
public class IconRenderer extends Renderer {
    
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component);
    }
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        if (!component.isRendered())
            return;

        Icon icon = (Icon) component;
        ResponseWriter writer = context.getResponseWriter();
        String clientId = icon.getClientId(context);

        writer.startElement("span", icon);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", join(" ", "ifx-icon", icon.getStyleClass()), "styleClass");
        writer.writeAttribute("style", icon.getStyle(), "style");
        writer.writeAttribute("onclick", icon.getOnclick(), "onclick");
        writer.writeAttribute("title", icon.getText(), "text");
        switch (icon.getType().toLowerCase()) {
        case "simple-line":
            writeSimpleLineIcon(context, icon);
            break;
        case "mdl":
            writeMdlIcon(context, icon);
            break;
        default:
            writeFaIcon(context, icon);
            break;
        }
        if (icon.getShowText()) {
            writer.startElement("label", null);
            writer.writeAttribute("class", join(" ", "ifx-icon-text", icon.getTextStyleClass()), "textStyleClass");
            writer.writeText(icon.getText(), "text");
            writer.endElement("label");
        }
        writer.endElement("span");
    }

    private String join(String separator, Object... objects) {
        return StringUtils.join(objects, separator);
    }
    
    private void writeSimpleLineIcon(FacesContext context, Icon icon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("i", null);
        writer.writeAttribute("class", StringUtils.join(new Object[] { "icon", icon.getValue() }, "-"), "value");
        writer.endElement("i");
    }
    
    private void writeFaIcon(FacesContext context, Icon icon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("i", null);
        writer.writeAttribute("class", StringUtils.join(new Object[] { "fa fa", icon.getValue() }, "-"), "value");
        writer.endElement("i");
    }

    private void writeMdlIcon(FacesContext context, Icon icon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("i", null);
        writer.writeAttribute("class", "material-icons", null);
        writer.writeText(icon.getValue(), "value");
        writer.endElement("i");
    }
}
