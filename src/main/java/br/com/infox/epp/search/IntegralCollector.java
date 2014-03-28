package br.com.infox.epp.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;

public class IntegralCollector extends Collector {

    private int docBase;
    private List<Integer> docs = new ArrayList<>();

    public List<Integer> getDocs() {
        return docs;
    }

    @Override
    public void setScorer(Scorer scorer) throws IOException {
        // TODO Auto-generated method stub
    }

    @Override
    public void collect(int doc) throws IOException {
        docs.add(docBase + doc);
    }

    @Override
    public void setNextReader(IndexReader reader, int docBase) throws IOException {
        this.docBase = docBase;
    }

    @Override
    public boolean acceptsDocsOutOfOrder() {
        return true;
    }

}
