package br.com.infox.epp.ajuda.view;

import static br.com.infox.core.constants.WarningConstants.RAWTYPES;
import static br.com.infox.core.constants.WarningConstants.UNCHECKED;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
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
import br.com.infox.epp.search.SearchUtil;
import br.com.itx.util.ComponentUtil;

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
    
    @SuppressWarnings({RAWTYPES, UNCHECKED})
    public List getResultadoPesquisa() throws ParseException {
        if (getTextoPesquisa() == null) {
            return null;
        }
        if (resultado == null) {
            resultado = new ArrayList();
            
            FullTextEntityManager em = (FullTextEntityManager) ComponentUtil.getComponent("entityManager");
            String[] fields = new String[] { "texto" };
            MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fields, SearchUtil.getAnalyzer());
            parser.setAllowLeadingWildcard(true);
            org.apache.lucene.search.Query query = parser.parse("+"
                    + getTextoPesquisa() + "+");

            FullTextQuery textQuery = em.createFullTextQuery(query, Ajuda.class);

            for (Object o : textQuery.getResultList()) {
                Ajuda a = (Ajuda) o;
                String s = SearchUtil.getBestFragments(query, a.getTexto());
                resultado.add(new Object[] { a, s });
            }
        }
        return resultado;
    }
    
    public String getTexto() {
        String texto = null;
        if (instance != null) {
            texto = instance.getTexto();

            if (textoPesquisa != null && texto != null) {
                QueryParser parser = new QueryParser(Version.LUCENE_36, "texto", SearchUtil.getAnalyzer());
                try {
                    org.apache.lucene.search.Query query = parser.parse(textoPesquisa);
                    String highlighted = SearchUtil.highlightText(query, texto, false);
                    if (!highlighted.equals("")) {
                        texto = highlighted;
                    }
                } catch (ParseException e) {
                    LOG.error(".getTexto()", e);
                }
            }
        }
        return texto;
    }

}
