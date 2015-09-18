package br.com.infox.epp.ajuda.dao;

import static br.com.infox.epp.ajuda.query.AjudaQuery.AJUDA_BY_URL;
import static br.com.infox.epp.ajuda.query.AjudaQuery.AJUDA_FIND_ALL_QUERY;
import static br.com.infox.epp.ajuda.query.AjudaQuery.PARAM_URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.ajuda.entity.Ajuda;
import br.com.infox.epp.search.SearchService;
import br.com.infox.hibernate.session.SessionAssistant;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(AjudaDAO.NAME)
@AutoCreate
public class AjudaDAO extends DAO<Ajuda> {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(AjudaDAO.class);
    private static final Class<Ajuda> CLASS = Ajuda.class;
    public static final String NAME = "ajudaDAO";

    private static final String INDICES_CRIADOS = "----------- Indices criados -------------";
    private static final String CRIANDO_INDICES = "----------- Criando indices -------------";

    @In
    private SessionAssistant sessionAssistant;

    public Ajuda getAjudaByPaginaUrl(String url) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_URL, url);
        return getNamedSingleResult(AJUDA_BY_URL, parameters);
    }

    public List<Object[]> pesquisar(String textoPesquisa) throws ParseException {
        Query luceneQuery = getLuceneQuery(textoPesquisa);
        FullTextQuery textQuery = getEntityManager().createFullTextQuery(luceneQuery, CLASS);
        List<Object[]> resultado = new ArrayList<>();
        for (Object obj : textQuery.getResultList()) {
            Ajuda ajuda = (Ajuda) obj;
            String fragments = SearchService.getBestFragments(luceneQuery, ajuda.getTexto());
            resultado.add(new Object[] { ajuda, fragments });
        }
        return resultado;
    }

    private Query getLuceneQuery(String textoPesquisa) throws ParseException {
        String[] fields = new String[] { "texto" };
        MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fields, SearchService.getAnalyzer());
        parser.setAllowLeadingWildcard(true);
        return parser.parse("+" + textoPesquisa + "+");
    }

    @Override
    public FullTextEntityManager getEntityManager() {
        return Search.getFullTextEntityManager(super.getEntityManager());
    }

    public void reindexarAjuda() {
        LOG.info(CRIANDO_INDICES);
        Session session = sessionAssistant.getSession();
        org.hibernate.Query query = session.createQuery(AJUDA_FIND_ALL_QUERY);
        query.setCacheMode(CacheMode.IGNORE);
        query.setFetchSize(50);
        ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);
        while (scroll.next()) {
            Ajuda a = (Ajuda) scroll.get(0);
            getEntityManager().index(a);
        }
        scroll.close();
        LOG.info(INDICES_CRIADOS);
    }

}
