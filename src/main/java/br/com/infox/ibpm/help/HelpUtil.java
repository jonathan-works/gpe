/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.help;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.demo.html.HTMLParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.NullFragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import br.com.itx.util.FileUtil;


public final class HelpUtil {
	
	private static final String SEPARATOR = "<b> &#183;&#183;&#183;</b>";


	private HelpUtil() {}

	private static final String BEGIN_TAG = "<span class='highlight'>";
	private static final String END_TAG = "</span>";

	private static final String BEGIN_MARKER = "!!!BEGIN_HIGHLIGHT!!!";
	private static final String END_MARKER = "!!!END_HIGHLIGHT!!!";


	public static String getBestFragments(Query query, String text) {
		Reader reader = null;
		BufferedReader br = null;
		try {
			reader = new HTMLParser(new StringReader(text)).getReader();
			br = new BufferedReader(reader);
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line=br.readLine()) != null) {
				sb.append(line).append(System.getProperty("line.separator"));
			}
			text = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FileUtil.close(reader);
			FileUtil.close(br);
		}
		
		return highlightText(query, text, true);
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
		
		text = Entities.decode(text);
		TokenStream ts = getAnalyzer().tokenStream("texto",
				new StringReader(text));
		
		try {
			String s = highlighter.getBestFragments(ts, text, 3, SEPARATOR);
			s = s.replaceAll(BEGIN_MARKER, BEGIN_TAG);
			s= s.replaceAll(END_MARKER, END_TAG);
			return s;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static Analyzer getAnalyzer() {
		return new BrazilianAnalyzer();
	}
	

}