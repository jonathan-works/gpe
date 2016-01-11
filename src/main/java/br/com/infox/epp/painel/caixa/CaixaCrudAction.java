package br.com.infox.epp.painel.caixa;

import java.util.List;

import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.tarefa.component.tree.PainelTreeHandler;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.hibernate.postgres.error.PostgreSQLErrorCode;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(CaixaCrudAction.NAME)
@ContextDependency
public class CaixaCrudAction extends AbstractCrudAction<Caixa, CaixaManager> {
	
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(CaixaCrudAction.class);

    public static final String NAME = "caixaCrudAction";

    @Inject
    private TarefaManager tarefaManager;
    @Inject
    private CaixaManager caixaManager;
    @Inject
    private ActionMessagesService actionMessagesService;
    @Inject
    private PainelTreeHandler painelTreeHandler;
    @Inject
    private InfoxMessages infoxMessages;

    public List<SelectItem> getPreviousNodes() {
        return tarefaManager.getPreviousNodes(getInstance().getTaskKey());
    }

    @Override
    protected boolean isInstanceValid() {
    	return (getInstance().getTaskKey() != null);
    }

    public void adicionarCaixaNoPainel(String destinationNodeKey) {
    	getInstance().setTaskKey(destinationNodeKey);
    	try {
			caixaManager.persist(getInstance());
		} catch (DAOException e) {
			actionMessagesService.handleDAOException(e);
			LOG.error("adicionarCaixaNoPainel", e);
		}
        painelTreeHandler.clearTree();
        newInstance();
    }
    
    public void removerCaixaNoPainel() {
    	try {
			caixaManager.remove(getInstance());
		} catch (DAOException e) {
			actionMessagesService.handleDAOException(e);
			LOG.error("removerCaixaNoPainel", e);
		}
        painelTreeHandler.clearTree();
        newInstance();
    }

    @Override
    public String update() {
        String ret = super.update();
        try {
            if (PostgreSQLErrorCode.valueOf(ret) == PostgreSQLErrorCode.UNIQUE_VIOLATION) {
                final StatusMessages messages = getMessagesHandler();
                messages.clear();
                messages.add(Severity.ERROR, infoxMessages.get("caixa.error.previousNodeExists"));
            }
        } catch (IllegalArgumentException e) {
            LOG.warn(".update()", e);
        }
        return ret;
    }
    
    public void setCaixaIdCaixa(Integer id) {
        setId(id);
    }

    public Integer getCaixaIdCaixa() {
        return (Integer) getId();
    }

}
