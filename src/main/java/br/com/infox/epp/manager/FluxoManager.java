package br.com.infox.epp.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.FluxoDAO;
import br.com.infox.ibpm.entity.Fluxo;

/**
 * Classe Manager para a entidade Fluxo 
 * @author tassio
 */
@Name(FluxoManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class FluxoManager extends GenericManager{

	private static final long serialVersionUID = -6521661616139554331L;

	public static final String NAME = "fluxoManager";

	@In
	private FluxoDAO fluxoDAO;
	
	/**
	 * Retorna todos os Fluxos ativos
	 * @return lista de fluxos ativos
	 */
	public List<Fluxo> getFluxoList() {
		return fluxoDAO.getFluxoList();
	}

	public boolean contemProcessoAtrasado(Fluxo fluxo) {
		return fluxoDAO.quantidadeProcessosAtrasados(fluxo) > 0;
	}
	
	public Fluxo getFluxoByDescricao(String descricao){
		return fluxoDAO.getFluxoByDescricao(descricao);
	}
	
	public boolean existemProcessosAssociadosAFluxo(Fluxo fluxo){
		return fluxoDAO.getQuantidadeDeProcessoAssociadosAFluxo(fluxo) > 0;
	}
	
}