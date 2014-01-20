package br.com.infox.epp.ajuda.view;

import static br.com.infox.core.constants.WarningConstants.RAWTYPES;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.ajuda.entity.Ajuda;
import br.com.infox.epp.ajuda.entity.Pagina;
import br.com.infox.epp.search.SearchUtil;
import br.com.itx.util.ComponentUtil;

@Name(AjudaView.NAME)
@Scope(ScopeType.PAGE)
public class AjudaView {
    
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
    
    @SuppressWarnings(RAWTYPES)
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

}
