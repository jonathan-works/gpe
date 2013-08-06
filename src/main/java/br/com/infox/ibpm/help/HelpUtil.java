/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.help;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
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
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public final class HelpUtil {
	
	private static final String SEPARATOR = "<b> &#183;&#183;&#183;</b>";
	private static final LogProvider LOG = Logging.getLogProvider(HelpUtil.class);

	private HelpUtil() {}

	private static final String BEGIN_TAG = "<span class='highlight'>";
	private static final String END_TAG = "</span>";

	private static final String BEGIN_MARKER = "!!!BEGIN_HIGHLIGHT!!!";
	private static final String END_MARKER = "!!!END_HIGHLIGHT!!!";


	public static String getBestFragments(Query query, String text) {
		    Document doc = Jsoup.parse(text);
	        return highlightText(query, doc.body().text(), true);
	}


	public static String highlightText(Query query, String text, boolean isFragment) {
		Scorer scorer = new QueryScorer(query);
		Formatter fmt = new SimpleHTMLFormatter(BEGIN_MARKER,END_MARKER);
		Highlighter highlighter = new Highlighter(fmt, scorer);
		if (isFragment) {
			highlighter.setTextFragmenter(new SimpleFragmenter(80));
		} else {
			highlighter.setTextFragmenter(new NullFragmenter());
		}
		
		String auxiliarText = Entities.decode(text);
		TokenStream ts = getAnalyzer().tokenStream("texto",
				new StringReader(auxiliarText));
		
		try {
			String s = highlighter.getBestFragments(ts, auxiliarText, 3, SEPARATOR);
			s = s.replaceAll(BEGIN_MARKER, BEGIN_TAG);
			s= s.replaceAll(END_MARKER, END_TAG);
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