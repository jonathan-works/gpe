package br.com.infox.jsf.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primefaces.context.RequestContext;

public class DialogFrameworkBuilder {
    
    private String path;
    private final Map<String,Object> options = new HashMap<>();
    private final Map<String, List<String>> params = new HashMap<String, List<String>>();
    private final Map<String, Object> flashParams = new HashMap<>();
    
    public static DialogFrameworkBuilder builder() {
        return new DialogFrameworkBuilder();
    }
    
    public DialogFrameworkBuilder setPath(String path) {
        this.path = path;
        return this;
    }
    
    public DialogFrameworkBuilder setModal(boolean modal) {
        options.put("modal", modal);
        return this;
    }
    
    public DialogFrameworkBuilder setDraggable(boolean draggable) {
        options.put("draggable", draggable);
        return this;
    }
    
    public DialogFrameworkBuilder setResizable(boolean resizable) {
        options.put("resizable", resizable);
        return this;
    }
    
    public DialogFrameworkBuilder setContentWidth(String contentWidth) {
        options.put("contentWidth", contentWidth);
        return this;
    }
    
    public DialogFrameworkBuilder setContentHeight(String contentHeight) {
        options.put("contentHeight", contentHeight);
        return this;
    }
    
    public DialogFrameworkBuilder setWidth(String width) {
        options.put("width", width);
        return this;
    }
    
    public DialogFrameworkBuilder setHeight(String height) {
        options.put("height", height);
        return this;
    }
    
    public DialogFrameworkBuilder setIncludeViewParams(boolean includeViewParams) {
        options.put("includeViewParams", includeViewParams);
        return this;
    }
    
    public DialogFrameworkBuilder setClosable(boolean closable) {
        options.put("closable", closable);
        return this;
    }
    
    public DialogFrameworkBuilder setHeaderText(String headerText) {
        options.put("headerText", headerText);
        return this;
    }
    
    public DialogFrameworkBuilder setOnShow(String onShow) {
        options.put("onShow", onShow);
        return this;
    }
    
    public DialogFrameworkBuilder setFlashParam(String nome, Object value) {
        flashParams.put(nome, value);
        return this;
    }
    
    public DialogFrameworkBuilder setParam(String nome, String value) {
        if ( params.containsKey(nome) ) {
            params.get(nome).add(value);
        } else {
            params.put(nome, Arrays.asList(new String[]{value}));
        }
        return this;
    }
    
    public DialogFrameworkBuilder setParam(String nome, List<String> list) {
        params.put(nome, list);
        return this;
    }
    
    public DialogFramework build() {
        if ( !options.containsKey("contentWidth") ) {
            options.put("contentWidth", "100%");
        }
        
        if ( !options.containsKey("contentHeight") ) {
            options.put("contentHeight", "100%");
        }
        
        if ( !options.containsKey("width") ) {
            options.put("width", "50%");
        }
        
        if ( !options.containsKey("height") ) {
            options.put("height", "50%");
        }
        
        if ( options.containsKey("position") && "center".equals(options.get("position"))) {
            Object width = options.get("width");
            Object height = options.get("height");
            String left = "calc( 100% - " + width + ") / 2";
            String top = "calc( 100% - " + height + ") / 2";
            options.put("position", left + "," + top);
        }
        return new DialogFramework(path, options, params, flashParams);
    }
    
    public static class DialogFramework {
        
        private String path;
        private Map<String,Object> options = new HashMap<>();
        private Map<String, List<String>> params;
        private Map<String, Object> flashParams;
        
        public DialogFramework(String path, Map<String, Object> options, Map<String, List<String>> params, Map<String, Object> flashParams) {
            this.path = path;
            this.options = options;
            this.params = params;
            this.flashParams = flashParams;
        }

        public void openDialog() {
            if ( !flashParams.isEmpty()  ) {
                for (Map.Entry<String, Object> entryFlashParam : flashParams.entrySet()) {
                    JsfUtil.instance().addFlashParam(entryFlashParam.getKey(), entryFlashParam.getValue());
                }
            }
            RequestContext.getCurrentInstance().openDialog(path, options, params);
            JsfUtil.instance().applyLastPhaseFlashAction();
        }
        
    }
}
