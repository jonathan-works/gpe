package br.com.infox.ibpm.task.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery.TooManyClauses;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epp.search.SearchService;
import br.com.infox.ibpm.task.dao.TaskConteudoDAO;
import br.com.infox.ibpm.task.entity.TaskConteudo;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Scope(ScopeType.CONVERSATION)
@Name(TaskConteudoSearch.NAME)
public class TaskConteudoSearch implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "taskConteudoSearch";
    private static final LogProvider LOG = Logging.getLogProvider(TaskConteudoSearch.class);
    private static final Integer PAGE_SIZE = 15;

    @In
    private TaskConteudoDAO taskConteudoDAO;

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
        try {
            setResultadoPesquisa(taskConteudoDAO.pesquisar(getPalavraPesquisada()));
        } catch (TooManyClauses e) {
            LOG.warn("", e);
            FacesMessages.instance().clear();
            FacesMessages.instance().add("Não foi possível realizar a pesquisa, muitos termos de busca");
        } catch (ParseException e) {
            LOG.error("", e);
            FacesMessages.instance().clear();
            FacesMessages.instance().add("Erro ao realizar a pesquisa, favor tentar novamente");
        }
    }

    public String getBestFragments(TaskConteudo taskConteudo) throws ParseException {
        QueryParser parser = new QueryParser(Version.LUCENE_36, "conteudo", new BrazilianAnalyzer(Version.LUCENE_36));
        Query query;
        try {
            query = parser.parse(getPalavraPesquisada());
        } catch (TooManyClauses e) {
            LOG.warn("", e);
            FacesMessages.instance().clear();
            FacesMessages.instance().add("Não foi possível realizar a pesquisa, muitos termos de busca");
            return "";
        } catch (ParseException e) {
            LOG.error("", e);
            FacesMessages.instance().clear();
            FacesMessages.instance().add("Erro ao realizar a pesquisa, favor tentar novamente");
            return "";
        }
        return SearchService.getBestFragments(query, taskConteudo.getConteudo());
    }

}
