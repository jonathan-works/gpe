package br.com.infox.core.jsf;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;

import org.jboss.seam.core.Expressions;
import org.richfaces.component.UIColumn;
import org.richfaces.component.UIDataTable;

import com.sun.faces.facelets.el.ELText;

public final class DatatableDynamicColumnHelper {
	
	public static void addDynamicColumn(ColumnModel columnModel, UIDataTable dataTable) {
		addDynamicColumn(columnModel, dataTable, dataTable.getChildCount());
	}
	
	public static void addDynamicColumn(ColumnModel columnModel, UIDataTable dataTable, int index) {
		dataTable.getChildren().add(index, createColumn(columnModel));
	}
	
	public static void addDynamicColumn(ColumnModel columnModel, UIDataTable dataTable, String columnId) {
		int index = dataTable.getChildCount();
		for (int i = 0; i < dataTable.getChildCount(); i++) {
			UIComponent c = dataTable.getChildren().get(i);
			if (c instanceof UIColumn && c.getId().equals(columnId)) {
				index = i + 1;
			}
		}
		addDynamicColumn(columnModel, dataTable, index);
	}
	
	private static UIColumn createColumn(ColumnModel columnModel) {
		UIOutput header = createHeader(columnModel);
		UIOutput value = createValue(columnModel);
		
		UIColumn column = new UIColumn();
		column.setHeader(header);
		column.getChildren().add(value);
		return column;
	}
	
	private static UIOutput createValue(ColumnModel columnModel) {
		UIOutput value = new UIOutput();
		if (ELText.isLiteral(columnModel.getValue())) {
			value.setValue(columnModel.getValue());
		} else {
			value.setValueExpression("value", Expressions.instance().createValueExpression(columnModel.getValue()).toUnifiedValueExpression());
		}
		return value;
	}

	private static UIOutput createHeader(ColumnModel columnModel) {
		UIOutput header = new UIOutput();
		if (ELText.isLiteral(columnModel.getHeader())) {
			header.setValue(columnModel.getHeader());
		} else {
			header.setValueExpression("value", Expressions.instance().createValueExpression(columnModel.getHeader()).toUnifiedValueExpression());
		}
		return header;
	}

	public static class ColumnModel {
		private String header;
		private String value;
		
		public ColumnModel(String header, String value) {
			this.header = header;
			this.value = value;
		}
		
		public String getHeader() {
			return header;
		}
		
		public String getValue() {
			return value;
		}
	}
}
