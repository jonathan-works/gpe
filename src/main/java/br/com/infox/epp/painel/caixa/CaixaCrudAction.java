package br.com.infox.epp.painel.caixa;

import java.text.MessageFormat;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.tarefa.component.tree.TarefasTreeHandler;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.hibernate.postgres.error.PostgreSQLErrorCode;
import br.com.infox.ibpm.event.JbpmEventsHandler;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

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
        getInstance().setTarefa(tarefaManager.find(idTarefa));
        getInstance().setNomeIndice(MessageFormat.format("{0}-{1}", getInstance().getNomeCaixa(), idTarefa));
        save();
        newInstance();
    }

    @Override
    protected void afterSave(String ret) {
        if (AbstractHome.PERSISTED.equals(ret)) {
            JbpmEventsHandler.updatePostDeploy();
            TarefasTreeHandler.clearActiveTree();
        }
        super.afterSave(ret);
    }

    @Override
    public String update() {
        String ret = super.update();
        try {
            if (PostgreSQLErrorCode.valueOf(ret) == PostgreSQLErrorCode.UNIQUE_VIOLATION) {
                FacesMessages.instance().clear();
                FacesMessages.instance().add(Severity.ERROR, "Já existe uma caixa na mesma tarefa com o nó anterior especificado.");
            }
        } catch (IllegalArgumentException e) {
            LOG.warn(".update()", e);
            // Retorno do update não pertence ao enum, nada a fazer
        }
        return ret;
    }

    public static CaixaCrudAction instance() {
        return ComponentUtil.getComponent(NAME);
    }

    @Override
    public String remove() {
        caixaManager.removeCaixaByIdCaixa(getInstance().getIdCaixa());
        String ret = super.remove();
        TarefasTreeHandler.clearActiveTree();
        return ret;
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
            FacesMessages.instance().add(Severity.ERROR, "Por favor, selecione a caixa que deseja excluir!");
        }
        TarefasTreeHandler tree = ComponentUtil.getComponent("tarefasTree");
        tree.clearTree();
    }
}
