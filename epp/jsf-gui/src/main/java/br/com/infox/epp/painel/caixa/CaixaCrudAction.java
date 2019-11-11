package br.com.infox.epp.painel.caixa;

import java.util.List;

import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.hibernate.postgres.error.PostgreSQLErrorCode;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(CaixaCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
@ContextDependency
public class CaixaCrudAction extends AbstractCrudAction<Caixa, CaixaManager> {
	
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(CaixaCrudAction.class);

    public static final String NAME = "caixaCrudAction";

    @Inject
    private TarefaManager tarefaManager;
    @Inject
    private InfoxMessages infoxMessages;
    
    public String getTaskName() {
        return tarefaManager.getTaskName(getInstance().getTaskKey());
    }

    public List<SelectItem> getPreviousNodes() {
        return tarefaManager.getPreviousNodes(getInstance().getTaskKey());
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
    
}
