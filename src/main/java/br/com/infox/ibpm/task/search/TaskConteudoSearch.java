package br.com.infox.ibpm.task.search;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.manager.ProcessoManager;
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
    
}
