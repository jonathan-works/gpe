package br.com.infox.list;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.LocalizacaoFisica;
import br.com.itx.util.ComponentUtil;

@Name(LocalizacaoFisicaList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class LocalizacaoFisicaList extends EntityList<LocalizacaoFisica> {

	public static final String NAME = "localizacaoFisicaList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from LocalizacaoFisica o";
	private static final String DEFAULT_ORDER = "caminhoCompleto";
	
	private static final String R1 = "o.caminhoCompleto like concat(" +
			"#{localizacaoFisicaList.entity.localizacaoFisicaPai.caminhoCompleto}, '%')";
	
	@Override
	protected void addSearchFields() {
		addSearchField("descricao", SearchCriteria.CONTENDO);
		addSearchField("localizacaoFisicaPai", SearchCriteria.CONTENDO, R1);
		addSearchField("ativo", SearchCriteria.IGUAL);
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
		Map<String, String> map = new HashMap<String, String>();
		map.put("localizacaoFisicaPai", "localizacaoFisicaPai.descricao");
		return map;
	}
	
	public static final LocalizacaoFisicaList instance() {
		return ComponentUtil.getComponent(LocalizacaoFisicaList.NAME);
	}
}
