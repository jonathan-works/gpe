package br.com.infox.epp.processo.situacao.dao;

import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.AND;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.COM_CAIXA_COND;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.COUNT_TAREFAS_ATIVAS_BY_TASK_ID;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.FILTRO_LOCALIZACAO_DESTINO;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.FILTRO_PESSOA_DESTINATARIO;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.GROUP_BY_PROCESSO_SUFIX;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.ID_TAREFA_PARAM;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.OR;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PARAM_COLEGIADA_LOGADA;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PARAM_ID_LOCALIZACAO;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PARAM_ID_PESSOA;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PARAM_ID_TASKINSTANCE;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PARAM_MONOCRATICA_LOGADA;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PARAM_TIPO_PROCESSO;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PROCESSOS_ABERTOS_BASE_QUERY;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PROCESSOS_COM_COLEGIADA_COND;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PROCESSOS_COM_COLEGIADA_E_MONOCRATICA_COND;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PROCESSOS_COM_MONOCRATICA_COND;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PROCESSOS_SEM_COLEGIADA_NEM_MONOCRATICA_COND;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.SEM_CAIXA_COND;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.TAREFAS_TREE_FILTER_POOLEDACTOR;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.TAREFAS_TREE_QUERY_CAIXAS_BASE;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.TAREFAS_TREE_QUERY_CAIXAS_SUFIX;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.TAREFAS_TREE_QUERY_CHILDREN_BASE;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.TAREFAS_TREE_QUERY_CHILDREN_SUFIX;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.TAREFAS_TREE_QUERY_ROOTS_BASE;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.TAREFAS_TREE_QUERY_ROOTS_BY_TIPO;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.TAREFAS_TREE_QUERY_ROOTS_SEM_TIPO;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.TAREFAS_TREE_QUERY_ROOTS_SUFIX;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Events;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.situacao.entity.SituacaoProcesso;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.tarefa.component.tree.TarefasTreeHandler;
import br.com.infox.ibpm.util.JbpmUtil;

@AutoCreate
@Name(SituacaoProcessoDAO.NAME)
public class SituacaoProcessoDAO extends DAO<SituacaoProcesso> {
    
    @In
    private Authenticator authenticator;
    
    private static final long serialVersionUID = 1L;
    public static final String NAME = "situacaoProcessoDAO";

