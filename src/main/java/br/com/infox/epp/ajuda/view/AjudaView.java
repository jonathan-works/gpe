package br.com.infox.epp.ajuda.view;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.ajuda.entity.Pagina;

@Name(AjudaView.NAME)
@Scope(ScopeType.PAGE)
public class AjudaView <Ajuda>{
    
    public static final String NAME = "ajudaView";
    
    private String tab;
    private String viewId;
    private Pagina pagina;
    private String textoPesquisa;
    private List resultado;
    
    
    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getView() {
        return null;
    }
    
    public void setView(String view) {
        setViewId(view, true);
    }
    
    public void setViewId(String viewId, boolean clearSearch) {
        this.viewId = viewId;
        this.pagina = null;
//        createInstance();
        if (clearSearch) {
            setTextoPesquisa(null);
        }
    }
    
    public String getTextoPesquisa() {
        return textoPesquisa;
    }

    public void setTextoPesquisa(String textoPesquisa) {
        this.resultado = null;
        this.textoPesquisa = textoPesquisa;
    }

}
