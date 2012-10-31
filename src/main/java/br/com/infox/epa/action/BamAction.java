package br.com.infox.epa.action;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epa.entity.ProcessoEpa;
import br.com.infox.epa.manager.FluxoManager;
import br.com.infox.epa.manager.ProcessoEpaManager;
import br.com.infox.ibpm.entity.Fluxo;

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
	
	/**
	 * Retorna todos os fluxos ativos no sistema
	 * @return lista com todos os fluxos ativos
	 */
	public List<Fluxo> getFluxoList() {
		return fluxoManager.getFluxoList();
	}
	
	/**
	 * Retorna os processo não finalizados de um determinado fluxo
	 */
	public List<ProcessoEpa> getProcessosNaoFinalizados(Fluxo fluxo) {
		return processoEpaManager.listNotEnded(fluxo);
	}

}