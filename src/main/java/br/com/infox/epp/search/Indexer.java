package br.com.infox.epp.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LimitTokenCountAnalyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jboss.seam.Component;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.jsf.function.ElFunctions;
import br.com.infox.seam.path.PathResolver;

public class Indexer {

    private Analyzer analyzer = new LimitTokenCountAnalyzer(new BrazilianAnalyzer(Version.LUCENE_36), Integer.MAX_VALUE);
    private Directory directory;
    private static final LogProvider LOG = Logging.getLogProvider(Indexer.class);

    public static File getIndexerPath() {
        String fileName = getIndexerFileName();
        if (fileName == null) {
            String path = System.getProperty("user.home");
            StringBuilder sb = new StringBuilder();
            sb.append(path).append(File.separatorChar);
            sb.append(getPathResolver().getContextPath().substring(1));
            sb.append(File.separatorChar).append("indexer");
            fileName = sb.toString();
        }
        return new File(fileName);
    }

    public Indexer() throws IOException {
        this(getIndexerPath());
    }

    public Indexer(File indexPath) throws IOException {
        directory = FSDirectory.open(indexPath);
    }

    public void index(String id, Map<String, String> storedFields,
            Map<String, String> fields) {
        try {
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_36, analyzer).setOpenMode(OpenMode.CREATE_OR_APPEND);
            IndexWriter writer = new IndexWriter(directory, indexWriterConfig);
            Document doc = new Document();
            doc.add(new Field("id", id, Field.Store.YES, Field.Index.ANALYZED));
            for (Entry<String, String> e : fields.entrySet()) {
                doc.add(new Field(e.getKey(), e.getValue(), Field.Store.NO, Field.Index.ANALYZED));
            }
            for (Entry<String, String> e : storedFields.entrySet()) {
                doc.add(new Field(e.getKey(), e.getValue(), Field.Store.YES, Field.Index.ANALYZED));
            }
            writer.updateDocument(new Term("id", id), doc);
            writer.close();
        } catch (Exception e) {
            LOG.error(".index()", e);
        }

    }

    public List<Document> search(String searchText, String[] fields,
            int maxResult) {
        List<Document> list = new ArrayList<Document>();
        try {
            IndexReader indexReader = IndexReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(indexReader);
            Query query = getQuery(searchText, fields);
            TopScoreDocCollector collector = TopScoreDocCollector.create(maxResult, true);
            isearcher.search(query, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                Document doc = isearcher.doc(hits[i].doc);
                list.add(doc);
            }
            isearcher.close();
            directory.close();
        } catch (Exception e) {
            LOG.error(".search()", e);
        }
        return list;
    }

    public Query getQuery(String searchText, String[] fields) throws ParseException {
        QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fields, analyzer);
        return parser.parse(searchText);
    }

    private static PathResolver getPathResolver() {
        return (PathResolver) Component.getInstance(PathResolver.NAME);
    }

    private static String getIndexerFileName() {
        ElFunctions elFunctions = (ElFunctions) Component.getInstance(ElFunctions.NAME);
        return elFunctions.evaluateExpression("indexerFileName");
    }

}
