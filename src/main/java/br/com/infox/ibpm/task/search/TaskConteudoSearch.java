package br.com.infox.ibpm.task.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.faces.Redirect;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.search.SearchService;
import br.com.infox.ibpm.task.dao.TaskConteudoDAO;
import br.com.infox.ibpm.task.entity.TaskConteudo;

@Scope(ScopeType.CONVERSATION)
@Name(TaskConteudoSearch.NAME)
public class TaskConteudoSearch {

    public static final String NAME = "taskConteudoSearch";
    
    @In
    private TaskConteudoDAO taskConteudoDAO;
    @In private ProcessoManager processoManager;
    
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
        String[] fields = new String[] { "conteudo" };
        QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fields, new BrazilianAnalyzer(Version.LUCENE_36));
        Query query = parser.parse(getPalavraPesquisada());
        return SearchService.getBestFragments(query, taskConteudo.getConteudo());
    }
    
    /**
     * Método redireciona para visualização do processo escolhido no paginador
     * 
     * @param processo Processo a ser visualizado no paginador
     */
    public void visualizarProcesso(int idProcesso) {
        Processo processo = processoManager.find(idProcesso);
        Redirect.instance().setConversationPropagationEnabled(false);
        Redirect.instance().setViewId("/Processo/Consulta/paginator.xhtml");
        Redirect.instance().setParameter("id", processo.getIdProcesso());
        Redirect.instance().setParameter("idJbpm", processo.getIdJbpm());
        Redirect.instance().execute();
    }
}
