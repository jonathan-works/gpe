package br.com.infox.epp.processo.consulta.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.processo.entity.ProcessoEpa;

@Name(ConsultaProcessoEpaList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class ConsultaProcessoEpaList extends EntityList<ProcessoEpa> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "consultaProcessoEpaList";
	
	private static final String DEFAULT_EJBQL = "select o from ProcessoEpa o";
	private static final String DEFAULT_ORDER = "o.prioridadeProcesso, o.dataInicio";
	
	private static final String R1 = "o.idProcesso in (#{painelUsuarioHome.processoIdList})";
	private static final String R2 = "o.caixa.idCaixa = #{painelUsuarioHome.idCaixa}";
	private static final String R3 = "o.numeroProcesso = #{consultaProcessoHome.instance.numeroProcesso}";
	private static final String R4 = "o.naturezaCategoriaFluxo.natureza = #{consultaProcessoHome.instance.natureza}";
	private static final String R5 = "o.naturezaCategoriaFluxo.categoria = #{consultaProcessoHome.instance.categoria}";
	private static final String R6 = "cast(o.dataInicio as date) >= #{consultaProcessoHome.instance.dataInicio}";
	private static final String R7 = "cast(o.dataInicio as date) <= #{consultaProcessoHome.instance.dataFim}";

	@Override
	protected void addSearchFields() {
		addSearchField("idProcesso", SearchCriteria.IGUAL, R1);
		addSearchField("caixa.idCaixa", SearchCriteria.IGUAL, R2);
		addSearchField("numeroProcesso", SearchCriteria.IGUAL, R3);
		addSearchField("naturezaCategoriaFluxo.natureza", SearchCriteria.IGUAL, R4);
		addSearchField("naturezaCategoriaFluxo.categoria", SearchCriteria.IGUAL, R5);
		addSearchField("dataInicio", SearchCriteria.MAIOR_IGUAL,R6);
		addSearchField("dataFim", SearchCriteria.MENOR_IGUAL,R7);
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
