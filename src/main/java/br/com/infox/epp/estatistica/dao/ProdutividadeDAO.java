package br.com.infox.epp.estatistica.dao;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.BASE_QUERY;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.CONDICAO_DATA_FIM;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.CONDICAO_DATA_INICIO;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.CONDICAO_FIXA;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.CONDICAO_FLUXO;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.CONDICAO_USUARIO;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.GROUP_BY;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.INDEX_LOCALIZACAO;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.INDEX_MAXIMO_TEMPO_GASTO;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.INDEX_MEDIA_TEMPO_GASTO;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.INDEX_MINIMO_TEMPO_GASTO;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.INDEX_PAPEL;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.INDEX_QUANTIDADE_TAREFAS;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.INDEX_TAREFA;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.INDEX_TEMPO_PREVISTO;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.INDEX_TIPO_PRAZO_TAREFA;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.INDEX_USUARIO;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.INNER_JOIN_FLUXO;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.ORDER_BY;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.PARAM_COUNT;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.PARAM_DATA_FIM;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.PARAM_DATA_INICIO;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.PARAM_FLUXO;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.PARAM_START;
import static br.com.infox.epp.estatistica.query.ProdutividadeQuery.PARAM_USUARIO;

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

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.estatistica.bean.ProdutividadeBean;
import br.com.infox.epp.tarefa.type.PrazoEnum;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(ProdutividadeDAO.NAME)
public class ProdutividadeDAO extends DAO<ProdutividadeBean> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "produtividadeDAO";

	@SuppressWarnings(UNCHECKED)
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
		StringBuilder sb = new StringBuilder(BASE_QUERY);
		if (params.containsKey(PARAM_FLUXO)) {
			sb.append(INNER_JOIN_FLUXO);
		}
		sb.append(CONDICAO_FIXA);
		if (params.containsKey(PARAM_USUARIO)) {
			sb.append(CONDICAO_USUARIO);
		}
		if (params.containsKey(PARAM_FLUXO)) {
			sb.append(CONDICAO_FLUXO);
		}
		if (params.containsKey(PARAM_DATA_INICIO)) {
			sb.append(CONDICAO_DATA_INICIO);
		}
		if (params.containsKey(PARAM_DATA_FIM)) {
			sb.append(CONDICAO_DATA_FIM);
		}
		sb.append(GROUP_BY);
		sb.append(ORDER_BY);
		return sb.toString();
	}
	
	private Query setParameters(Map<String, Object> params, Query base) {
		if (params.containsKey(PARAM_USUARIO)) {
			base.setParameter(PARAM_USUARIO, params.get(PARAM_USUARIO));
		}
		if (params.containsKey(PARAM_FLUXO)) {
			base.setParameter(PARAM_FLUXO, params.get(PARAM_FLUXO));
		}
		if (params.containsKey(PARAM_DATA_INICIO)) {
			base.setParameter(PARAM_DATA_INICIO, params.get(PARAM_DATA_INICIO));
		}
		if (params.containsKey(PARAM_DATA_FIM)) {
			base.setParameter(PARAM_DATA_FIM, params.get(PARAM_DATA_FIM));
		}
		return base;
	}

	private Query setPaginationParams(Map<String, Object> params, Query base) {
		if (params.containsKey(PARAM_START)) {
			base.setFirstResult((int) params.get(PARAM_START));
		}
		if (params.containsKey(PARAM_COUNT)) {
			base.setMaxResults((int) params.get(PARAM_COUNT));
		}
		return base;
	}
	
	private ProdutividadeBean buildProdutividade(Object[] o) {
		ProdutividadeBean produtividade = new ProdutividadeBean();
		
		Character c = (Character) o[INDEX_TIPO_PRAZO_TAREFA];
		PrazoEnum tipoPrazo = null;
		if (c != null) {
			tipoPrazo = PrazoEnum.valueOf(c.toString());
		}
		
		produtividade.setLocalizacao((String) o[INDEX_LOCALIZACAO]);
		produtividade.setMaximoTempoGasto(PrazoEnum.formatTempo((Integer) o[INDEX_MAXIMO_TEMPO_GASTO], tipoPrazo));
		produtividade.setMediaTempoGasto(PrazoEnum.formatTempo(((BigDecimal) o[INDEX_MEDIA_TEMPO_GASTO]).intValue(), tipoPrazo));
		produtividade.setMinimoTempoGasto(PrazoEnum.formatTempo((Integer) o[INDEX_MINIMO_TEMPO_GASTO], tipoPrazo));
		produtividade.setPapel((String) o[INDEX_PAPEL]);
		produtividade.setQuantidadeTarefas(((BigInteger) o[INDEX_QUANTIDADE_TAREFAS]).longValue());
		produtividade.setTarefa((String) o[INDEX_TAREFA]);
		produtividade.setTempoPrevisto(PrazoEnum.formatTempo((Integer) o[INDEX_TEMPO_PREVISTO], tipoPrazo));
		produtividade.setUsuario((String) o[INDEX_USUARIO]);
		return produtividade;
	}
}
