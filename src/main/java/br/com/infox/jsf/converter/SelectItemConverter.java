package br.com.infox.jsf.converter;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("br.com.infox.jsf.converter.SelectItemConverter")
public class SelectItemConverter implements Converter {
    
    private List<Object> selectItems;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        carregarSelectItemList(component);
        try {
            Integer valueOf = Integer.valueOf(value);
            return selectItems.get(valueOf);
        } catch ( NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        carregarSelectItemList(component);
        if ( value == null ) {
            return null;
        } else {
            Integer indexOf = selectItems.indexOf(value);
            return indexOf == -1 ? null : indexOf.toString();
        }
    }

    private void carregarSelectItemList(UIComponent component) {
        if ( selectItems == null ) {
            selectItems = new ArrayList<>();
            List<UIComponent> children = component.getChildren();
            for (UIComponent child : children) {
                if ( child instanceof UISelectItem ) {
                    UISelectItem selectItem = (UISelectItem) child;
                    if (!selectItem.isNoSelectionOption()) {
                        selectItems.add(selectItem.getValue());
                    }
                } else if ( child instanceof UISelectItems ) {
                    selectItems.addAll((List<?>) ((UISelectItems) child).getValue());
                }
            }
        }
    }

}
