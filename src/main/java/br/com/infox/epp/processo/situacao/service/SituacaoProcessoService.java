package br.com.infox.epp.processo.situacao.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.localizacao.entity.ProcessoLocalizacaoIbpm;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.situacao.entity.SituacaoProcesso;
import br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.tarefa.component.tree.TarefasTreeHandler;
import br.com.infox.ibpm.util.JbpmUtil;

@AutoCreate
@Scope(ScopeType.STATELESS)
@Name(SituacaoProcessoService.NAME)
public class SituacaoProcessoService implements Serializable {
	
	private static final long serialVersionUID = 1L;
    public static final String NAME = "situacaoProcessoService";
    
	@In
	private EntityManager entityManager;
    @In
    private Authenticator authenticator;
    
    public List<Tuple> getRootList(TipoProcesso tipoProcesso) {
        CriteriaQuery<Tuple> criteriaQuery = createBaseQueryRoot();
        addTipoProcessoFilter(criteriaQuery, tipoProcesso);
        addTipoProcessoFiltersRoot(criteriaQuery, tipoProcesso);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
	
	public List<Tuple> getChildrenList(Integer idFluxo, TipoProcesso tipoProcesso) {
		CriteriaQuery<Tuple> criteriaQuery = createBaseQueryChildren(idFluxo);
		addTipoProcessoFilter(criteriaQuery, tipoProcesso);
		addTipoProcessoFiltersChildren(criteriaQuery, tipoProcesso);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
	
//	public TypedQuery<Tuple> createQueryCaixas(TipoProcesso tipoProcesso) {
//		return putParametroIdPerfilTemplate(putParametrosDosFiltrosDeUnidadesDecisoras(createQuery(createHqlQueryCaixa())));
//	}
	
	public TypedQuery<Tuple> createQueryCaixas(TipoProcesso tipoProcesso) {
    	StringBuilder sb = new StringBuilder(TAREFAS_TREE_QUERY_CAIXAS_BASE);
    	sb.append(TAREFAS_TREE_QUERY_ROOTS_BY_TIPO);
    	putFiltroLocalizacaoAndPessoa(sb);
    	sb.append(TAREFAS_TREE_QUERY_CAIXAS_SUFIX);
    	TypedQuery<Tuple> typedQuery = putParametersLocalizacaoAndPessoa(entityManager.createQuery(sb.toString(), Tuple.class)));
    	typedQuery.setParameter(PARAM_TIPO_PROCESSO, tipoProcesso);
        return typedQuery;
    }
    
    private String createHqlQueryCaixa() {
        String baseQuery = TAREFAS_TREE_QUERY_CAIXAS_BASE + TAREFAS_TREE_FILTER_POOLEDACTOR;
        return putFiltrosDeUnidadesDecisoras(baseQuery) + TAREFAS_TREE_QUERY_CAIXAS_SUFIX;
    }
	
	public List<Integer> getProcessosAbertosByIdTarefa(Integer idTarefa, Tuple selected, TipoProcesso tipoProcesso) {
		CriteriaQuery<Integer> criteriaQuery = createBaseQueryProcessosAbertos(idTarefa);
		String treeType = selected.get("tree", String.class);
        String nodeType = selected.get("type", String.class);
		if ("caixa".equals(treeType) && "Task".equals(nodeType)) {
			addFilterSemCaixa(criteriaQuery);
        }
        if (treeType == null && "Caixa".equals(nodeType)) {
            addFilterComCaixa(criteriaQuery);
        }
        addTipoProcessoFilter(criteriaQuery, tipoProcesso);
        addFilterTipoProcessoProcessosAbertos(criteriaQuery, tipoProcesso);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
    
    public Long getQuantidadeTarefasAtivasByTaskId(long taskId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(SituacaoProcessoQuery.PARAM_ID_TASKINSTANCE, taskId);
        return getNamedSingleResult(SituacaoProcessoQuery.COUNT_TAREFAS_ATIVAS_BY_TASK_ID, parameters);
    }
    
    public boolean canOpenTask(long currentTaskId) {
        JbpmUtil.getJbpmSession().flush();
        Long count = getQuantidadeTarefasAtivasByTaskId(currentTaskId);
        return count != null && count > 0;
    }

	private void addFilterTipoProcessoProcessosAbertos(CriteriaQuery<Integer> criteriaQuery, TipoProcesso tipoProcesso) {
		if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
			addDestinoOrDestinatarioFilter(criteriaQuery);
		} else if (TipoProcesso.DOCUMENTO.equals(tipoProcesso)) {
			addDestinoOrDestinatarioFilter(criteriaQuery);
		} else {
			addUnidadeDecisoraFilter(criteriaQuery);
		}
	}

	protected CriteriaQuery<Integer> createBaseQueryProcessosAbertos(Integer idTarefa) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
        Root<SituacaoProcesso> root = criteriaQuery.from(SituacaoProcesso.class);
        criteriaQuery.select(root.get("idProcesso").as(Integer.class));
        criteriaQuery.groupBy(root.get("idProcesso"));
        
        criteriaQuery.where(cb.equal(root.get("idTarefa"), idTarefa));
        return criteriaQuery;
    }

