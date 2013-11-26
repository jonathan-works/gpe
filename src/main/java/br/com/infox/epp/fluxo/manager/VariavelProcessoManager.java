package br.com.infox.epp.fluxo.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.fluxo.dao.VariavelProcessoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.VariavelProcesso;

@Name(VariavelProcessoManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class VariavelProcessoManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "variavelProcessoManager";
	
	@In
	private VariavelProcessoDAO variavelProcessoDAO;
	
	public List<VariavelProcesso> listVariaveisProcessoByFluxo(Fluxo fluxo, int start, int count) {
		return variavelProcessoDAO.listVariaveisProcessoByFluxo(fluxo, start, count);
	}
	
	public Long getTotalVariaveisProcessoByFluxo(Fluxo fluxo) {
		return variavelProcessoDAO.getTotalVariaveisProcessoByFluxo(fluxo);
	}
}
