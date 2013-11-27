package br.com.infox.epp.estatistica.action;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.ibpm.jbpm.context.InfoxManagedJbpmContext;

/**
 * Classe Action para o BAM
 * @author tassio
 */
@Name(BamAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class BamAction {

	public static final String NAME = "bamAction";
	
	@In
	private FluxoManager fluxoManager;
	@In
	private ProcessoEpaManager processoEpaManager;
	
	private List<Fluxo> fluxoList;
	
	/**
	 * Retorna todos os fluxos ativos no sistema
	 * @return lista com todos os fluxos ativos
	 */
	public List<Fluxo> getFluxoList() {
		if (fluxoList == null) {
			fluxoList = fluxoManager.getFluxoList(); 
		}
		return fluxoList;
	}
	
	public List<Fluxo> getFluxoSuggest(Object suggest) {
		String prefix = (String) suggest;
		List<Fluxo> fluxoList = getFluxoList();
		List<Fluxo> result = new ArrayList<Fluxo>();
		for (Fluxo fluxo: fluxoList) {
			if (fluxo.getFluxo().startsWith(prefix)) {
				result.add(fluxo);
			}
		}
		return result;
	}
	
	public Boolean contemProcessoAtrasado(Fluxo fluxo) {
		return fluxoManager.contemProcessoAtrasado(fluxo);
	}
	
	/**
	 * Retorna os processo não finalizados de um determinado fluxo
	 */
	public List<ProcessoEpa> getProcessosNaoFinalizados(Fluxo fluxo) {
		return processoEpaManager.listNotEnded(fluxo);
	}

	public TaskInstance getTaskInstance(Long idTaskInstance) {
		return InfoxManagedJbpmContext.instance().getTaskInstance(idTaskInstance);
	}
}