    private CriteriaQuery<Tuple> createBaseQueryRoot() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = cb.createTupleQuery();
        Root<SituacaoProcesso> root = criteriaQuery.from(SituacaoProcesso.class);
        Selection<String> nomeFluxo = root.get("nomeFluxo").as(String.class).alias("nomeFluxo");
        Selection<Integer> idFluxo = cb.max(root.get("idFluxo").as(Integer.class)).alias("idFluxo");
        Selection<String> type = cb.literal("Fluxo").as(String.class).alias("type");
        criteriaQuery.select(cb.tuple(nomeFluxo, idFluxo, type));
        criteriaQuery.groupBy(root.get("nomeFluxo"));
        criteriaQuery.orderBy(cb.asc(root.get("nomeFluxo")));
        criteriaQuery.where(cb.and());
        return criteriaQuery;
    }
    
	private CriteriaQuery<Tuple> createBaseQueryChildren(Integer idFluxo) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<SituacaoProcesso> from = cq.from(SituacaoProcesso.class);
		Selection<String> nomeTarefa = from.get("nomeTarefa").as(String.class).alias("nomeTarefa");
		Selection<Integer> maxIdTask = cb.max(from.get("idTask").as(Integer.class)).alias("idTask");
		Selection<Integer> maxIdTarefa = cb.max(from.get("idTarefa").as(Integer.class)).alias("idTarefa");
		Selection<Long> countCaixa = cb.count(from.get("nomeCaixa")).as(Long.class).alias("qtdEmCaixa");
		Selection<Long> countProcesso = cb.count(from.get("idProcesso")).as(Long.class).alias("qtd");
		Selection<String> tree = cb.literal("caixa").as(String.class).alias("tree");
		Selection<String> type = cb.literal("Task").as(String.class).alias("type");
		cq.select(cb.tuple(nomeTarefa, maxIdTask, maxIdTarefa, countCaixa, countProcesso, tree, type));
		
		cq.where(cb.equal(from.get("idFluxo"), idFluxo));
		
		cq.groupBy(from.get("nomeTarefa"));
		cq.orderBy(cb.asc(from.get("nomeTarefa")));
		return cq;
	}
    
    protected void addTipoProcessoFiltersRoot(CriteriaQuery<Tuple> criteriaQuery, TipoProcesso tipoProcesso) {
    	if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
    		addDestinoOrDestinatarioFilter(criteriaQuery);
    	} else if (TipoProcesso.DOCUMENTO.equals(tipoProcesso)) {
    		addDestinoOrDestinatarioFilter(criteriaQuery);
    	} else {
            addPapelLocalizacaoFilter(criteriaQuery);
            addUnidadeDecisoraFilter(criteriaQuery);
            addRelatorFilter(criteriaQuery);
    	}
    }
    
	private void addDestinoOrDestinatarioFilter(CriteriaQuery<?> criteriaQuery) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Root<?> from = criteriaQuery.getRoots().iterator().next();
        Subquery<Integer> subqueryDestino = createSubqueryDestino(criteriaQuery, from);
        Subquery<Integer> subqueryDestinatario = createSubqueryDestinatario(criteriaQuery, from);
        
        Predicate predicateQuery = criteriaQuery.getRestriction();
        predicateQuery = cb.and(cb.or(cb.exists(subqueryDestino), cb.exists(subqueryDestinatario)), predicateQuery);
        criteriaQuery.where(predicateQuery);
    }

	private Subquery<Integer> createSubqueryDestino(CriteriaQuery<?> criteriaQuery, Root<?> from) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        
        String metadadoDestino = EppMetadadoProvider.LOCALIZACAO_DESTINO.getMetadadoType();
        Integer idLocalizacao = Authenticator.getLocalizacaoAtual().getIdLocalizacao();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoDestino));
        predicateSubquery = cb.and(cb.equal(metadado.get("valor"), idLocalizacao.toString()), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), from.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
		return subquery;
	}
	
	private Subquery<Integer> createSubqueryDestinatario(CriteriaQuery<?> criteriaQuery, Root<?> from) { 
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
		Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
		subquery.select(cb.literal(1));
      
		String metadadoDestinatario = EppMetadadoProvider.PESSOA_DESTINATARIO.getMetadadoType();
		PessoaFisica pessoaFisica = Authenticator.getUsuarioLogado().getPessoaFisica();
		Integer idPessoaFisica = pessoaFisica == null ? -1 : pessoaFisica.getIdPessoa();
		Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoDestinatario));
		predicateSubquery = cb.and(cb.equal(metadado.get("valor"), idPessoaFisica.toString()), predicateSubquery);
		predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), from.get("idProcesso")), predicateSubquery);
		subquery.where(predicateSubquery);
		return subquery;
	}
    
    private void addTipoProcessoFilter(CriteriaQuery<?> criteriaQuery, TipoProcesso tipoProcesso) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Root<?> root = criteriaQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        
        String metadadoTipoProcesso = EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoTipoProcesso));
        if (tipoProcesso != null) {
        	predicateSubquery = cb.and(cb.equal(metadado.get("valor"), tipoProcesso.toString()), predicateSubquery);
        }
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
        
        Predicate predicate = criteriaQuery.getRestriction();
        if (tipoProcesso == null) {
        	predicate = cb.and(cb.not(cb.exists(subquery)), predicate);
        } else {
        	predicate = cb.and(cb.exists(subquery), predicate);
        }
        criteriaQuery.where(predicate);
    }
    
    private void addPapelLocalizacaoFilter(CriteriaQuery<?> criteriaQuery) {
    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Root<?> root = criteriaQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<ProcessoLocalizacaoIbpm> subRoot = subquery.from(ProcessoLocalizacaoIbpm.class);
        subquery.select(cb.literal(1));
        
        Localizacao localizacaoInternaAtual = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getLocalizacao();
        Papel papel = Authenticator.getPapelAtual();
        Predicate predicateSubquery = cb.and(cb.equal(subRoot.get("idTaskInstance"), root.get("idTaskInstance")));
        predicateSubquery = cb.and(cb.equal(subRoot.get("localizacao"), localizacaoInternaAtual), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(subRoot.get("papel"), papel), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(subRoot.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
        
        Predicate predicate = criteriaQuery.getRestriction();
        predicate = cb.and(cb.exists(subquery), predicate);
        criteriaQuery.where(predicate);
	}
    
    private void addUnidadeDecisoraFilter(CriteriaQuery<?> criteriaQuery) {
    	if (authenticator.isUsuarioLogandoInMonocraticaAndColegiada()) {
    		addFilterUnidadeDecisoraColegiada(criteriaQuery);
    		addFilterUnidadeDecisoraMonocratica(criteriaQuery);
        } else if (authenticator.isUsuarioLogadoInColegiada()) {
            addFilterUnidadeDecisoraColegiada(criteriaQuery);
        } else if (authenticator.isUsuarioLogadoInMonocratica()) {
        	addFilterUnidadeDecisoraMonocratica(criteriaQuery);
        } else {
        	addFilterUnidadeDecisoraColegiada(criteriaQuery);
    		addFilterUnidadeDecisoraMonocratica(criteriaQuery);
        }
	}
    
	private void addFilterUnidadeDecisoraColegiada(CriteriaQuery<?> criteriaQuery) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Root<?> root = criteriaQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        
        String metadadoUnidadeDecisora = EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA.getMetadadoType();
        Integer idUnidadeDecisora = authenticator.getColegiadaLogada().getIdUnidadeDecisoraColegiada();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoUnidadeDecisora));
        predicateSubquery = cb.and(cb.equal(metadado.get("valor"), idUnidadeDecisora.toString()));
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
        
        Predicate predicate = criteriaQuery.getRestriction();
    	predicate = cb.and(cb.exists(subquery), predicate);
        criteriaQuery.where(predicate);
	}
	
	private void addFilterUnidadeDecisoraMonocratica(CriteriaQuery<?> criteriaQuery) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Root<?> root = criteriaQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        
        String metadadoUnidadeDecisora = EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA.getMetadadoType();
        Integer idUnidadeDecisora = authenticator.getMonocraticaLogada().getIdUnidadeDecisoraMonocratica();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoUnidadeDecisora));
        predicateSubquery = cb.and(cb.equal(metadado.get("valor"), idUnidadeDecisora.toString()));
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
        
        Predicate predicate = criteriaQuery.getRestriction();
    	predicate = cb.and(cb.exists(subquery), predicate);
        criteriaQuery.where(predicate);
	}
	
	private void addFilterSemUnidadeDecisora(CriteriaQuery<?> criteriaQuery) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Root<?> root = criteriaQuery.getRoots().iterator().next();
        Subquery<Integer> subqueryMonocratica = criteriaQuery.subquery(Integer.class);
        subqueryMonocratica.select(cb.literal(1));
	}
	
	private void addRelatorFilter(CriteriaQuery<Tuple> criteriaQuery) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Root<?> root = criteriaQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        subquery.select(cb.literal(1));
        
        Predicate predicateSubquery = cb.and(restrictions)
        		
        Predicate predicate = criteriaQuery.getRestriction();
        predicate = cb.and(cb.equal(x, y))
	}
	
	private void addFilterComCaixa(CriteriaQuery<Integer> criteriaQuery) {
    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    	Root<?> root = criteriaQuery.getRoots().iterator().next();
    	Predicate predicate = criteriaQuery.getRestriction();
    	predicate = cb.and(cb.isNotNull(root.get("idCaixa")), predicate);
    	criteriaQuery.where(predicate);
	}

	private void addFilterSemCaixa(CriteriaQuery<Integer> criteriaQuery) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    	Root<?> root = criteriaQuery.getRoots().iterator().next();
    	Predicate predicate = criteriaQuery.getRestriction();
    	predicate = cb.and(cb.isNull(root.get("idCaixa")), predicate);
    	criteriaQuery.where(predicate);
	}

	protected void addTipoProcessoFiltersChildren(CriteriaQuery<?> criteriaQuery, TipoProcesso tipoProcesso) {
		if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
			addDestinoOrDestinatarioFilter(criteriaQuery);
		} else if (TipoProcesso.DOCUMENTO.equals(tipoProcesso)) {
			addDestinoOrDestinatarioFilter(criteriaQuery);
		} else {
			addPerfilTemplateFilter(criteriaQuery);
			addUnidadeDecisoraFilter(criteriaQuery);
		}
	}
	
    private void addPerfilTemplateFilter(CriteriaQuery<?> criteriaQuery) {
    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    	Root<?> from = criteriaQuery.getRoots().iterator().next();
    	Integer idPerfilTemplate = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getId();
		Predicate predicate = criteriaQuery.getRestriction();
		predicate = cb.and(cb.equal(from.get("pooledActor"), idPerfilTemplate.toString()), predicate);
		criteriaQuery.where(predicate);
	}

}
