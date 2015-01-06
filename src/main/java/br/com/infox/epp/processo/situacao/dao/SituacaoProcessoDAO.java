package br.com.infox.epp.processo.situacao.dao;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.localizacao.entity.ProcessoLocalizacaoIbpm;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.situacao.entity.SituacaoProcesso;
import br.com.infox.epp.processo.type.TipoProcesso;

@AutoCreate
@Name(SituacaoProcessoDAO.NAME)
public class SituacaoProcessoDAO extends DAO<SituacaoProcesso> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "situacaoProcessoDAO";
	
    public final List<Tuple> getRootList(TipoProcesso tipoProcesso) {
        CriteriaQuery<Tuple> criteriaQuery = createBaseQueryRoot();
        appendTipoProcessoFilter(criteriaQuery, tipoProcesso);
        appendTipoProcessoRootsFilters(criteriaQuery, tipoProcesso);
        appendSigiloProcessoFilter(criteriaQuery);
        return getEntityManager().createQuery(criteriaQuery).getResultList();
    }
	
	private void appendSigiloProcessoFilter(CriteriaQuery<?> criteriaQuery) {
		
	}

	public final List<Tuple> getChildrenList(Integer idFluxo, TipoProcesso tipoProcesso) {
		CriteriaQuery<Tuple> criteriaQuery = createBaseQueryChildren(idFluxo);
		appendTipoProcessoFilter(criteriaQuery, tipoProcesso);
		appendTipoProcessoChildrenFilters(criteriaQuery, tipoProcesso);
		appendSigiloProcessoFilter(criteriaQuery);
        return getEntityManager().createQuery(criteriaQuery).getResultList();
    }
	
	public final List<Tuple> getCaixaList(TipoProcesso tipoProcesso, Integer idTask) {
		CriteriaQuery<Tuple> criteriaQuery = createBaseQueryCaixas(tipoProcesso, idTask);
		appendTipoProcessoCaixasFilters(criteriaQuery, tipoProcesso);
		appendSigiloProcessoFilter(criteriaQuery);
        return getEntityManager().createQuery(criteriaQuery).getResultList();
    }
    
    protected void appendTipoProcessoCaixasFilters(CriteriaQuery<?> criteriaQuery, TipoProcesso tipoProcesso) {
    	if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
			appendDestinoOrDestinatarioFilter(criteriaQuery);
		} else if (TipoProcesso.DOCUMENTO.equals(tipoProcesso)) {
			appendDestinoOrDestinatarioFilter(criteriaQuery);
		} else {
			appendPerfilTemplateFilter(criteriaQuery);
			appendUnidadeDecisoraFilter(criteriaQuery);
		}
	}

	private CriteriaQuery<Tuple> createBaseQueryCaixas(TipoProcesso tipoProcesso, Integer idTask) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<Tuple> cq = cb.createTupleQuery();
    	Root<SituacaoProcesso> root = cq.from(SituacaoProcesso.class);
    	Selection<Integer> idCaixa = root.get("idCaixa").as(Integer.class).alias("idCaixa");
    	Selection<Integer> idTarefa = root.get("idTarefa").as(Integer.class).alias("idTarefa");
    	Selection<String> nomeCaixa = root.get("nomeCaixa").as(String.class).alias("nomeCaixa");
    	Selection<Long> qtProcessoCaixa = cb.count(root.get("idProcesso")).as(Long.class).alias("qtd");
    	Selection<String> type = cb.literal("Caixa").alias("type");
    	cq.select(cb.tuple(idCaixa, idTarefa, nomeCaixa, qtProcessoCaixa, type)).distinct(true);
    	cq.where(cb.equal(root.get("idTarefa"), idTask));
    	cq.groupBy(root.get("idCaixa"), root.get("idTarefa"), root.get("nomeCaixa"));
    	cq.orderBy(cb.asc(root.get("nomeCaixa")));
     	return cq;
	}

	public final List<Integer> getProcessosAbertosByIdTarefa(Integer idTarefa, Tuple selected, TipoProcesso tipoProcesso) {
		CriteriaQuery<Integer> criteriaQuery = createBaseQueryProcessosAbertos(idTarefa);
        String nodeType = selected.get("type", String.class);
		if ("Task".equals(nodeType)) {
			appendProcessSemCaixaFilter(criteriaQuery);
        } else if ("Caixa".equals(nodeType)) {
            appendProcessoComCaixaFilter(criteriaQuery);
        }
        appendTipoProcessoFilter(criteriaQuery, tipoProcesso);
        appendSigiloProcessoFilter(criteriaQuery);
        appendTipoProcessoProcessosAbertosFilters(criteriaQuery, tipoProcesso);
        return getEntityManager().createQuery(criteriaQuery).getResultList();
    }
    
	private void appendTipoProcessoProcessosAbertosFilters(CriteriaQuery<Integer> criteriaQuery, TipoProcesso tipoProcesso) {
		if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
			appendDestinoOrDestinatarioFilter(criteriaQuery);
		} else if (TipoProcesso.DOCUMENTO.equals(tipoProcesso)) {
			appendDestinoOrDestinatarioFilter(criteriaQuery);
		} else {
			appendPapelLocalizacaoFilter(criteriaQuery);
			appendUnidadeDecisoraFilter(criteriaQuery);
		}
	}

	private CriteriaQuery<Integer> createBaseQueryProcessosAbertos(Integer idTarefa) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
        Root<SituacaoProcesso> root = criteriaQuery.from(SituacaoProcesso.class);
        criteriaQuery.select(root.get("idProcesso").as(Integer.class));
        criteriaQuery.groupBy(root.get("idProcesso"));
        criteriaQuery.where(cb.equal(root.get("idTarefa"), idTarefa));
        return criteriaQuery;
    }

    private CriteriaQuery<Tuple> createBaseQueryRoot() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
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
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
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
    
    protected void appendTipoProcessoRootsFilters(CriteriaQuery<?> criteriaQuery, TipoProcesso tipoProcesso) {
    	if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
    		appendDestinoOrDestinatarioFilter(criteriaQuery);
    	} else if (TipoProcesso.DOCUMENTO.equals(tipoProcesso)) {
    		appendDestinoOrDestinatarioFilter(criteriaQuery);
    	} else {
            appendPapelLocalizacaoFilter(criteriaQuery);
            appendUnidadeDecisoraFilter(criteriaQuery);
            appendRelatorFilter(criteriaQuery);
    	}
    }
    
	private void appendDestinoOrDestinatarioFilter(CriteriaQuery<?> criteriaQuery) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<?> from = criteriaQuery.getRoots().iterator().next();
        Subquery<Integer> subqueryDestino = createSubqueryDestino(criteriaQuery, from);
        Subquery<Integer> subqueryDestinatario = createSubqueryDestinatario(criteriaQuery, from);
        Predicate predicateQuery = criteriaQuery.getRestriction();
        predicateQuery = cb.and(cb.or(cb.exists(subqueryDestino), cb.exists(subqueryDestinatario)), predicateQuery);
        criteriaQuery.where(predicateQuery);
    }

	private Subquery<Integer> createSubqueryDestino(CriteriaQuery<?> criteriaQuery, Root<?> from) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
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
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
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
    
    private void appendTipoProcessoFilter(CriteriaQuery<?> criteriaQuery, TipoProcesso tipoProcesso) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
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
    
    private void appendPapelLocalizacaoFilter(CriteriaQuery<?> criteriaQuery) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
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
    
    private void appendUnidadeDecisoraFilter(CriteriaQuery<?> criteriaQuery) {
    	Authenticator authenticator = Authenticator.instance();
    	if (authenticator.isUsuarioLogandoInMonocraticaAndColegiada()) {
    		appendUnidadeDecisoraColegiadaFilter(criteriaQuery);
    		appendUnidadeDecisoraMonocraticaFilter(criteriaQuery);
        } else if (authenticator.isUsuarioLogadoInColegiada()) {
            appendUnidadeDecisoraColegiadaFilter(criteriaQuery);
        } else if (authenticator.isUsuarioLogadoInMonocratica()) {
        	appendUnidadeDecisoraMonocraticaFilter(criteriaQuery);
        } else {
        	appendSemUnidadeDecisoraFilter(criteriaQuery);
        }
	}
    
	private void appendUnidadeDecisoraColegiadaFilter(CriteriaQuery<?> criteriaQuery) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<?> root = criteriaQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        
        String metadadoUnidadeDecisora = EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA.getMetadadoType();
        Integer idUnidadeDecisora = Authenticator.instance().getColegiadaLogada().getIdUnidadeDecisoraColegiada();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoUnidadeDecisora));
        predicateSubquery = cb.and(cb.equal(metadado.get("valor"), idUnidadeDecisora.toString()));
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
        
        Predicate predicate = criteriaQuery.getRestriction();
    	predicate = cb.and(cb.exists(subquery), predicate);
        criteriaQuery.where(predicate);
	}
	
	private void appendUnidadeDecisoraMonocraticaFilter(CriteriaQuery<?> criteriaQuery) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<?> root = criteriaQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        
        String metadadoUnidadeDecisora = EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA.getMetadadoType();
        Integer idUnidadeDecisora = Authenticator.instance().getMonocraticaLogada().getIdUnidadeDecisoraMonocratica();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoUnidadeDecisora));
        predicateSubquery = cb.and(cb.equal(metadado.get("valor"), idUnidadeDecisora.toString()));
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
        
        Predicate predicate = criteriaQuery.getRestriction();
    	predicate = cb.and(cb.exists(subquery), predicate);
        criteriaQuery.where(predicate);
	}
	
	private void appendSemUnidadeDecisoraFilter(CriteriaQuery<?> criteriaQuery) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<?> root = criteriaQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        
        String unidadeDecisoraMonocratica = EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA.getMetadadoType();
        String unidadeDecisoraColegiada = EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA.getMetadadoType();
        Predicate predicateSubquery = cb.and(metadado.get("metadadoType").in(unidadeDecisoraMonocratica, unidadeDecisoraColegiada));
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), root.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
        
        Predicate predicate = criteriaQuery.getRestriction();
    	predicate = cb.and(cb.not(cb.exists(subquery)), predicate);
        criteriaQuery.where(predicate);
	}
	
	/**
     * TODO : Fazer filtro do relator
     */
	private void appendRelatorFilter(CriteriaQuery<?> criteriaQuery) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<?> root = criteriaQuery.getRoots().iterator().next();
        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        
