package br.com.infox.jbpm.graphic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.graph.exe.Token;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.jsf.util.JsfUtil;

@Named
@ViewScoped
public class GraphicExecutionView implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private GraphicExecutionService graphicExecutionService;
    @Inject
    private JsfUtil jsfUtil;
    
    private NodeGraphImage nodeGraphImage;
    private Map<String, GraphImageBean> graphImageBeans;
    private Token token;
    
    public String getSvg() {
        graphImageBeans = new HashMap<>();
        String svg = graphicExecutionService.performGraphicExecution(token, graphImageBeans); 
        return svg;
    }

    public void onSelectNodeElement() {
        String key = jsfUtil.getRequestParameter("key");
        nodeGraphImage = (NodeGraphImage) graphImageBeans.get(key);
    }
    
    public String getUsuariosExecutaramNo() {
        if (nodeGraphImage != null && nodeGraphImage.isTaskNode()) {
            return graphicExecutionService.getUsuariosExecutaramNo(nodeGraphImage);
        }
        return null;
    }
    
    public void onCloseInformacoes() {
        nodeGraphImage = null;
    }
    
    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
    
    public NodeGraphImage getNodeGraphImage() {
        return nodeGraphImage;
    }

}
