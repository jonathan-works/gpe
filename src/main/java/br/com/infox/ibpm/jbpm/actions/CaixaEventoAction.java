package br.com.infox.ibpm.jbpm.actions;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.ibpm.entity.Caixa;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.home.ProcessoHome;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.itx.component.grid.GridQuery;
import br.com.itx.exception.AplicationException;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;


@Name(CaixaEventoAction.NAME)
@BypassInterceptors
@Scope(ScopeType.APPLICATION)
/**
 * Classe responsável pela verificação e associação (se existir) para qual Caixa
 * irá o processo que está sendo movimentado.
 */
public class CaixaEventoAction {

	private static final LogProvider LOG = Logging.getLogProvider(CaixaEventoAction.class);

	public static final String NAME = "caixaEvento";
	private static final String PROCESSO = "processo";
	private static final String FILTRO_CAIXA_SEARCH = "filtroCaixaSearch";
	private static final String FILTRO_CAIXA_GRID = "filtroCaixaGrid";
	
	/**
	 * Método principal, onde ocorrerá a validação para verificar se o processo
	 * se encaixa em alguma Caixa, para então adicioná-lo à ela.
	 */
	@Observer(Event.EVENTTYPE_TRANSITION)
 	public void filtrarProcessos(ExecutionContext context) {
		try {
			Transition t = context.getTransition();
			Integer idProcesso = JbpmUtil.getProcessVariable(PROCESSO);
			Caixa cf =  new Caixa();
			cf.setTarefa(JbpmUtil.getTarefa(t.getTo().getName(), 
					t.getTo().getProcessDefinition().getName()));
			if(cf.getTarefa() == null) {
				return;
			}
			cf.setTarefaAnterior(JbpmUtil.getTarefa(t.getFrom().getName(), 
										 			t.getFrom().getProcessDefinition().getName()));
			Processo proc = null;
			if(idProcesso == null) {
				Processo processo = ProcessoHome.instance().getInstance();
				if(processo != null) {
					proc = EntityUtil.getEntityManager().find(Processo.class, processo.getIdProcesso());
				}
			} else {
				proc = EntityUtil.getEntityManager().find(Processo.class, idProcesso);
			}
			if(proc == null) {
				return;
			}
			Contexts.getConversationContext().set(FILTRO_CAIXA_SEARCH, cf);
			GridQuery gridQuery = (GridQuery)ComponentUtil.getComponent(FILTRO_CAIXA_GRID);
			List<Caixa> caixaList = gridQuery.getResultList();
			if(caixaList != null && caixaList.size() > 0) {
				addProcessoCaixa(caixaList, proc);
			}
		} catch (Exception ex) {
			LOG.warn("Erro ao filtrar os processos na caixa", ex);
			throw new AplicationException(AplicationException.
					createMessage("filtrar os processos na caixa: " + ex.getLocalizedMessage(), 
								  "filtrarProcessos()", 
								  "CaixaEventoAction", 
								  "BPM"));
		}
	}
	
	/**
	 * Adiciona o(s) processo(s) a(s) caixa(s)
	 * @param caixaList - Lista da caixas que o processo pode ser inserido
	 * @param processo - Processo em Movimentação
	 */
	private void addProcessoCaixa(List<Caixa> caixaList, Processo processo) throws Exception {
		if(caixaList != null) {
			for(Caixa c : caixaList) {
				EntityUtil.flush();
				EntityUtil.getEntityManager().createNativeQuery(
						"update public.tb_processo set id_caixa = :caixa " +
						"where id_processo = :idProcesso")
						.setParameter("caixa", c.getIdCaixa())
						.setParameter("idProcesso", processo.getIdProcesso())
						.executeUpdate();
				return;
			}
		}
	}
	
}