//        Predicate predicateSubquery = cb.and(restrictions)
//        		
//        Predicate predicate = criteriaQuery.getRestriction();
//        predicate = cb.and(cb.equal(x, y))
	}
	
	private void appendProcessoComCaixaFilter(CriteriaQuery<Integer> criteriaQuery) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	Root<?> root = criteriaQuery.getRoots().iterator().next();
    	Predicate predicate = criteriaQuery.getRestriction();
    	predicate = cb.and(cb.isNotNull(root.get("idCaixa")), predicate);
    	criteriaQuery.where(predicate);
	}

	private void appendProcessSemCaixaFilter(CriteriaQuery<Integer> criteriaQuery) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	Root<?> root = criteriaQuery.getRoots().iterator().next();
    	Predicate predicate = criteriaQuery.getRestriction();
    	predicate = cb.and(cb.isNull(root.get("idCaixa")), predicate);
    	criteriaQuery.where(predicate);
	}

	protected void appendTipoProcessoChildrenFilters(CriteriaQuery<?> criteriaQuery, TipoProcesso tipoProcesso) {
		if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
			appendDestinoOrDestinatarioFilter(criteriaQuery);
		} else if (TipoProcesso.DOCUMENTO.equals(tipoProcesso)) {
			appendDestinoOrDestinatarioFilter(criteriaQuery);
		} else {
			appendPerfilTemplateFilter(criteriaQuery);
			appendUnidadeDecisoraFilter(criteriaQuery);
		}
	}
	
    private void appendPerfilTemplateFilter(CriteriaQuery<?> criteriaQuery) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	Root<?> from = criteriaQuery.getRoots().iterator().next();
    	Integer idPerfilTemplate = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getId();
		Predicate predicate = criteriaQuery.getRestriction();
		predicate = cb.and(cb.equal(from.get("pooledActor"), idPerfilTemplate.toString()), predicate);
		criteriaQuery.where(predicate);
	}

	public boolean canOpenTask(long id) {
		// TODO Auto-generated method stub
		return false;
	}

}
