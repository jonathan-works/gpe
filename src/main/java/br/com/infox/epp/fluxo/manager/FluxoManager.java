package br.com.infox.epp.fluxo.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;

/**
 * Classe Manager para a entidade Fluxo 
 * @author tassio
 */
@Name(FluxoManager.NAME)
@AutoCreate
public class FluxoManager extends GenericManager{

	private static final long serialVersionUID = -6521661616139554331L;
	private static final Class<Fluxo> CLASS = Fluxo.class;

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
	
	public List<Fluxo> findAll(){
	    return findAll(CLASS);
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
	
	public boolean existeFluxoComDescricao(String descricao) {
		return fluxoDAO.existeFluxoComDescricao(descricao);
	}
	
	public boolean existeFluxoComCodigo(String codigo) {
		return fluxoDAO.existeFluxoComCodigo(codigo);
	}
}