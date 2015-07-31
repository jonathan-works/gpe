package br.com.infox.jsf.util;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;

import org.richfaces.component.UIDataTable;

public class JsfUtil {
	public static void clear(UIComponent uiComponent){
		if(uiComponent instanceof EditableValueHolder){
			((EditableValueHolder)uiComponent).resetValue();
		}
		for (UIComponent child : uiComponent.getChildren()){
			if(!(child instanceof UIData) || !(child instanceof UIDataTable)){
				clear(child);
			}
		}
	}
}
