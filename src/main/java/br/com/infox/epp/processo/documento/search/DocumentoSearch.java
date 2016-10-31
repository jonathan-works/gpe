package br.com.infox.epp.processo.documento.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery.TooManyClauses;
import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.documento.dao.DocumentoDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Scope(ScopeType.CONVERSATION)
@Name(DocumentoSearch.NAME)
public class DocumentoSearch implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Integer PAGE_SIZE = 15;
	public static final String NAME = "documentoSearch";
    private static final LogProvider LOG = Logging.getLogProvider(DocumentoSearch.class);
    
	@In
    private DocumentoDAO documentoDAO;
    @In
    private ProcessoManager processoManager;

    private String palavraPesquisada;
    private List<Documento> resultadoPesquisa = new ArrayList<>();

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

    public List<Documento> getResultadoPesquisa() {
        return resultadoPesquisa;
    }

    public void setResultadoPesquisa(List<Documento> resultadoPesquisa) {
        this.resultadoPesquisa = resultadoPesquisa;
    }

    private void pesquisar() {
        try {
            setResultadoPesquisa(documentoDAO.pesquisar(getPalavraPesquisada()));
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

    @Transactional
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
