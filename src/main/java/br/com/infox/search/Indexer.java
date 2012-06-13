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
package br.com.infox.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import br.com.itx.component.Util;


public class Indexer {
	
	private Analyzer analyzer = new BrazilianAnalyzer();
	private Directory directory;

	public static File getIndexerPath() {
		Util util = new Util();
		String fileName = util.eval("indexerFileName");
		if (fileName == null) {
			String path = System.getProperty("user.home");
			StringBuilder sb = new StringBuilder();
			sb.append(path).append(File.separatorChar);
			sb.append(util.getContextPath().substring(1));
			sb.append(File.separatorChar).append("indexer");
			fileName = sb.toString();
		}
		return new File(fileName);
	}
	
	public Indexer() throws IOException {
		this(getIndexerPath());
	}
	
	public Indexer(File indexPath) throws IOException {
		directory = FSDirectory.getDirectory(indexPath);
	}
	
	public void index(String id, Map<String, String> storedFields, Map<String, String> fields) {
		try {
			IndexWriter writer = new IndexWriter(directory, analyzer, MaxFieldLength.UNLIMITED);
			Document doc = new Document();
			doc.add(new Field("id", id, Field.Store.YES, Field.Index.ANALYZED));
			for (Entry<String, String> e : fields.entrySet()) {
				doc.add(new Field(e.getKey(), e.getValue(), Field.Store.NO,
						Field.Index.ANALYZED));
			}
			for (Entry<String, String> e : storedFields.entrySet()) {
				doc.add(new Field(e.getKey(), e.getValue(), Field.Store.YES,
						Field.Index.ANALYZED));
			}
		    writer.updateDocument(new Term("id", id), doc);
		    writer.optimize();
		    writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public List<Document> search(String searchText, String[] fields, int maxResult) {
	    List<Document> list = new ArrayList<Document>();
		try {
			IndexSearcher isearcher = new IndexSearcher(directory);
		    Query query = getQuery(searchText, fields);
		    TopDocCollector collector = new TopDocCollector(maxResult);
		    isearcher.search(query, collector);
		    ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    for (int i = 0; i < hits.length; i++) {
		    	Document doc = isearcher.doc(hits[i].doc);
				list.add(doc);
		    }
		    isearcher.close();
		    directory.close();	    
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public Query getQuery(String searchText, String[] fields)
			throws ParseException {
		QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
		return parser.parse(searchText);
	}
	
}