package br.com.infox.list;

import java.util.Map;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.ParteProcesso;

public class HistoricoParteProcessoList extends EntityList<ParteProcesso> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "historicoParteProcessoList";
	
	private static final String DEFAULT_EJBQL = "select o from HistoricoParteProcesso o";
	private static final String DEFAULT_ORDER = "dataModificacao DESC";
	private static final String R1 = "parte = #{parteProcessoHome.instance}";

	@Override
	protected void addSearchFields() {
		addSearchField("parteProcesso", SearchCriteria.igual, R1);
		addSearchField("responsavelPorModificacao", SearchCriteria.igual);
		addSearchField("dataModificacao", SearchCriteria.dataIgual);
		addSearchField("motivoModificacao", SearchCriteria.contendo);
		addSearchField("nomeParte", SearchCriteria.contendo);
		addSearchField("ativo", SearchCriteria.igual);
	}

	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}
	
	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}
	
	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}

}
