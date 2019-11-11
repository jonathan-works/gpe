package br.com.infox.epp.ajuda.crud;

import java.util.Date;

import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.ajuda.entity.Ajuda;
import br.com.infox.epp.ajuda.entity.HistoricoAjuda;
import br.com.infox.epp.ajuda.entity.Pagina;
import br.com.infox.epp.ajuda.list.HistoricoAjudaList;
import br.com.infox.epp.ajuda.manager.AjudaManager;
import br.com.infox.epp.ajuda.manager.PaginaManager;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(AjudaCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
@ContextDependency
public class AjudaCrudAction extends AbstractCrudAction<Ajuda, AjudaManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "ajudaCrudAction";
    private static final LogProvider LOG = Logging.getLogProvider(AjudaCrudAction.class);

    private String viewId;
    private Pagina pagina;
    private Ajuda oldInstance;

    @Inject
    private AjudaManager ajudaManager;
    @Inject
    private PaginaManager paginaManager;
    @Inject
    private HistoricoAjudaList historicoAjudaList;

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
        createInstance();
    }

    @Override
    public void newInstance() {
        super.newInstance();
        createInstance();
    }

    private Ajuda createInstance() {
        setInstance(new Ajuda());
        Ajuda ajuda = ajudaManager.getAjudaByPaginaUrl(viewId);
        if (ajuda != null) {
            getInstance().setTexto(ajuda.getTexto());
            oldInstance = ajuda;
        }else{
        	oldInstance = null;
        }
        getInstance().setPagina(getPagina());
        return getInstance();
    }

    @Override
    public String save() {
        Pagina pagina = paginaManager.getPaginaByUrl(viewId);
        if (pagina == null) {
            try {
                pagina = paginaManager.criarNovaPagina(viewId);
            } catch (DAOException e) {
                LOG.error("Não foi possível gravar a página " + pagina, e);
            }
        }
        getInstance().setUsuario(Authenticator.getUsuarioLogado());
        getInstance().setDataRegistro(new Date());
        getInstance().setPagina(pagina);
        String ret = super.save();
        if (PERSISTED.equals(ret)) {
            if (oldInstance != null) {
                try {
                    ajudaManager.gravarHistorico(oldInstance);
                } catch (DAOException e) {
                    LOG.error("Erro ao gravar o histórico de ajuda", e);
                }
                historicoAjudaList.refresh();
            }
            newInstance();
        }
        return ret;
    }

    private Pagina getPagina() {
        if (pagina == null) {
            return paginaManager.getPaginaByUrl(viewId);
        }
        return pagina;
    }

    public void recuperar(HistoricoAjuda historico) {
        getInstance().setTexto(historico.getTexto());
        save();
    }

    public Integer getOldInstanceId() {
        if (this.oldInstance != null) {
            return this.oldInstance.getIdAjuda();
        }
        return null;
    }
}
