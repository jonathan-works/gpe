package br.com.infox.epp.processo.documento.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery.TooManyClauses;
import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
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
    private static final LogProvider LOG = Logging.getLogProvider(ProcessoDocumentoSearch.class);

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
        try {
            setResultadoPesquisa(processoDocumentoDAO.pesquisar(getPalavraPesquisada()));
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
