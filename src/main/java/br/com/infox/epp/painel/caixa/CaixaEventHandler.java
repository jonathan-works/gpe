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

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.ibpm.jbpm.util.JbpmUtil;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;


@Name(CaixaEventHandler.NAME)
@Scope(ScopeType.APPLICATION)
/**
 * Classe responsável pela verificação e associação (se existir) para qual Caixa
 * irá o processo que está sendo movimentado.
 */
public class CaixaEventHandler {

	public static final String NAME = "caixaEvento";
	private static final String PROCESSO = "processo";
	
	@In private ProcessoManager processoManager;
	
	/**
	 * Método principal, onde ocorrerá a validação para verificar se o processo
	 * se encaixa em alguma Caixa, para então adicioná-lo à ela.
	 */
	@Observer(Event.EVENTTYPE_TRANSITION)
 	public void filtrarProcessos(final ExecutionContext context) {
		final Processo proc = EntityUtil.find(Processo.class, getIdProcesso());
		if(proc != null) {
			final List<Caixa> caixaResList = getCaixaResultList(context);
			if(caixaResList != null && !caixaResList.isEmpty()) {
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
		Caixa caixa =  null;
		final Tarefa destino = JbpmUtil.getTarefa(transicao.getTo().getName(), 
				transicao.getTo().getProcessDefinition().getName());
		if (destino != null) {
			caixa = new Caixa();
			caixa.setTarefa(destino);
			caixa.setIdNodeAnterior(Long.valueOf(transicao.getFrom().getId()).intValue());
		}
		return caixa;
	}

	private Integer getIdProcesso() {
		Integer idProcesso = JbpmUtil.getProcessVariable(PROCESSO);
		if(idProcesso == null) {
			final Processo processo = ProcessoHome.instance().getInstance();
			if(processo != null) {
				idProcesso = processo.getIdProcesso();
			}
		}
		return idProcesso;
	}
	
}