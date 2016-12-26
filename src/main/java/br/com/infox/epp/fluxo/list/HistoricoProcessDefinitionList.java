package br.com.infox.epp.fluxo.list;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.crud.FluxoController;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.HistoricoProcessDefinition;

@Named
@ViewScoped
public class HistoricoProcessDefinitionList extends DataList<HistoricoProcessDefinition> {
	private static final long serialVersionUID = 1L;

	@Inject
	private FluxoController fluxoController;
	
	@Override
	protected String getDefaultOrder() {
		return "dataAlteracao desc";
	}

	@Override
	protected String getDefaultEjbql() {
		return "select o from HistoricoProcessDefinition o";
	}
	
	@Override
	protected String getDefaultWhere() {
		return "where o.fluxo = #{historicoProcessDefinitionList.fluxo}";
	}
	
	public Fluxo getFluxo() {
	    return fluxoController.getFluxo();
	}
}
