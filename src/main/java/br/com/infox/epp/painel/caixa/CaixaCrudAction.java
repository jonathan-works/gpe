package br.com.infox.epp.painel.caixa;

import static java.text.MessageFormat.format;

import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.tarefa.component.tree.TarefasTreeHandler;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.hibernate.postgres.error.PostgreSQLErrorCode;
import br.com.infox.ibpm.event.JbpmEventsHandler;
import br.com.infox.seam.util.ComponentUtil;

@Name(CaixaCrudAction.NAME)
public class CaixaCrudAction extends AbstractCrudAction<Caixa, CaixaManager> {
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(CaixaCrudAction.class);

    public static final String NAME = "caixaCrudAction";
    public static final String ADD_CAIXA_EVENT = "addCaixaEvent";

    @In
    private TarefaManager tarefaManager;
    @In
    private CaixaManager caixaManager;
    @In
    private ActionMessagesService actionMessagesService;

    public List<SelectItem> getPreviousNodes() {
        return tarefaManager.getPreviousNodes(getInstance().getTarefa());
    }

    @Override
    protected boolean isInstanceValid() {
        if (getInstance().getTarefa() == null) {
            return false;
        }
        return true;
    }

    public void addCaixa(int idTarefa) {
        final Caixa caixa = getInstance();
        caixa.setTarefa(tarefaManager.find(idTarefa));
        caixa.setNomeIndice(format("{0}-{1}", caixa.getNomeCaixa(), idTarefa));
        save();
        newInstance();
    }

    @Override
    protected void afterSave(String ret) {
        if (AbstractAction.PERSISTED.equals(ret)) {
            try {
                JbpmEventsHandler.updatePostDeploy();
            } catch (DAOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            TarefasTreeHandler.clearActiveTree();
        }
        super.afterSave(ret);
    }

    @Override
    public String update() {
        String ret = super.update();
        try {
            if (PostgreSQLErrorCode.valueOf(ret) == PostgreSQLErrorCode.UNIQUE_VIOLATION) {
                final StatusMessages messages = getMessagesHandler();
                messages.clear();
                messages.add(Severity.ERROR, "Já existe uma caixa na mesma tarefa com o nó anterior especificado.");
            }
        } catch (IllegalArgumentException e) {
            LOG.warn(".update()", e);
            // Retorno do update não pertence ao enum, nada a fazer
        }
        resolveStatusMessage(ret);
        return ret;
    }

    public static CaixaCrudAction instance() {
        return ComponentUtil.getComponent(NAME);
    }

    @Override
    public String remove() {
        try {
            caixaManager.removeCaixaByIdCaixa(getInstance().getIdCaixa());
            String ret = super.remove();
            TarefasTreeHandler.clearActiveTree();
            return ret;
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
        }
        return null;
    }

    public void setCaixaIdCaixa(Integer id) {
        setId(id);
    }

    public Integer getCaixaIdCaixa() {
        return (Integer) getId();
    }

    public void removeCaixa(int idCaixa) {
        if (idCaixa == 0) {
            return;
        }
        setInstance(caixaManager.find(idCaixa));
        if (getInstance() != null) {
            remove();
        } else {
            getMessagesHandler().add(Severity.ERROR, "Por favor, selecione a caixa que deseja excluir!");
        }
        final TarefasTreeHandler tree = ComponentUtil.getComponent(TarefasTreeHandler.NAME);
        tree.clearTree();
    }
}
