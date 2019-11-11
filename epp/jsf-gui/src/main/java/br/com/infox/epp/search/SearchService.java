package br.com.infox.epp.search;

import java.io.IOException;
import java.io.StringReader;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.NullFragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(SearchService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
@Transactional
@Stateless
public class SearchService {

    public static final String NAME = "searchService";
    private static final String SEPARATOR = "<b> &#183;&#183;&#183;</b>";
    private static final LogProvider LOG = Logging.getLogProvider(SearchService.class);

    private static final String BEGIN_TAG = "<span class='highlight'>";
    private static final String END_TAG = "</span>";

    private static final String BEGIN_MARKER = "!!!BEGIN_HIGHLIGHT!!!";
    private static final String END_MARKER = "!!!END_HIGHLIGHT!!!";

    public static String getBestFragments(Query query, String text) {
        Document doc = Jsoup.parse(text);
        return highlightText(query, doc.body().text(), true);
    }

    public String pesquisaEmTexto(String textoPesquisa, String texto) {
        QueryParser parser = new QueryParser(Version.LUCENE_36, "texto", SearchService.getAnalyzer());
        try {
            Query query = parser.parse(textoPesquisa);
            Document doc = Jsoup.parse(texto);
            return SearchService.highlightText(query, StringEscapeUtils.unescapeHtml4(texto), false);
        } catch (ParseException e) {
            LOG.error("Não foi possível fazer parser do texto { Pesquisa por "
                    + textoPesquisa + " em " + texto + "}", e);
            return "";
        }
    }

    public static String highlightText(Query query, String text, boolean isFragment) {
        Scorer scorer = new QueryScorer(query);
        Formatter fmt = new SimpleHTMLFormatter(BEGIN_MARKER, END_MARKER);
        Highlighter highlighter = new Highlighter(fmt, scorer);
        if (isFragment) {
            highlighter.setTextFragmenter(new SimpleFragmenter(80));
        } else {
            highlighter.setTextFragmenter(new NullFragmenter());
        }

        TokenStream ts = getAnalyzer().tokenStream("texto", new StringReader(text));

        try {
            String s = highlighter.getBestFragments(ts, text, 3, SEPARATOR);
            s = s.replaceAll(BEGIN_MARKER, BEGIN_TAG);
            s = s.replaceAll(END_MARKER, END_TAG);
            return s;
        } catch (IOException | InvalidTokenOffsetsException e) {
            LOG.error(".highlightText()", e);
        }
        return "";
    }

    public static Analyzer getAnalyzer() {
        return new BrazilianAnalyzer(Version.LUCENE_36);
    }

}
