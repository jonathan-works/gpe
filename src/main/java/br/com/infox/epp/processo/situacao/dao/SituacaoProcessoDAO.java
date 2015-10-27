package br.com.infox.epp.processo.situacao.dao;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.Component;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.localizacao.entity.ProcessoLocalizacaoIbpm;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso_;
import br.com.infox.epp.processo.situacao.entity.SituacaoProcesso;
import br.com.infox.epp.processo.situacao.entity.SituacaoProcesso_;
import br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.tarefa.component.tree.PainelEntityNode;
import br.com.infox.hibernate.util.HibernateUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SituacaoProcessoDAO {

	@Inject
	private EntityManager entityManager;
	
	private Authenticator authenticator = (Authenticator) Component.getInstance(Authenticator.class);
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public Tuple getRoot(Integer idFluxo, TipoProcesso tipoProcesso, boolean expedidas) {
        CriteriaQuery<Tuple> criteriaQuery = createQueryRootList(tipoProcesso, expedidas);
        appendIdFluxoFilter(criteriaQuery, idFluxo);
        TypedQuery<Tuple> createQuery = getEntityManager().createQuery(criteriaQuery);
        return createQuery.setMaxResults(1).getResultList().get(0);
    }

	private void appendIdFluxoFilter(CriteriaQuery<Tuple> criteriaQuery, Integer idFluxo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		Root<?> root = criteriaQuery.getRoots().iterator().next();
		Predicate predicate = criteriaQuery.getRestriction();
        predicate = cb.and(cb.equal(root.<Integer>get("idFluxo"), idFluxo), predicate);
        criteriaQuery.where(predicate);
	}
	
	 public List<Tuple> getRootList(TipoProcesso tipoProcesso, boolean comunicacoesExpedidas, String numeroProcessoRoot) {
	        CriteriaQuery<Tuple> criteriaQuery = createQueryRootList(tipoProcesso, comunicacoesExpedidas);
	        
	        TypedQuery<Tuple> typedQuery;
			if (numeroProcessoRoot != null && !numeroProcessoRoot.isEmpty()) {
				appendNumeroProcessoRootFilter(criteriaQuery);
				typedQuery = getEntityManager().createQuery(criteriaQuery);
				typedQuery.setParameter("numeroProcessoRoot", numeroProcessoRoot);
			} else {
				typedQuery = getEntityManager().createQuery(criteriaQuery);
			}
	        return typedQuery.getResultList();
	    }

	private CriteriaQuery<Tuple> createQueryRootList(TipoProcesso tipoProcesso, boolean comunicacoesExpedidas) {
		CriteriaQuery<Tuple> criteriaQuery = createBaseCriteriaQueryRoot();
        appendMandatoryFilters(criteriaQuery, tipoProcesso);
        appendTipoProcessoFilters(criteriaQuery, tipoProcesso, comunicacoesExpedidas);
		return criteriaQuery;
	}
	
	public List<Tuple> getChildrenList(Integer idFluxo, TipoProcesso tipoProcesso, Boolean comunicacoesExpedidas, String numeroProcessoRoot) {
		CriteriaQuery<Tuple> criteriaQuery = createBaseCriteriaQueryChildren(idFluxo);
		appendMandatoryFilters(criteriaQuery, tipoProcesso);
		appendTipoProcessoFilters(criteriaQuery, tipoProcesso, comunicacoesExpedidas);
		
		TypedQuery<Tuple> typedQuery;
		if (numeroProcessoRoot != null && !numeroProcessoRoot.isEmpty()) {
			appendNumeroProcessoRootFilter(criteriaQuery);
			typedQuery = getEntityManager().createQuery(criteriaQuery);
			typedQuery.setParameter("numeroProcessoRoot", numeroProcessoRoot);
		} else {
			typedQuery = getEntityManager().createQuery(criteriaQuery);
		}
        return typedQuery.getResultList();
    }
	
	public List<Tuple> getCaixaList(TipoProcesso tipoProcesso, Integer idTarefa, Boolean comunicacoesExpedidas, String numeroProcessoRoot) {
		TypedQuery<Long> typedCount = getEntityManager().createQuery(getCountSubqueryCaixas(tipoProcesso, comunicacoesExpedidas));
		String countQueryCaixa = HibernateUtil.getQueryString(typedCount);
		String queryCaixas = "select c.idCaixa as idCaixa, "
								  + "c.tarefa.idTarefa as idTarefa, "
								  + "c.nomeCaixa as nomeCaixa, " 
								  + "'" + PainelEntityNode.CAIXA_TYPE + "' as type, "
								  + "( " +  countQueryCaixa +  " and idCaixa = c.idCaixa ) as qtd "
							 + "from Caixa c "
							 + "where c.tarefa.idTarefa = :taskId ";
							 
		if (numeroProcessoRoot != null && !numeroProcessoRoot.isEmpty()) {
			queryCaixas += "and exists (select 1 from Processo p "
							 	+ "inner join p.processoRoot r "
							 	+ "where p.caixa = c and r.numeroProcesso like concat('%', :numeroProcessoRoot, '%')) "
							 + "order by c.nomeCaixa ";
		}
		TypedQuery<Tuple> typedQuery = getEntityManager().createQuery(queryCaixas, Tuple.class);
		typedQuery.setParameter("taskId", idTarefa);
		if (numeroProcessoRoot != null && !numeroProcessoRoot.isEmpty()) {
			typedQuery.setParameter("numeroProcessoRoot", numeroProcessoRoot);
		}
		Map<String, Object> parametersCountSubquery = HibernateUtil.getQueryParams(typedCount);
		for (String key : parametersCountSubquery.keySet()) {
			typedQuery.setParameter(key, parametersCountSubquery.get(key));
		}
        return typedQuery.getResultList();
    }
	
	private CriteriaQuery<Long> getCountSubqueryCaixas(TipoProcesso tipoProcesso, Boolean comunicacoesExpedidas) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<SituacaoProcesso> root = criteriaQuery.from(SituacaoProcesso.class);
		criteriaQuery.select(cb.countDistinct(root.<Integer>get("idProcesso"))).distinct(true);
		criteriaQuery.where(cb.and());
		appendMandatoryFilters(criteriaQuery, tipoProcesso);
		appendTipoProcessoFilters(criteriaQuery, tipoProcesso, comunicacoesExpedidas);
		return criteriaQuery;
	}
	
	public List<Integer> getIdProcessosAbertosByIdTarefa(Tuple selected, TipoProcesso tipoProcesso, Boolean comunicacoesExpedidas, String numeroProcessoRoot) {
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
        appendTipoProcessoFilters(criteriaQuery, tipoProcesso, comunicacoesExpedidas);
        
        TypedQuery<Integer> typedQuery;
		if (numeroProcessoRoot != null && !numeroProcessoRoot.isEmpty()) {
			appendNumeroProcessoRootFilter(criteriaQuery);
			typedQuery = getEntityManager().createQuery(criteriaQuery);
			typedQuery.setParameter("numeroProcessoRoot", numeroProcessoRoot);
		} else {
			typedQuery = getEntityManager().createQuery(criteriaQuery);
		}
		
        return typedQuery.getResultList();
    }
	
    public void appendMandatoryFilters(AbstractQuery<?> abstractQuery, TipoProcesso tipoProcesso) {
		appendSigiloProcessoFilter(abstractQuery);
		appendTipoProcessoFilter(abstractQuery, tipoProcesso);
	}
	
    protected void appendNumeroProcessoRootFilter(AbstractQuery<?> abstractQuery) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	Root<?> root = abstractQuery.getRoots().iterator().next();
    	Subquery<Integer> subqueryProcesso = abstractQuery.subquery(Integer.class);
    	Root<Processo> processo = subqueryProcesso.from(Processo.class);
    	Join<Processo, Processo> processoRoot = processo.join(Processo_.processoRoot);
    	subqueryProcesso.where(
    			cb.like(processoRoot.get(Processo_.numeroProcesso), 
    					cb.concat(cb.concat(cb.literal("%"), cb.parameter(String.class, "numeroProcessoRoot")), cb.literal("%"))),
				cb.equal(root.get(SituacaoProcesso_.idProcesso.getName()), processo.get(Processo_.idProcesso)));
    	subqueryProcesso.select(cb.literal(1));

    	Predicate predicate = abstractQuery.getRestriction();
    	predicate = cb.and(predicate, cb.exists(subqueryProcesso));
    	abstractQuery.where(predicate);
    }
    
    public void appendTipoProcessoFilters(AbstractQuery<?> abstractQuery, TipoProcesso tipoProcesso) {
        appendTipoProcessoFilters(abstractQuery, tipoProcesso, Boolean.FALSE);
    }

    public void appendTipoProcessoFilters(AbstractQuery<?> abstractQuery, TipoProcesso tipoProcesso, Boolean comunicacoesExpedidas) {
		if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
		    if (comunicacoesExpedidas != null && comunicacoesExpedidas) {
		        appendLocalizacaoExpedidoraFilter(abstractQuery);
		    } else {
		        appendDestinoOrDestinatarioFilter(abstractQuery);
		    }
		} else if (TipoProcesso.DOCUMENTO.equals(tipoProcesso)) {
			appendPapelLocalizacaoFilter(abstractQuery);
			appendPerfilTemplateFilter(abstractQuery);
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
		whereSigiloProcesso = cb.and(cb.isTrue(sigiloProcesso.get(SigiloProcesso_.sigiloso)), whereSigiloProcesso);
		existsSigiloProcesso.where(whereSigiloProcesso);
		
		Subquery<Integer> existsSigiloProcessoPermissao = principalQuery.subquery(Integer.class);
		Root<SigiloProcessoPermissao> sigiloProcessoPermissao = existsSigiloProcessoPermissao.from(SigiloProcessoPermissao.class);
		existsSigiloProcessoPermissao.select(cb.literal(1));
		
		Subquery<Integer> subquery3 = existsSigiloProcessoPermissao.subquery(Integer.class);
		Root<SigiloProcesso> sigiloProcesso2 = subquery3.from(SigiloProcesso.class);
		subquery3.select(sigiloProcesso2.get("id").as(Integer.class));
		Predicate predicateSubquery3 = cb.equal(sigiloProcesso2.get(SigiloProcesso_.processo).get(Processo_.idProcesso), root.get("idProcesso"));
		predicateSubquery3 = cb.and(cb.isTrue(sigiloProcesso2.get(SigiloProcesso_.ativo)) , predicateSubquery3);
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
        Selection<Long> countFluxo = cb.countDistinct(root.get(SituacaoProcesso_.idTaskInstance)).alias("qtProcesso");
        criteriaQuery.select(cb.tuple(nomeFluxo, idFluxo, type, countFluxo));
        criteriaQuery.groupBy(root.get("nomeFluxo"));
        criteriaQuery.orderBy(cb.asc(root.get("nomeFluxo")));
        criteriaQuery.where(cb.and());
        return criteriaQuery;
    }
    
	private CriteriaQuery<Tuple> createBaseCriteriaQueryChildren(Integer idFluxo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<SituacaoProcesso> from = cq.from(SituacaoProcesso.class);
		Selection<String> nomeTarefa = from.get(SituacaoProcesso_.nomeTarefa).alias("nomeTarefa");
		Selection<Long> maxIdTask = cb.max(from.get(SituacaoProcesso_.idTask)).alias("idTask");
		Selection<Integer> maxIdTarefa = cb.max(from.get(SituacaoProcesso_.idTarefa)).alias("idTarefa");
		Selection<Long> countCaixa = cb.count(from.get(SituacaoProcesso_.idCaixa)).alias("qtdEmCaixa");
		Selection<Long> countProcesso = cb.countDistinct(from.get(SituacaoProcesso_.idTaskInstance)).alias("qtd");
		Selection<String> type = cb.<String>literal(PainelEntityNode.TASK_TYPE).alias("type");
		cq.select(cb.tuple(nomeTarefa, maxIdTask, maxIdTarefa, countCaixa, countProcesso, type));
		cq.where(cb.equal(from.get("idFluxo"), idFluxo));
		cq.groupBy(from.get("nomeTarefa"));
		cq.orderBy(cb.asc(from.get("nomeTarefa")));
		return cq;
	}
    
	protected void appendDestinoOrDestinatarioFilter(AbstractQuery<?> abstractQuery) {
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

	protected void appendLocalizacaoExpedidoraFilter(AbstractQuery<?> abstractQuery) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    Subquery<Integer> subqueryLocalizacaoExpediu = createSubqueryLocalizacaoExpediu(abstractQuery);
	    Predicate predicateQuery = abstractQuery.getRestriction();
	    predicateQuery = cb.and(cb.exists(subqueryLocalizacaoExpediu), predicateQuery);
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
    
    private Subquery<Integer> createSubqueryLocalizacaoExpediu(AbstractQuery<?> abstractQuery) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<?> root = abstractQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<Processo> processo = subquery.from(Processo.class);
        subquery.select(cb.literal(1));
        Integer idLocalizacao = Authenticator.getLocalizacaoAtual().getIdLocalizacao();
        Predicate predicateSubQuery = cb.and(cb.equal(root.get("idProcesso"), processo.get("idProcesso")));
        predicateSubQuery = cb.and(cb.equal(processo.get("localizacao"), idLocalizacao), predicateSubQuery);
        subquery.where(predicateSubQuery);
        return subquery;
    }
	
	private Subquery<Integer> createSubqueryDestinatario(AbstractQuery<?> abstractQuery) { 
		String metadadoDestinatario = EppMetadadoProvider.PESSOA_DESTINATARIO.getMetadadoType();

		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		Root<?> root = abstractQuery.getRoots().iterator().next();
		Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
		Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
		subquery.select(cb.literal(1));
		Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoDestinatario));
		predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
		
		subquery.where(getRestrictionDestinatarioComunicacao(abstractQuery, subquery), predicateSubquery);
		return subquery;
	}
	
	@SuppressWarnings("unchecked")
	protected Predicate getRestrictionDestinatarioComunicacao(AbstractQuery<?> abstractQuery, Subquery<Integer> subqueryDestinatario) {
		PessoaFisica pessoaFisica = Authenticator.getUsuarioLogado().getPessoaFisica();
		Integer idPessoaFisica = pessoaFisica == null ? -1 : pessoaFisica.getIdPessoa();

		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		Root<MetadadoProcesso> metadado = (Root<MetadadoProcesso>) subqueryDestinatario.getRoots().iterator().next();
		return cb.equal(metadado.get("valor"), idPessoaFisica.toString());
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
    
    protected void appendPapelLocalizacaoFilter(AbstractQuery<?> abstractQuery) {
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
    
    protected void appendUnidadeDecisoraFilter(AbstractQuery<?> abstractQuery) {
    	if (authenticator.isUsuarioLogandoInMonocraticaAndColegiada()) {
    		appendUnidadeDecisoraColegiadaFilter(abstractQuery);
    		appendUnidadeDecisoraMonocraticaFilter(abstractQuery);
        } else if (authenticator.isUsuarioLogadoInColegiada()) {
            appendUnidadeDecisoraColegiadaFilter(abstractQuery);
        } else if (authenticator.isUsuarioLogadoInMonocratica()) {
        	appendUnidadeDecisoraMonocraticaFilter(abstractQuery);
        }
	}
    
	protected void appendUnidadeDecisoraColegiadaFilter(AbstractQuery<?> abstractQuery) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		Root<?> root = abstractQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        String metadadoUnidadeDecisora = EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA.getMetadadoType();
        Integer idUnidadeDecisora = authenticator.getColegiadaLogada().getIdUnidadeDecisoraColegiada();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoUnidadeDecisora));
        predicateSubquery = cb.and(cb.equal(metadado.get("valor"), idUnidadeDecisora.toString()), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
        Predicate predicate = abstractQuery.getRestriction();
    	predicate = cb.and(cb.exists(subquery), predicate);
    	abstractQuery.where(predicate);
	}
	
	public void appendUnidadeDecisoraMonocraticaFilter(AbstractQuery<?> abstractQuery) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		Root<?> root = abstractQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        
        String metadadoUnidadeDecisora = EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA.getMetadadoType();
        Integer idUnidadeDecisora = authenticator.getMonocraticaLogada().getIdUnidadeDecisoraMonocratica();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoUnidadeDecisora));
        predicateSubquery = cb.and(cb.equal(metadado.get("valor"), idUnidadeDecisora.toString()), predicateSubquery);
        
        Subquery<Integer> subQueryProcessoRoot = abstractQuery.subquery(Integer.class);
        Root<Processo> processo = subQueryProcessoRoot.from(Processo.class);
        Expression<Integer> idProcesso = processo.get("idProcesso");
        subQueryProcessoRoot.select(idProcesso);
        subQueryProcessoRoot.where(cb.and(
        	cb.equal(processo.get("numeroProcesso"), cb.function("NumeroProcessoRoot", String.class, root.get("idProcesso"))),
        	cb.isNull(processo.get("processoPai"))
        ));
        
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"),	subQueryProcessoRoot), predicateSubquery);
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

    public void appendPerfilTemplateFilter(AbstractQuery<?> abstractQuery) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	Root<?> root = abstractQuery.getRoots().iterator().next();
    	Integer idPerfilTemplate = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getId();
		Predicate predicate = abstractQuery.getRestriction();
		predicate = cb.and(cb.equal(root.get("pooledActor"), idPerfilTemplate.toString()), predicate);
		abstractQuery.where(predicate);
	}

	public boolean canOpenTask(long idTaskInstance, TipoProcesso tipoProcesso, Boolean comunicacoesExpedidas) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<SituacaoProcesso> root = criteriaQuery.from(SituacaoProcesso.class);
		criteriaQuery.select(cb.count(root.get("idTaskInstance")));
		criteriaQuery.where(cb.equal(root.get("idTaskInstance"), idTaskInstance));
		appendMandatoryFilters(criteriaQuery, tipoProcesso);
		appendTipoProcessoFilters(criteriaQuery, tipoProcesso, comunicacoesExpedidas);
		Long count = getEntityManager().createQuery(criteriaQuery).getSingleResult();
		return count > 0;
	}

	public boolean canAccessProcesso(Integer idProcesso, TipoProcesso tipoProcesso, Boolean comunicacoesExpedidas) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<SituacaoProcesso> root = criteriaQuery.from(SituacaoProcesso.class);
		criteriaQuery.select(cb.count(root.get("idProcesso")));
		criteriaQuery.where(cb.equal(root.get("idProcesso"), idProcesso));
		appendMandatoryFilters(criteriaQuery, tipoProcesso);
		appendTipoProcessoFilters(criteriaQuery, tipoProcesso, comunicacoesExpedidas);
		TypedQuery<Long> query = getEntityManager().createQuery(criteriaQuery);
		query.setHint("org.hibernate.cacheable", "true");
        Long count = query.getSingleResult(); 
		return count > 0;
	}
	
	public Long getIdTaskInstanceByIdProcesso(Integer idProcesso, Integer idTarefa) {
		TypedQuery<Long> query = getEntityManager().createNamedQuery(SituacaoProcessoQuery.GET_ID_TASK_INSTANCE_BY_ID_PROCESSO, Long.class);
		query.setParameter(SituacaoProcessoQuery.PARAM_ID_PROCESSO, idProcesso);
		query.setParameter(SituacaoProcessoQuery.PARAM_ID_TAREFA, idTarefa);
		List<Long> resultList = query.setMaxResults(1).getResultList();
		return resultList.isEmpty() ? null : resultList.get(0);
	}
	
}
