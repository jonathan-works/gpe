package br.com.infox.index;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

public class SimpleQueryParser {
    
    private static final Log LOG = Logging.getLog(SimpleQueryParser.class);
    
    private String[] fields;
    private Analyzer analyzer;
    
    public SimpleQueryParser(Analyzer analyzer, String... fields) {
        this.fields = fields;
        this.analyzer = analyzer;
    }
    
    public Query parse(String query) {
        BooleanQuery booleanQuery = new BooleanQuery(false);
        for (String field : fields) {
            TokenStream tokens = analyzer.tokenStream(field, new StringReader(query));
            try {
                CharTermAttribute termAtt = tokens.getAttribute(CharTermAttribute.class);
                while (tokens.incrementToken()) {
                    booleanQuery.add(new TermQuery(new Term(field, termAtt.toString())), Occur.SHOULD);
                }
            } catch (IOException e) {
                LOG.warn("", e);
            }
        }
        return booleanQuery;
    }
}
