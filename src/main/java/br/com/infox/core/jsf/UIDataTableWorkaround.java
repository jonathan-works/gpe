package br.com.infox.core.jsf;

import javax.faces.context.FacesContext;

import org.richfaces.component.UIDataTable;

public class UIDataTableWorkaround extends UIDataTable {
    
    @Override
    public void setRowKey(FacesContext context, Object rowKey) {
        if (rowKey == null && getRowKey() == null){
            return;
        }
        super.setRowKey(context, rowKey);
    }

}
