package br.com.infox.jsf.workaround;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.util.Util;

public class TablelessRadioRenderer extends com.sun.faces.renderkit.html_basic.RadioRenderer {
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        rendererParamsNotNull(context, component);
        if (!shouldEncode(component)) {
            return;
        }
        ResponseWriter writer = context.getResponseWriter();
        if (writer == null){
            return;
        }
        String alignStr;
        boolean alignVertical = false;
        if (null != (alignStr = (String) component.getAttributes().get("layout"))) {
            alignVertical = alignStr.equalsIgnoreCase("pageDirection");
        }
        Converter converter = null;
        if (component instanceof ValueHolder) {
            converter = ((ValueHolder) component).getConverter();
        }
        Iterator<SelectItem> items = RenderKitUtils.getSelectItems(context, component);
        Object currentSelections = getCurrentSelectedValues(component);
        Object[] submittedValues = getSubmittedSelectedValues(component);
        Map<String, Object> attributes = component.getAttributes();
        OptionComponentInfo optionInfo = new OptionComponentInfo((String) attributes.get("disabledClass"),
                (String) attributes.get("enabledClass"), (String) attributes.get("unselectedClass"),
                (String) attributes.get("selectedClass"), Util.componentIsDisabled(component),
                isHideNoSelection(component));
        writer.startElement("span", component);
        writer.writeAttribute("style", attributes.get("style"), "style");
        writer.writeAttribute("class", attributes.get("styleClass"), "styleClass");
        int idx = -1;
        while (items.hasNext()) {
            SelectItem curItem = items.next();
            idx++;
            if (curItem instanceof SelectItemGroup) {
                if (curItem.getLabel() != null) {
                    writer.writeText(curItem.getLabel(), component, "label");
                }
                SelectItem[] itemsArray = ((SelectItemGroup) curItem).getSelectItems();
                for (int i = 0; i < itemsArray.length; ++i) {
                    renderOption(context, component, converter, itemsArray[i], currentSelections, submittedValues,
                            alignVertical, i, optionInfo);
                }
            } else {
                renderOption(context, component, converter, curItem, currentSelections, submittedValues, alignVertical,
                        idx, optionInfo);
            }
        }
        writer.endElement("span");
    }
}
