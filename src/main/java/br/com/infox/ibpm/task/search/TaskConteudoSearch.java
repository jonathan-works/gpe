package br.com.infox.ibpm.task.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery.TooManyClauses;
import org.apache.lucene.util.Version;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.epp.search.SearchService;
import br.com.infox.ibpm.task.dao.TaskConteudoDAO;
import br.com.infox.ibpm.task.entity.TaskConteudo;
import br.com.infox.index.SimpleQueryParser;

@Scope(ScopeType.CONVERSATION)
@Name(TaskConteudoSearch.NAME)
public class TaskConteudoSearch {

    public static final String NAME = "taskConteudoSearch";
    private static final LogProvider LOG = Logging.getLogProvider(TaskConteudoSearch.class);

    @In
    private TaskConteudoDAO taskConteudoDAO;
    

    private static final Integer PAGE_SIZE = 15;

    private String palavraPesquisada;
    private List<TaskConteudo> resultadoPesquisa = new ArrayList<TaskConteudo>();

    public Integer getPageSize() {
        return PAGE_SIZE;
    }

    public String getPalavraPesquisada() {
        return palavraPesquisada;
    }

    public void setPalavraPesquisada(String palavraPesquisada) {
        this.palavraPesquisada = palavraPesquisada;
        pesquisar();
    }

    public List<TaskConteudo> getResultadoPesquisa() {
        return resultadoPesquisa;
    }

    public void setResultadoPesquisa(List<TaskConteudo> resultadoPesquisa) {
        this.resultadoPesquisa = resultadoPesquisa;
    }

    private void pesquisar() {
        setResultadoPesquisa(taskConteudoDAO.pesquisar(getPalavraPesquisada()));
    }

    public String getBestFragments(TaskConteudo taskConteudo) throws ParseException {
        SimpleQueryParser parser = new SimpleQueryParser(new BrazilianAnalyzer(Version.LUCENE_36), "conteudo");
        Query query;
        try {
            query = parser.parse(getPalavraPesquisada());
        } catch (TooManyClauses e) {
            LOG.warn("", e);
            return "";
        }
        return SearchService.getBestFragments(query, taskConteudo.getConteudo());
    }

}
