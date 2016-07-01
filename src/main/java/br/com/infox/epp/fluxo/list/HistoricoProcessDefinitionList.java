package br.com.infox.epp.fluxo.list;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.HistoricoProcessDefinition;

@Named
@ViewScoped
public class HistoricoProcessDefinitionList extends DataList<HistoricoProcessDefinition> {
	private static final long serialVersionUID = 1L;

	private Integer idFluxo;
	
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
		return "where o.fluxo.idFluxo = #{historicoProcessDefinitionList.idFluxo}";
	}

	public Integer getIdFluxo() {
		return idFluxo;
	}
	
	public void setIdFluxo(Integer idFluxo) {
		this.idFluxo = idFluxo;
	}
}
