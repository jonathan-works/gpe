package br.com.infox.epp.estatistica.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.estatistica.bean.ProdutividadeBean;
import br.com.infox.epp.estatistica.query.ProdutividadeQuery;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(ProdutividadeDAO.NAME)
public class ProdutividadeDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "produtividadeDAO";

	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<ProdutividadeBean> listProdutividade(Map<String, Object> params) {
		String sql = buildSql(params);
		Query query = setParameters(params, getEntityManager().createNativeQuery(sql));
		query = setPaginationParams(params, query);
		List<Object[]> results = query.getResultList();
		List<ProdutividadeBean> produtividades = new ArrayList<>();
		for (Object[] o : results) {
			produtividades.add(buildProdutividade(o));
		}
		return produtividades;
	}
	
	public Long totalProdutividades(Map<String, Object> params) {
		String sql = buildSql(params);
		StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM (");
		sb.append(sql);
		sb.append(") a");
		Query query = setParameters(params, getEntityManager().createNativeQuery(sb.toString()));
		return ((BigInteger) query.getSingleResult()).longValue();
	}
	
	private String buildSql(Map<String, Object> params) {
		StringBuilder sb = new StringBuilder(ProdutividadeQuery.BASE_QUERY);
		if (params.containsKey(ProdutividadeQuery.PARAM_FLUXO)) {
			sb.append(ProdutividadeQuery.INNER_JOIN_FLUXO);
		}
		sb.append(ProdutividadeQuery.CONDICAO_USUARIO);
		if (params.containsKey(ProdutividadeQuery.PARAM_FLUXO)) {
			sb.append(ProdutividadeQuery.CONDICAO_FLUXO);
		}
		if (params.containsKey(ProdutividadeQuery.PARAM_DATA_INICIO)) {
			sb.append(ProdutividadeQuery.CONDICAO_DATA_INICIO);
		}
		if (params.containsKey(ProdutividadeQuery.PARAM_DATA_FIM)) {
			sb.append(ProdutividadeQuery.CONDICAO_DATA_FIM);
		}
		sb.append(ProdutividadeQuery.GROUP_BY);
		return sb.toString();
	}
	
	private Query setParameters(Map<String, Object> params, Query base) {
		base.setParameter(ProdutividadeQuery.PARAM_USUARIO, params.get(ProdutividadeQuery.PARAM_USUARIO));
		if (params.containsKey(ProdutividadeQuery.PARAM_FLUXO)) {
			base.setParameter(ProdutividadeQuery.PARAM_FLUXO, params.get(ProdutividadeQuery.PARAM_FLUXO));
		}
		if (params.containsKey(ProdutividadeQuery.PARAM_DATA_INICIO)) {
			base.setParameter(ProdutividadeQuery.PARAM_DATA_INICIO, params.get(ProdutividadeQuery.PARAM_DATA_INICIO));
		}
		if (params.containsKey(ProdutividadeQuery.PARAM_DATA_FIM)) {
			base.setParameter(ProdutividadeQuery.PARAM_DATA_FIM, params.get(ProdutividadeQuery.PARAM_DATA_FIM));
		}
		return base;
	}

	private Query setPaginationParams(Map<String, Object> params, Query base) {
		if (params.containsKey(ProdutividadeQuery.PARAM_START)) {
			base.setFirstResult((int) params.get(ProdutividadeQuery.PARAM_START));
		}
		if (params.containsKey(ProdutividadeQuery.PARAM_COUNT)) {
			base.setMaxResults((int) params.get(ProdutividadeQuery.PARAM_COUNT));
		}
		return base;
	}
	
	private ProdutividadeBean buildProdutividade(Object[] o) {
		ProdutividadeBean produtividade = new ProdutividadeBean();
		produtividade.setLocalizacao((String) o[ProdutividadeQuery.INDEX_LOCALIZACAO]);
		produtividade.setMaximoTempoGasto((Integer) o[ProdutividadeQuery.INDEX_MAXIMO_TEMPO_GASTO]);
		produtividade.setMediaTempoGasto(((BigDecimal) o[ProdutividadeQuery.INDEX_MEDIA_TEMPO_GASTO]).doubleValue());
		produtividade.setMinimoTempoGasto((Integer) o[ProdutividadeQuery.INDEX_MINIMO_TEMPO_GASTO]);
		produtividade.setPapel((String) o[ProdutividadeQuery.INDEX_PAPEL]);
		produtividade.setQuantidadeTarefas(((BigInteger) o[ProdutividadeQuery.INDEX_QUANTIDADE_TAREFAS]).longValue());
		produtividade.setTarefa((String) o[ProdutividadeQuery.INDEX_TAREFA]);
		produtividade.setTempoPrevisto((Integer) o[ProdutividadeQuery.INDEX_TEMPO_PREVISTO]);
		produtividade.setUsuario((String) o[ProdutividadeQuery.INDEX_USUARIO]);
		return produtividade;
	}
}
