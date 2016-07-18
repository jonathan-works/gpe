package br.com.infox.epp.estatistica.produtividade;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.BASE_QUERY;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.CONDICAO_DATA_FIM;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.CONDICAO_DATA_INICIO;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.CONDICAO_FIXA;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.CONDICAO_FLUXO;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.CONDICAO_USUARIO;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.GROUP_BY;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.INDEX_LOCALIZACAO;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.INDEX_MAXIMO_TEMPO_GASTO;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.INDEX_MEDIA_TEMPO_GASTO;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.INDEX_MINIMO_TEMPO_GASTO;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.INDEX_PAPEL;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.INDEX_QUANTIDADE_TAREFAS;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.INDEX_TAREFA;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.INDEX_TEMPO_PREVISTO;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.INDEX_TIPO_PRAZO_TAREFA;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.INDEX_USUARIO;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.INNER_JOIN_FLUXO;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.ORDER_BY;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.PARAM_COUNT;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.PARAM_DATA_FIM;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.PARAM_DATA_INICIO;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.PARAM_FLUXO;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.PARAM_START;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.PARAM_USUARIO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.tarefa.type.PrazoEnum;

@Stateless
@AutoCreate
@Name(ProdutividadeDAO.NAME)
public class ProdutividadeDAO extends DAO<ProdutividadeBean> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "produtividadeDAO";

    @SuppressWarnings(UNCHECKED)
    public List<ProdutividadeBean> listProdutividade(Map<String, Object> params) {
        String sql = buildOrderedSql(params);
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
        Number result = (Number) query.getSingleResult();
        return result.longValue();
    }
    
    private String buildOrderedSql(Map<String, Object> params) {
        return buildSql(params) + ORDER_BY;
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

        String c;
        if (o[INDEX_TIPO_PRAZO_TAREFA] instanceof Character) {
            c = ((Character) o[INDEX_TIPO_PRAZO_TAREFA]).toString();
        } else {
            // SQLServer
            c = (String) o[INDEX_TIPO_PRAZO_TAREFA];
        }
        PrazoEnum tipoPrazo = null;
        if (c != null) {
            tipoPrazo = PrazoEnum.valueOf(c);
        }

        produtividade.setLocalizacao((String) o[INDEX_LOCALIZACAO]);
        produtividade.setMaximoTempoGasto(PrazoEnum.formatTempo((Integer) o[INDEX_MAXIMO_TEMPO_GASTO], tipoPrazo));
        Integer media = ((Number) o[INDEX_MEDIA_TEMPO_GASTO]).intValue();
        produtividade.setMediaTempoGasto(PrazoEnum.formatTempo(media, tipoPrazo));
        produtividade.setMinimoTempoGasto(PrazoEnum.formatTempo((Integer) o[INDEX_MINIMO_TEMPO_GASTO], tipoPrazo));
        produtividade.setPapel((String) o[INDEX_PAPEL]);
        Long quantidadeTarefas = ((Number) o[INDEX_QUANTIDADE_TAREFAS]).longValue();
        produtividade.setQuantidadeTarefas(quantidadeTarefas);
        produtividade.setTarefa((String) o[INDEX_TAREFA]);
        Integer tempo = (Integer) o[INDEX_TEMPO_PREVISTO];
        if (tempo != null) {
            tempo *= 60;
        }
        produtividade.setTempoPrevisto(PrazoEnum.formatTempo(tempo, tipoPrazo));
        produtividade.setUsuario((String) o[INDEX_USUARIO]);
        return produtividade;
    }
}
