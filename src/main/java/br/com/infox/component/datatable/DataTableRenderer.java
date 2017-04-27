package br.com.infox.component.datatable;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.api.Pageable;
import org.primefaces.component.api.UIData;
import org.primefaces.util.MessageFactory;

import lombok.extern.java.Log;

public class DataTableRenderer extends org.primefaces.component.datatable.DataTableRenderer {
    
    static {
        addPaginatorElement("{RowsPerPageDropdown}", new RowsPerPageDropdownRenderer());
    }

    @Log
    public static class RowsPerPageDropdownRenderer extends org.primefaces.component.paginator.RowsPerPageDropdownRenderer {
        
        @Override
        public void render(FacesContext context, Pageable pageable) throws IOException {
            String template = pageable.getRowsPerPageTemplate();
            UIViewRoot viewroot = context.getViewRoot();
            char separator = UINamingContainer.getSeparatorChar(context);
            
            if(template != null) {
                ResponseWriter writer = context.getResponseWriter();
                int actualRows = pageable.getRows();
                String[] options = pageable.getRowsPerPageTemplate().split("[,\\s]+");
                String label = pageable.getRowsPerPageLabel();
                if(label != null)
                    log.info("RowsPerPageLabel attribute is deprecated, use 'primefaces.paginator.aria.ROWS_PER_PAGE' key instead to override default message.");
                else 
                    label = MessageFactory.getMessage(UIData.ROWS_PER_PAGE_LABEL, null);
                
                String clientId = pageable.getClientId(context);
                String ddId = clientId + separator + viewroot.createUniqueId();
                String ddName = clientId + "_rppDD";
                String labelId = null;
                
                if(label != null) {
                    labelId = clientId + "_rppLabel";
                    
                    writer.startElement("label", null);
                    writer.writeAttribute("id", labelId, null);
                    writer.writeAttribute("for", ddId, null);
                    writer.writeAttribute("class", UIData.PAGINATOR_RPP_LABEL_CLASS, null);
                    writer.writeText(label, null);
                    writer.endElement("label");
                }
                        
                writer.startElement("select", null);
                writer.writeAttribute("id", ddId, null);
                writer.writeAttribute("name", ddName, null);
                if(label != null) {
                    writer.writeAttribute("aria-labelledby", labelId, null);
                }
                writer.writeAttribute("class", UIData.PAGINATOR_RPP_OPTIONS_CLASS, null);
                writer.writeAttribute("value", pageable.getRows(), null);
                writer.writeAttribute("autocomplete", "off", null);

                for( String option : options){
                    int rows;
                    if ( "*".equals(option) ) {
                        rows = pageable.getRowCount();
                    } else {
                        rows = Integer.parseInt(option);
                    }
                    writer.startElement("option", null);
                    writer.writeAttribute("value", rows, null);

                    if(actualRows == rows){
                        writer.writeAttribute("selected", "selected", null);
                    }
                    Object labelAll = ((UIComponent) pageable).getAttributes().get("paginatorAllLabel");
                    writer.writeText("*".equals(option) ? (labelAll == null ? "ALL" : labelAll.toString()) : option, null);
                    writer.endElement("option");
                }

                writer.endElement("select");
            }
        }
        
    }
    
}
