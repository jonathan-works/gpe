package br.com.infox.epp.ajuda.view;

import static br.com.infox.core.constants.WarningConstants.RAWTYPES;

import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.epp.ajuda.entity.Ajuda;
import br.com.infox.epp.ajuda.entity.Pagina;
import br.com.infox.epp.ajuda.manager.AjudaManager;
import br.com.infox.epp.ajuda.manager.PaginaManager;
import br.com.infox.epp.search.SearchService;

@Name(AjudaView.NAME)
@Scope(ScopeType.CONVERSATION)
public class AjudaView {
    
    public static final String NAME = "ajudaView";
    private static final LogProvider LOG = Logging.getLogProvider(AjudaView.class);
    
    private Ajuda instance;
//    private Ajuda oldInstance;
    
    private String tab;
    private String viewId;
    private Pagina pagina;
    private String textoPesquisa;
    @SuppressWarnings(RAWTYPES)
    private List resultado;
    
    @In private AjudaManager ajudaManager;
    @In private PaginaManager paginaManager;
    @In private SearchService searchService;
    
    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getView() {
        return null;
    }
    
    public String getViewId() {
        return viewId;
    }
    
    public void setView(String view) {
        setViewId(view, true);
    }
    
    public void setViewId(String viewId, boolean clearSearch) {
        this.viewId = viewId;
        this.pagina = null;
        createInstance();
        if (clearSearch) {
            setTextoPesquisa(null);
        }
    }
    
    private Ajuda createInstance() {
        instance = new Ajuda();
        Ajuda ajuda = ajudaManager.getAjudaByPaginaUrl(viewId);
        if (ajuda != null) {
            instance.setTexto(ajuda.getTexto());
//            oldInstance = ajuda;
        }
        instance.setPagina(getPagina());
        return instance;
    }
    
    private Pagina getPagina() {
        if (pagina == null) {
            return verificaPagina();
        }
        return pagina;
    }
    
    private Pagina verificaPagina() {
        return paginaManager.getPaginaByUrl(viewId);
    }
    
    public String getTextoPesquisa() {
        return textoPesquisa;
    }

    public void setTextoPesquisa(String textoPesquisa) {
        this.resultado = null;
        this.textoPesquisa = textoPesquisa;
    }
    
    @SuppressWarnings({RAWTYPES})
    public List getResultadoPesquisa() throws ParseException {
        if (getTextoPesquisa() == null) {
            return null;
        }
        if (resultado == null) {
            resultado = ajudaManager.pesquisar(textoPesquisa);
        }
        return resultado;
    }
    
    public String getTexto() {
        String texto = null;
        if (instance != null) {
            texto = instance.getTexto();
            if (textoPesquisa != null && texto != null) {
                return searchService.pesquisaEmTexto(textoPesquisa, texto);
            }
        }
        return texto;
    }

}
