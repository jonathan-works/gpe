package br.com.infox.epp.processo.documento.search;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.faces.Redirect;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.documento.dao.ProcessoDocumentoDAO;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;

@Scope(ScopeType.CONVERSATION)
@Name(ProcessoDocumentoSearch.NAME)
public class ProcessoDocumentoSearch {

    @In
    private ProcessoDocumentoDAO processoDocumentoDAO;
    
    @In
    private ProcessoManager processoManager;

    private static final Integer PAGE_SIZE = 15;

    private String palavraPesquisada;
    private List<ProcessoDocumento> resultadoPesquisa = new ArrayList<>();

    public static final String NAME = "processoDocumentoSearch";

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

    public List<ProcessoDocumento> getResultadoPesquisa() {
        return resultadoPesquisa;
    }

    public void setResultadoPesquisa(List<ProcessoDocumento> resultadoPesquisa) {
        this.resultadoPesquisa = resultadoPesquisa;
    }

    private void pesquisar() {
        setResultadoPesquisa(processoDocumentoDAO.pesquisar(getPalavraPesquisada()));
    }

    public String getNameTarefa(Long idTask) {
        if (idTask != null && idTask != 0) {
            Session session = ManagedJbpmContext.instance().getSession();
            TaskInstance ti = (TaskInstance) session.get(TaskInstance.class, idTask);
            return " - " + ti.getTask().getName();
        } else {
            return "(Anexo do Processo)";
        }
    }
    
    /**
     * Método redireciona para visualização do processo escolhido no paginador
     * 
     * @param processo Processo a ser visualizado no paginador
     */
    public void visualizarProcesso(Processo processo) {
        if (processo != null) {
            Redirect.instance().setConversationPropagationEnabled(false);
            Redirect.instance().setViewId("/Processo/Consulta/paginator.xhtml");
            Redirect.instance().setParameter("id", processo.getIdProcesso());
            Redirect.instance().setParameter("idJbpm", processo.getIdJbpm());
            Redirect.instance().execute();
        }
    }
}