    public Long getQuantidadeTarefasAtivasByTaskId(long taskId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_TASKINSTANCE, taskId);
        return getNamedSingleResult(COUNT_TAREFAS_ATIVAS_BY_TASK_ID, parameters);
    }

    @SuppressWarnings("unchecked")
	public <T> List<T> getQueryProcessoAbertosByIdTarefa(Integer idTarefa, Map<String, Object> selected) {
        String hql = putFiltrosDeUnidadesDecisoras(getHqlQueryBaseProcessosAbertos(selected)) + GROUP_BY_PROCESSO_SUFIX;
        Query query = getEntityManager().createQuery(hql);
        query.setParameter(ID_TAREFA_PARAM, idTarefa);
        return putParametrosDosFiltrosDeUnidadesDecisoras(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public <T> List<T> getProcessosAbertosByIdTarefaAndTipoProcesso(Integer idTarefa, Map<String, Object> selected, TipoProcesso tipoProcesso) {
    	StringBuilder sb = new StringBuilder(getHqlQueryBaseProcessosAbertos(selected));
    	sb.append(TAREFAS_TREE_QUERY_ROOTS_BY_TIPO);
    	putFiltroLocalizacaoAndPessoa(sb);
    	sb.append(GROUP_BY_PROCESSO_SUFIX);
    	Query query = putParametersLocalizacaoAndPessoa(createQuery(sb.toString()));
        query.setParameter(ID_TAREFA_PARAM, idTarefa);
        query.setParameter(PARAM_TIPO_PROCESSO, tipoProcesso.toString());
        return query.getResultList();
    }

    protected String getHqlQueryBaseProcessosAbertos(Map<String, Object> selected) {
        String treeType = (String) selected.get("tree");
        String nodeType = (String) selected.get("type");
        if ("caixa".equals(treeType) && "Task".equals(nodeType)) {
            return PROCESSOS_ABERTOS_BASE_QUERY + SEM_CAIXA_COND;
        }
        if (treeType == null && "Caixa".equals(nodeType)) {
            return PROCESSOS_ABERTOS_BASE_QUERY + COM_CAIXA_COND;
        }
        return PROCESSOS_ABERTOS_BASE_QUERY;
    }
    

    public boolean canOpenTask(long currentTaskId) {
        JbpmUtil.getJbpmSession().flush();
        Events.instance().raiseEvent(TarefasTreeHandler.FILTER_TAREFAS_TREE);
        Long count = getQuantidadeTarefasAtivasByTaskId(currentTaskId);
        return count != null && count > 0;
    }
    
    @SuppressWarnings("unchecked")
	public List<Tuple> getRootList(TipoProcesso tipoProcesso) {
        CriteriaQuery<Tuple> criteriaQuery = createBaseQueryRoot();
        addTipoProcessoFilter(criteriaQuery, tipoProcesso);
        
        
    	Query query = putParametrosDosFiltrosDeUnidadesDecisoras(createQuery(createHqlQueryRoots()));
        return query.getResultList();
    }
    
    private CriteriaQuery<Tuple> createBaseQueryRoot() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = cb.createTupleQuery();
        Root<SituacaoProcesso> root = criteriaQuery.from(SituacaoProcesso.class);
        Selection<String> nomeFluxo = root.get("nomeFluxo").as(String.class).alias("nomeFluxo");
        Selection<Integer> idFluxo = root.get("idFluxo").as(Integer.class).alias("idFluxo");
        Selection<String> type = cb.literal("Fluxo").as(String.class).alias("type");
        criteriaQuery.select(cb.tuple(nomeFluxo, idFluxo, type));
        criteriaQuery.groupBy(root.get("nomeFluxo"));
        criteriaQuery.orderBy(cb.asc(root.get("nomeFluxo")));
        criteriaQuery.where(cb.and());
        return criteriaQuery;
    }
    
    private void addTipoProcessoFilter(CriteriaQuery<Tuple> criteriaQuery, TipoProcesso tipoProcesso) {
        switch (tipoProcesso) {
            case COMUNICACAO:
                addProcessoComunicacaoFilter(criteriaQuery);
                break;
            case DOCUMENTO:
                addProcessoDocumentoFilter(criteriaQuery);
                break;
            default:
                addProcessoSemTipoFilter(criteriaQuery);
                break;
        }
    }
    
    private void addProcessoComunicacaoFilter(CriteriaQuery<Tuple> criteriaQuery) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<?> root = criteriaQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        Predicate predicate = criteriaQuery.getRestriction();
        cb.and(cb.equal(metadado.get("metaddoType"), EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType()), predicate);
        cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicate);
        subquery.where(predicate);
        
    }
    
    private void addProcessoDocumentoFilter(CriteriaQuery<Tuple> criteriaQuery) {
        
    }
    
    private void addProcessoSemTipoFilter(CriteriaQuery<Tuple> criteriaQuery) {
        
    }
    
