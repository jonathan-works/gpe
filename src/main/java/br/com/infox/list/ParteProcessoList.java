package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.ParteProcesso;

@Name(ParteProcessoList.NAME)
@Scope(ScopeType.PAGE)
public class ParteProcessoList extends EntityList<ParteProcesso> {

	public static final String NAME = "parteProcessoList";
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from ParteProcesso o";
	private static final String DEFAULT_ORDER = "pessoa";
	private static final String REGRA1 = "o.processo.idJbpm = #{org.jboss.seam.bpm.processInstance.id}";
	private static final String REGRA_IS_RESPONSAVEL = "#{jbpmUtil.isUsuarioLogadoResponsavelLocalizacao()}=true or o.ativo=true";

	@Override
	protected void addSearchFields() {
		addSearchField("processo", SearchCriteria.igual);
		addSearchField("pessoa", SearchCriteria.igual);
		addSearchField("processoAtual", SearchCriteria.igual, REGRA1);
	    addSearchField("isResponsavelLocalizacao",SearchCriteria.igual, REGRA_IS_RESPONSAVEL);
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
