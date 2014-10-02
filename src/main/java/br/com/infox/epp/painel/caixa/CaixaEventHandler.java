package br.com.infox.epp.painel.caixa;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.home.ProcessoEpaHome;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.seam.util.ComponentUtil;

@Name(CaixaEventHandler.NAME)
@Scope(ScopeType.APPLICATION)
/**
 * Classe responsável pela verificação e associação (se existir) para qual Caixa
 * irá o processo que está sendo movimentado.
 */
public class CaixaEventHandler {

    public static final String NAME = "caixaEvento";
    private static final String PROCESSO = "processo";

    @In
    private ProcessoManager processoManager;

    /**
     * Método principal, onde ocorrerá a validação para verificar se o processo
     * se encaixa em alguma Caixa, para então adicioná-lo à ela.
     * @throws DAOException 
     */
    @Observer(Event.EVENTTYPE_TRANSITION)
    public void filtrarProcessos(final ExecutionContext context) throws DAOException {
        final Processo proc = processoManager.find(getIdProcesso());
        if (proc != null) {
            final List<Caixa> caixaResList = getCaixaResultList(context);
            if (caixaResList != null && !caixaResList.isEmpty()) {
                processoManager.moverProcessoParaCaixa(caixaResList, proc);
            }
        }
    }

    private List<Caixa> getCaixaResultList(final ExecutionContext context) {
        final Caixa cf = getCaixa(context.getTransition());
        final List<Caixa> result = new ArrayList<>(0);
        if (cf != null) {
            final CaixaList caixaList = ComponentUtil.getComponent(CaixaList.NAME);
            caixaList.setEntity(cf);
            result.addAll(caixaList.getResultList());
        }
        return result;
    }

    private Caixa getCaixa(final Transition transicao) {
        Caixa caixa = null;
        TarefaManager tarefaManager = ComponentUtil.getComponent(TarefaManager.NAME);
        final Tarefa destino = tarefaManager.getTarefa(transicao.getTo().getName(), transicao.getTo().getProcessDefinition().getName());
        if (destino != null) {
            caixa = new Caixa();
            caixa.setTarefa(destino);
            caixa.setIdNodeAnterior(new Long(transicao.getFrom().getId()).intValue());
        }
        return caixa;
    }

    private Integer getIdProcesso() {
        Integer idProcesso = JbpmUtil.getProcessVariable(PROCESSO);
        if (idProcesso == null) {
            final Processo processo = ProcessoEpaHome.instance().getInstance();
            if (processo != null) {
                idProcesso = processo.getIdProcesso();
            }
        }
        return idProcesso;
    }

}