//    @SuppressWarnings("unchecked")
//	public <T> List<T> getRootList(TipoProcesso tipoProcesso) {
//    	StringBuilder sb = new StringBuilder(TAREFAS_TREE_QUERY_ROOTS_BASE);
//    	sb.append(TAREFAS_TREE_QUERY_ROOTS_BY_TIPO);
//    	putFiltroLocalizacaoAndPessoa(sb);
//    	sb.append(TAREFAS_TREE_QUERY_ROOTS_SUFIX);
//    	Query query = putParametersLocalizacaoAndPessoa(createQuery(sb.toString()));
//    	query.setParameter(PARAM_TIPO_PROCESSO, tipoProcesso.name());
//        List<T> l = query.getResultList();
//        return l;
//    }
    
    @SuppressWarnings("unchecked")
	public <E> List<E> getChildrenList(Integer idFluxo) {
    	Query query = putParametroIdPerfilTemplate(putParametrosDosFiltrosDeUnidadesDecisoras(createQuery(createHqlQueryChildren())));
    	query.setParameter("idFluxo", idFluxo);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public <E> List<E> getChildrenComunicacaoDocumentoList(Integer idFluxo) {
    	StringBuilder sb = new StringBuilder(TAREFAS_TREE_QUERY_CHILDREN_BASE);
    	putFiltroLocalizacaoAndPessoa(sb);
    	sb.append(TAREFAS_TREE_QUERY_CHILDREN_SUFIX);
    	Query query = putParametersLocalizacaoAndPessoa(createQuery(sb.toString()));
    	query.setParameter("idFluxo", idFluxo);
        return query.getResultList();
    }
    
    private void putFiltroLocalizacaoAndPessoa(StringBuilder sb) {
    	if ( Authenticator.getUsuarioLogado().getPessoaFisica() != null ) {
    		sb.append(AND).append(" ( ");
    		sb.append(FILTRO_LOCALIZACAO_DESTINO).append(OR);
    		sb.append(FILTRO_PESSOA_DESTINATARIO).append(" ) ");
    	} else {
    		sb.append(AND).append(FILTRO_LOCALIZACAO_DESTINO);
    	}
    }
    
    private Query putParametersLocalizacaoAndPessoa(Query query){
    	PessoaFisica pessoaLogada = Authenticator.getUsuarioLogado().getPessoaFisica();
    	query.setParameter(PARAM_ID_LOCALIZACAO, Authenticator.getLocalizacaoAtual().getIdLocalizacao());
    	if (pessoaLogada != null) {
    		query.setParameter(PARAM_ID_PESSOA, pessoaLogada.getIdPessoa());
    	}
    	return query;
    }
    
    public Query createQueryCaixas() {
        return putParametroIdPerfilTemplate(putParametrosDosFiltrosDeUnidadesDecisoras(createQuery(createHqlQueryCaixa())));
    }
    
    public Query createQueryCaixas(TipoProcesso tipoProcesso) {
    	StringBuilder sb = new StringBuilder(TAREFAS_TREE_QUERY_CAIXAS_BASE);
    	sb.append(TAREFAS_TREE_QUERY_ROOTS_BY_TIPO);
    	putFiltroLocalizacaoAndPessoa(sb);
    	sb.append(TAREFAS_TREE_QUERY_CAIXAS_SUFIX);
    	Query query = putParametersLocalizacaoAndPessoa(createQuery(sb.toString()));
    	query.setParameter(PARAM_TIPO_PROCESSO, tipoProcesso.toString());
        return query;
    }
    
    private String createHqlQueryRoots() {
        String baseQuery = TAREFAS_TREE_QUERY_ROOTS_BASE + TAREFAS_TREE_QUERY_ROOTS_SEM_TIPO;
        return putFiltrosDeUnidadesDecisoras(baseQuery) + TAREFAS_TREE_QUERY_ROOTS_SUFIX;
    }
    
    private String createHqlQueryChildren() {
        String baseQuery = TAREFAS_TREE_QUERY_CHILDREN_BASE + TAREFAS_TREE_FILTER_POOLEDACTOR;
        return putFiltrosDeUnidadesDecisoras(baseQuery) + TAREFAS_TREE_QUERY_CHILDREN_SUFIX;
    }
    
    private String createHqlQueryCaixa() {
        String baseQuery = TAREFAS_TREE_QUERY_CAIXAS_BASE + TAREFAS_TREE_FILTER_POOLEDACTOR;
        return putFiltrosDeUnidadesDecisoras(baseQuery) + TAREFAS_TREE_QUERY_CAIXAS_SUFIX;
    }
    
    private String putFiltrosDeUnidadesDecisoras(String baseQuery) {
        if (authenticator.isUsuarioLogandoInMonocraticaAndColegiada()) {
            return baseQuery + PROCESSOS_COM_COLEGIADA_E_MONOCRATICA_COND;
        } else if (authenticator.isUsuarioLogadoInColegiada()) {
            return baseQuery + PROCESSOS_COM_COLEGIADA_COND;
        } else if (authenticator.isUsuarioLogadoInMonocratica()) {
            return baseQuery + PROCESSOS_COM_MONOCRATICA_COND;
        } else {
            return baseQuery + PROCESSOS_SEM_COLEGIADA_NEM_MONOCRATICA_COND;
        }
    }

    private Query putParametrosDosFiltrosDeUnidadesDecisoras(Query query) {
        if (authenticator.getColegiadaLogada() != null) {
            query.setParameter(PARAM_COLEGIADA_LOGADA, authenticator.getColegiadaLogada().getIdUnidadeDecisoraColegiada().toString());
        }
        if (authenticator.isUsuarioLogadoInMonocratica()) {
            query.setParameter(PARAM_MONOCRATICA_LOGADA, authenticator.getMonocraticaLogada().getIdUnidadeDecisoraMonocratica().toString());
        }
        return query;
    }
    
    private Query putParametroIdPerfilTemplate(Query query) {
        return query.setParameter("idPerfilTemplate", Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getId().toString());
    }
}
