package br.com.infox.epp.processo.situacao.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.localizacao.entity.ProcessoLocalizacaoIbpm;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao;
import br.com.infox.epp.processo.situacao.entity.SituacaoProcesso;
import br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.tarefa.component.tree.PainelEntityNode;
import br.com.infox.hibernate.util.HibernateUtil;

@AutoCreate
@Name(SituacaoProcessoDAO.NAME)
public class SituacaoProcessoDAO extends DAO<SituacaoProcesso> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "situacaoProcessoDAO";
	
	@In(required = false)
	protected Authenticator authenticator;
	
    public final List<Tuple> getRootList(TipoProcesso tipoProcesso) {
        CriteriaQuery<Tuple> criteriaQuery = createBaseCriteriaQueryRoot();
        appendMandatoryFilters(criteriaQuery, tipoProcesso);
        appendTipoProcessoFilters(criteriaQuery, tipoProcesso);
        return getEntityManager().createQuery(criteriaQuery).getResultList();
    }

	public final List<Tuple> getChildrenList(Integer idFluxo, TipoProcesso tipoProcesso) {
		CriteriaQuery<Tuple> criteriaQuery = createBaseCriteriaQueryChildren(idFluxo);
		appendMandatoryFilters(criteriaQuery, tipoProcesso);
		appendTipoProcessoFilters(criteriaQuery, tipoProcesso);
        return getEntityManager().createQuery(criteriaQuery).getResultList();
    }
	
	public final List<Tuple> getCaixaList(TipoProcesso tipoProcesso, Integer idTarefa) {
		TypedQuery<Long> typedCount = getEntityManager().createQuery(getCountSubqueryCaixas(tipoProcesso));
		String countQueryCaixa = HibernateUtil.getQueryString(typedCount);
		String queryCaixas = "select c.idCaixa as idCaixa, "
								  + "c.tarefa.idTarefa as idTarefa, "
								  + "c.nomeCaixa as nomeCaixa, " 
								  + "'" + PainelEntityNode.CAIXA_TYPE + "' as type, "
								  + "( " +  countQueryCaixa +  " and idCaixa = c.idCaixa ) as qtd "
							 + "from Caixa c "
							 + "where c.tarefa.idTarefa = :taskId "
							 + "order by c.nomeCaixa ";
		TypedQuery<Tuple> typedQuery = getEntityManager().createQuery(queryCaixas, Tuple.class);
		typedQuery.setParameter("taskId", idTarefa);
		Map<String, Object> parametersCountSubquery = HibernateUtil.getQueryParams(typedCount);
		for (String key : parametersCountSubquery.keySet()) {
			typedQuery.setParameter(key, parametersCountSubquery.get(key));
		}
        return typedQuery.getResultList();
    }
	
	private CriteriaQuery<Long> getCountSubqueryCaixas(TipoProcesso tipoProcesso) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<SituacaoProcesso> root = criteriaQuery.from(SituacaoProcesso.class);
		criteriaQuery.select(cb.count(root.<Integer>get("idProcesso"))).distinct(true);
		criteriaQuery.where(cb.and());
		appendMandatoryFilters(criteriaQuery, tipoProcesso);
		appendTipoProcessoFilters(criteriaQuery, tipoProcesso);
		return criteriaQuery;
	}
	
	public final List<Integer> getIdProcessosAbertosByIdTarefa(Tuple selected, TipoProcesso tipoProcesso) {
		Integer idTarefa = selected.get("idTarefa", Integer.class);
		CriteriaQuery<Integer> criteriaQuery = createBaseCriteriaQueryProcessosAbertos(idTarefa);
        String nodeType = selected.get("type", String.class);
		if (PainelEntityNode.TASK_TYPE.equals(nodeType)) {
			appendProcessSemCaixaFilter(criteriaQuery);
        } else if (PainelEntityNode.CAIXA_TYPE.equals(nodeType)) {
        	Integer idCaixa = selected.get("idCaixa", Integer.class);
            appendProcessoComCaixaFilter(criteriaQuery, idCaixa);
        }
        appendMandatoryFilters(criteriaQuery, tipoProcesso);
        appendTipoProcessoFilters(criteriaQuery, tipoProcesso);
        return getEntityManager().createQuery(criteriaQuery).getResultList();
    }
	
	private void appendMandatoryFilters(AbstractQuery<?> abstractQuery, TipoProcesso tipoProcesso) {
		appendSigiloProcessoFilter(abstractQuery);
		appendTipoProcessoFilter(abstractQuery, tipoProcesso);
	}
	
	protected void appendTipoProcessoFilters(AbstractQuery<?> abstractQuery, TipoProcesso tipoProcesso) {
		if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
			appendDestinoOrDestinatarioFilter(abstractQuery);
		} else if (TipoProcesso.DOCUMENTO.equals(tipoProcesso)) {
			appendDestinoOrDestinatarioFilter(abstractQuery);
		} else {
			appendPapelLocalizacaoFilter(abstractQuery);
			appendUnidadeDecisoraFilter(abstractQuery);
			appendPerfilTemplateFilter(abstractQuery);
		}
	}
	
	private void appendSigiloProcessoFilter(AbstractQuery<?> principalQuery) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		Root<?> root = principalQuery.getRoots().iterator().next();
		Subquery<Integer> existsSigiloProcesso = principalQuery.subquery(Integer.class);
		Root<SigiloProcesso> sigiloProcesso = existsSigiloProcesso.from(SigiloProcesso.class);
		existsSigiloProcesso.select(cb.literal(1));
		Predicate whereSigiloProcesso = cb.equal(sigiloProcesso.get("processo").get("idProcesso"), root.get("idProcesso"));
		whereSigiloProcesso = cb.and(cb.equal(sigiloProcesso.get("ativo"), true), whereSigiloProcesso);
		existsSigiloProcesso.where(whereSigiloProcesso);
		
		Subquery<Integer> existsSigiloProcessoPermissao = principalQuery.subquery(Integer.class);
		Root<SigiloProcessoPermissao> sigiloProcessoPermissao = existsSigiloProcessoPermissao.from(SigiloProcessoPermissao.class);
		existsSigiloProcessoPermissao.select(cb.literal(1));
		
		Subquery<Integer> subquery3 = existsSigiloProcessoPermissao.subquery(Integer.class);
		Root<SigiloProcesso> sigiloProcesso2 = subquery3.from(SigiloProcesso.class);
		subquery3.select(sigiloProcesso2.get("id").as(Integer.class));
		Predicate predicateSubquery3 = cb.equal(sigiloProcesso2.get("processo").get("idProcesso"), root.get("idProcesso"));
		predicateSubquery3 = cb.and(cb.equal(sigiloProcesso2.get("ativo"), Boolean.TRUE) , predicateSubquery3);
		subquery3.where(predicateSubquery3);
		
		Integer idUsuarioLogado = Authenticator.getUsuarioLogado().getIdUsuarioLogin();
		Predicate whereSigiloProcessoPermissao = sigiloProcessoPermissao.get("sigiloProcesso").get("id").in(subquery3);
		whereSigiloProcessoPermissao = cb.and(cb.equal(sigiloProcessoPermissao.get("ativo"), Boolean.TRUE), whereSigiloProcessoPermissao);
		whereSigiloProcessoPermissao = cb.and(cb.equal(sigiloProcessoPermissao.get("usuario").get("idUsuarioLogin"), idUsuarioLogado), whereSigiloProcessoPermissao);
		existsSigiloProcessoPermissao.where(whereSigiloProcessoPermissao);
		
		Predicate predicate = principalQuery.getRestriction();
		predicate = cb.and(cb.or(cb.not(cb.exists(existsSigiloProcesso)), cb.exists(existsSigiloProcessoPermissao)), predicate);
		principalQuery.where(predicate);
	}
	
	private CriteriaQuery<Integer> createBaseCriteriaQueryProcessosAbertos(Integer idTarefa) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
        Root<SituacaoProcesso> root = criteriaQuery.from(SituacaoProcesso.class);
        criteriaQuery.select(root.<Integer>get("idProcesso"));
        criteriaQuery.groupBy(root.get("idProcesso"));
        criteriaQuery.where(cb.equal(root.get("idTarefa"), idTarefa));
        return criteriaQuery;
    }

    private CriteriaQuery<Tuple> createBaseCriteriaQueryRoot() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = cb.createTupleQuery();
        Root<SituacaoProcesso> root = criteriaQuery.from(SituacaoProcesso.class);
        Selection<String> nomeFluxo = root.<String>get("nomeFluxo").alias("nomeFluxo");
        Selection<Integer> idFluxo = cb.max(root.<Integer>get("idFluxo")).alias("idFluxo");
        Selection<String> type = cb.literal(PainelEntityNode.FLUXO_TYPE).alias("type");
        criteriaQuery.select(cb.tuple(nomeFluxo, idFluxo, type));
        criteriaQuery.groupBy(root.get("nomeFluxo"));
        criteriaQuery.orderBy(cb.asc(root.get("nomeFluxo")));
        criteriaQuery.where(cb.and());
        return criteriaQuery;
    }
    
	private CriteriaQuery<Tuple> createBaseCriteriaQueryChildren(Integer idFluxo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<SituacaoProcesso> from = cq.from(SituacaoProcesso.class);
		Selection<String> nomeTarefa = from.<String>get("nomeTarefa").alias("nomeTarefa");
		Selection<Long> maxIdTask = cb.max(from.<Long>get("idTask")).alias("idTask");
		Selection<Integer> maxIdTarefa = cb.max(from.<Integer>get("idTarefa")).alias("idTarefa");
		Selection<Long> countCaixa = cb.count(from.get("nomeCaixa")).alias("qtdEmCaixa");
		Selection<Long> countProcesso = cb.count(from.get("idProcesso")).alias("qtd");
		Selection<String> type = cb.<String>literal(PainelEntityNode.TASK_TYPE).alias("type");
		cq.select(cb.tuple(nomeTarefa, maxIdTask, maxIdTarefa, countCaixa, countProcesso, type)).distinct(true);
		cq.where(cb.equal(from.get("idFluxo"), idFluxo));
		cq.groupBy(from.get("nomeTarefa"));
		cq.orderBy(cb.asc(from.get("nomeTarefa")));
		return cq;
	}
    
	private void appendDestinoOrDestinatarioFilter(AbstractQuery<?> abstractQuery) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<Integer> subqueryDestino = createSubqueryDestino(abstractQuery);
        Subquery<Integer> subqueryPerfilDestino = createSubqueryPerfilDestino(abstractQuery);
        Subquery<Integer> subqueryDestinatario = createSubqueryDestinatario(abstractQuery);
        Predicate predicateQuery = abstractQuery.getRestriction();
        predicateQuery = 
                cb.and(
                        cb.or(
                                cb.and(
                                    cb.exists(subqueryDestino), 
                                    cb.exists(subqueryPerfilDestino)
                                ),
                                cb.exists(subqueryDestinatario)
                        ),
                        predicateQuery
                );
        abstractQuery.where(predicateQuery);
    }

	private Subquery<Integer> createSubqueryPerfilDestino(AbstractQuery<?> abstractQuery) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<?> root = abstractQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        String metadadoDestino = EppMetadadoProvider.PERFIL_DESTINO.getMetadadoType();
        
        Subquery<Integer> subqueryExistsMetadado = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadadoExists = subqueryExistsMetadado.from(MetadadoProcesso.class);
        subqueryExistsMetadado.select(cb.literal(1));
        Predicate predicateSubqueryExists = cb.and(cb.equal(metadadoExists.get("metadadoType"), metadadoDestino));
        predicateSubqueryExists = cb.and(cb.equal(metadadoExists.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubqueryExists);
        subqueryExistsMetadado.where(predicateSubqueryExists);
        
        Integer idPerfilAtual = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getId();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoDestino));
        predicateSubquery = cb.and(cb.equal(metadado.get("valor"), idPerfilAtual.toString()), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
        predicateSubquery = cb.or(cb.not(cb.exists(subqueryExistsMetadado)), predicateSubquery);
        subquery.where(predicateSubquery);
        return subquery;
    }

    private Subquery<Integer> createSubqueryDestino(AbstractQuery<?> abstractQuery) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		Root<?> root = abstractQuery.getRoots().iterator().next();
		Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        String metadadoDestino = EppMetadadoProvider.LOCALIZACAO_DESTINO.getMetadadoType();
        Integer idLocalizacao = Authenticator.getLocalizacaoAtual().getIdLocalizacao();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoDestino));
        predicateSubquery = cb.and(cb.equal(metadado.get("valor"), idLocalizacao.toString()), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
		return subquery;
	}
	
	private Subquery<Integer> createSubqueryDestinatario(AbstractQuery<?> abstractQuery) { 
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		Root<?> root = abstractQuery.getRoots().iterator().next();
		Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
		Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
		subquery.select(cb.literal(1));
		String metadadoDestinatario = EppMetadadoProvider.PESSOA_DESTINATARIO.getMetadadoType();
		PessoaFisica pessoaFisica = Authenticator.getUsuarioLogado().getPessoaFisica();
		Integer idPessoaFisica = pessoaFisica == null ? -1 : pessoaFisica.getIdPessoa();
		Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoDestinatario));
		predicateSubquery = cb.and(cb.equal(metadado.get("valor"), idPessoaFisica.toString()), predicateSubquery);
		predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
		subquery.where(predicateSubquery);
		return subquery;
	}
    
    private void appendTipoProcessoFilter(AbstractQuery<?> abstractQuery, TipoProcesso tipoProcesso) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<?> root = abstractQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        String metadadoTipoProcesso = EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType();
        Predicate predicateSubquery = cb.equal(metadado.get("metadadoType"), metadadoTipoProcesso);
        if (tipoProcesso != null) {
        	predicateSubquery = cb.and(cb.equal(metadado.get("valor"), tipoProcesso.toString()), predicateSubquery);
        }
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
        Predicate predicate = abstractQuery.getRestriction();
        if (tipoProcesso == null) {
        	predicate = cb.and(cb.not(cb.exists(subquery)), predicate);
        } else {
        	predicate = cb.and(cb.exists(subquery), predicate);
        }
        abstractQuery.where(predicate);
    }
    
    private void appendPapelLocalizacaoFilter(AbstractQuery<?> abstractQuery) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	Root<?> root = abstractQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<ProcessoLocalizacaoIbpm> subRoot = subquery.from(ProcessoLocalizacaoIbpm.class);
        subquery.select(cb.literal(1));
        Localizacao localizacao = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getLocalizacao();
        Integer idLocalizacao = localizacao == null ? 0 : localizacao.getIdLocalizacao();
        Integer idPapel = Authenticator.getPapelAtual().getIdPapel();
        Predicate predicateSubquery = cb.and(cb.equal(subRoot.get("idTaskInstance"), root.get("idTaskInstance")));
        predicateSubquery = cb.and(cb.equal(subRoot.get("localizacao").get("idLocalizacao"), idLocalizacao), predicateSubquery);
        predicateSubquery = cb.and(cb.or(cb.equal(subRoot.get("papel").get("idPapel"), idPapel), cb.isNull(subRoot.get("papel"))), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(subRoot.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
        Predicate predicate = abstractQuery.getRestriction();
        predicate = cb.and(cb.exists(subquery), predicate);
        abstractQuery.where(predicate);
	}
    
    private void appendUnidadeDecisoraFilter(AbstractQuery<?> abstractQuery) {
    	if (authenticator.isUsuarioLogandoInMonocraticaAndColegiada()) {
    		appendUnidadeDecisoraColegiadaFilter(abstractQuery);
    		appendUnidadeDecisoraMonocraticaFilter(abstractQuery);
        } else if (authenticator.isUsuarioLogadoInColegiada()) {
            appendUnidadeDecisoraColegiadaFilter(abstractQuery);
        } else if (authenticator.isUsuarioLogadoInMonocratica()) {
        	appendUnidadeDecisoraMonocraticaFilter(abstractQuery);
        }
	}
    
	private void appendUnidadeDecisoraColegiadaFilter(AbstractQuery<?> abstractQuery) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		Root<?> root = abstractQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        String metadadoUnidadeDecisora = EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA.getMetadadoType();
        Integer idUnidadeDecisora = authenticator.getColegiadaLogada().getIdUnidadeDecisoraColegiada();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoUnidadeDecisora));
        predicateSubquery = cb.and(cb.equal(metadado.get("valor"), idUnidadeDecisora.toString()));
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
        Predicate predicate = abstractQuery.getRestriction();
    	predicate = cb.and(cb.exists(subquery), predicate);
    	abstractQuery.where(predicate);
	}
	
	private void appendUnidadeDecisoraMonocraticaFilter(AbstractQuery<?> abstractQuery) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		Root<?> root = abstractQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        
        String metadadoUnidadeDecisora = EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA.getMetadadoType();
        Integer idUnidadeDecisora = authenticator.getMonocraticaLogada().getIdUnidadeDecisoraMonocratica();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoUnidadeDecisora));
        predicateSubquery = cb.and(cb.equal(metadado.get("valor"), idUnidadeDecisora.toString()), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
        
        Predicate predicate = abstractQuery.getRestriction();
    	predicate = cb.and(cb.exists(subquery), predicate);
    	abstractQuery.where(predicate);
	}
	
	private void appendProcessoComCaixaFilter(AbstractQuery<?> abstractQuery, Integer idCaixa) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	Root<?> root = abstractQuery.getRoots().iterator().next();
    	Predicate predicate = abstractQuery.getRestriction();
    	predicate = cb.and(cb.equal(root.get("idCaixa"), idCaixa), predicate);
    	abstractQuery.where(predicate);
	}

	private void appendProcessSemCaixaFilter(AbstractQuery<?> abstractQuery) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		Root<?> root = abstractQuery.getRoots().iterator().next();
    	Predicate predicate = abstractQuery.getRestriction();
    	predicate = cb.and(cb.isNull(root.get("idCaixa")), predicate);
    	abstractQuery.where(predicate);
	}

    private void appendPerfilTemplateFilter(AbstractQuery<?> abstractQuery) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	Root<?> root = abstractQuery.getRoots().iterator().next();
    	Integer idPerfilTemplate = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getId();
		Predicate predicate = abstractQuery.getRestriction();
		predicate = cb.and(cb.equal(root.get("pooledActor"), idPerfilTemplate.toString()), predicate);
		abstractQuery.where(predicate);
	}

	public boolean canOpenTask(long idTaskInstance, TipoProcesso tipoProcesso) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<SituacaoProcesso> root = criteriaQuery.from(SituacaoProcesso.class);
		criteriaQuery.select(cb.count(root.get("idTaskInstance")));
		criteriaQuery.where(cb.equal(root.get("idTaskInstance"), idTaskInstance));
		appendMandatoryFilters(criteriaQuery, tipoProcesso);
		appendTipoProcessoFilters(criteriaQuery, tipoProcesso);
		Long count = getEntityManager().createQuery(criteriaQuery).getSingleResult();
		return count > 0;
	}

	public boolean canAccessProcesso(Integer idProcesso, TipoProcesso tipoProcesso) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<SituacaoProcesso> root = criteriaQuery.from(SituacaoProcesso.class);
		criteriaQuery.select(cb.count(root.get("idProcesso")));
		criteriaQuery.where(cb.equal(root.get("idProcesso"), idProcesso));
		appendMandatoryFilters(criteriaQuery, tipoProcesso);
		appendTipoProcessoFilters(criteriaQuery, tipoProcesso);
		Long count = getEntityManager().createQuery(criteriaQuery).getSingleResult(); 
		return count > 0;
	}
	
	public Long getIdTaskInstanceByIdProcesso(Integer idProcesso) {
		Map<String, Object> params = new HashMap<>(1);
		params.put(SituacaoProcessoQuery.PARAM_ID_PROCESSO, idProcesso);
		return getNamedSingleResult(SituacaoProcessoQuery.GET_ID_TASK_INSTANCE_BY_ID_PROCESSO, params);
	}
	
}
