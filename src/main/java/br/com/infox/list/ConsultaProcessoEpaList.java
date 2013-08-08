package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.entity.ProcessoEpa;

@Name(ConsultaProcessoEpaList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class ConsultaProcessoEpaList extends EntityList<ProcessoEpa> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "consultaProcessoEpaList";
	
	private static final String DEFAULT_EJBQL = "select o from ProcessoEpa o";
	private static final String DEFAULT_ORDER = "dataInicio";
	
	private static final String R1 = "o.idProcesso in (#{painelUsuarioHome.processoIdList})";
	private static final String R2 = "o.caixa.idCaixa = #{painelUsuarioHome.idCaixa}";
	private static final String R3 = "o.numeroProcesso = #{consultaProcessoHome.instance.numeroProcesso}";
	private static final String R4 = "o.naturezaCategoriaFluxo.natureza = #{consultaProcessoHome.instance.natureza}";
	private static final String R5 = "o.naturezaCategoriaFluxo.categoria = #{consultaProcessoHome.instance.categoria}";

	@Override
	protected void addSearchFields() {
		addSearchField("idProcesso", SearchCriteria.IGUAL, R1);
		addSearchField("caixa.idCaixa", SearchCriteria.IGUAL, R2);
		addSearchField("numeroProcesso", SearchCriteria.IGUAL, R3);
		addSearchField("naturezaCategoriaFluxo.natureza", SearchCriteria.IGUAL, R4);
		addSearchField("naturezaCategoriaFluxo.categoria", SearchCriteria.IGUAL, R5);
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
		// TODO Auto-generated method stub
		return null;
	}

}
