package br.com.infox.epp.processo.situacao.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.jbpm.context.exe.variableinstance.LongInstance;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Categoria_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.fluxo.entity.Natureza_;
import br.com.infox.epp.painel.TaskBean;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.painel.caixa.Caixa_;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso_;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao_;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso_;
import br.com.infox.epp.processo.type.TipoProcesso;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SituacaoProcessoDAO {
	
	public EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
	
	public List<TaskBean> getTaskIntances(TipoProcesso tipoProcesso, boolean comunicacoesExpedidas, String numeroProcessoRootFilter) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    CriteriaQuery<TaskBean> cq = cb.createQuery(TaskBean.class);
	    Root<TaskInstance> taskInstance = cq.from(TaskInstance.class);
	    Root<LongInstance> variableInstance = cq.from(LongInstance.class);
	    Root<Processo> processo = cq.from(Processo.class);
	    Join<TaskInstance, ProcessInstance> processInstance = taskInstance.join("processInstance", JoinType.INNER);
	    Join<Processo, NaturezaCategoriaFluxo> natCatFluxo = processo.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);
	    Join<NaturezaCategoriaFluxo, Fluxo> fluxo = natCatFluxo.join(NaturezaCategoriaFluxo_.fluxo, JoinType.INNER);
	    Join<TaskInstance, Task> task = taskInstance.join("task", JoinType.INNER);
	    Join<Task, TaskNode> taskNode = task.join("taskNode", JoinType.INNER);
	    Join<NaturezaCategoriaFluxo, Natureza> natureza = natCatFluxo.join(NaturezaCategoriaFluxo_.natureza, JoinType.INNER);
	    Join<NaturezaCategoriaFluxo, Categoria> categoria = natCatFluxo.join(NaturezaCategoriaFluxo_.categoria, JoinType.INNER);
	    Join<Processo, UsuarioLogin> usuarioSolicitante = processo.join(Processo_.usuarioCadastro, JoinType.INNER);
	    Join<Processo, Processo> processoRoot = processo.join(Processo_.processoRoot, JoinType.INNER);
	    Join<Processo, PrioridadeProcesso> prioridadeProcesso = processo.join(Processo_.prioridadeProcesso, JoinType.LEFT);
	    Join<Processo, Caixa> caixa = processo.join(Processo_.caixa, JoinType.LEFT);
	    
	    Selection<String> idTaskInstance = taskInstance.<Long>get("id").as(String.class);
	    Selection<String> taskName = task.<String>get("name");
	    Selection<String> assignee = taskInstance.<String>get("assignee");
	    Selection<String> idProcessInstance = processInstance.<Long>get("id").as(String.class);
	    Selection<String> taskNodeKey = taskNode.<String>get("key");
	    Selection<Integer> idProcesso =  processo.get(Processo_.idProcesso);
	    Selection<String> nomeCaixa =  caixa.get(Caixa_.nomeCaixa);
	    Selection<Integer> idCaixa =  caixa.get(Caixa_.idCaixa);
	    Selection<String> nomeFluxo =  fluxo.get(Fluxo_.fluxo);
	    Selection<Integer> idFluxo =  fluxo.get(Fluxo_.idFluxo);
	    
	    Selection<String> nomeNatureza = natureza.get(Natureza_.natureza); 
	    Selection<String> nomeCategoria = categoria.get(Categoria_.categoria); 
	    Selection<String> numeroProcesso = processo.get(Processo_.numeroProcesso);
	    Selection<String> numeroProcessoRoot = processoRoot.get(Processo_.numeroProcesso);
	    Selection<String> nomeUsuarioSolicitante = usuarioSolicitante.get(UsuarioLogin_.nomeUsuario);
	    Selection<String> nomePrioridade = prioridadeProcesso.get(PrioridadeProcesso_.descricaoPrioridade);
	    Selection<Integer> pesoPrioridade = prioridadeProcesso.get(PrioridadeProcesso_.peso);
	    Selection<Date> dataInicio = processo.get(Processo_.dataInicio);
	    
	    cq.select(cb.construct(TaskBean.class, idTaskInstance, taskName, assignee, idProcessInstance, taskNodeKey, idProcesso, nomeCaixa, idCaixa, nomeFluxo, idFluxo,
	            nomeNatureza, nomeCategoria, numeroProcesso, numeroProcessoRoot, nomeUsuarioSolicitante, nomePrioridade, pesoPrioridade, dataInicio));

	    cq.where(
	            cb.equal(variableInstance.get("processInstance").<Long>get("id"), processInstance.<Long>get("id")),
	            cb.equal(variableInstance.<String>get("name"), cb.literal("processo")),
	            cb.equal(variableInstance.<Long>get("value"), processo.get(Processo_.idProcesso)),
	            cb.isNull(processInstance.<Date>get("end")),
	            cb.isTrue(taskInstance.<Boolean>get("isOpen")),
	            cb.isFalse(taskInstance.<Boolean>get("isSuspended"))
	    );
	    
	    appendSigiloProcessoFilter(cq, processo);
        appendTipoProcessoFilter(cq, tipoProcesso, processo);
        appendTipoProcessoFilters(cq, tipoProcesso, comunicacoesExpedidas, taskInstance, processo);
        if (!StringUtil.isEmpty(numeroProcessoRootFilter)) {
            appendNumeroProcessoRootFilter(cq, numeroProcessoRootFilter, processoRoot);
        }
	    return getEntityManager().createQuery(cq).getResultList();
	}
	
    protected void appendNumeroProcessoRootFilter(AbstractQuery<?> abstractQuery, String numeroProcesso, From<?, Processo> processoRoot) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Predicate predicate = abstractQuery.getRestriction();
        abstractQuery.where(
               cb.and(
                       cb.like(processoRoot.get(Processo_.numeroProcesso), "%" + cb.literal(numeroProcesso) + "%"),
                       predicate
               )
        );
    }
    
    public void appendTipoProcessoFilters(AbstractQuery<?> abstractQuery, TipoProcesso tipoProcesso, Boolean comunicacoesExpedidas, 
            From<?, TaskInstance> taskInstance, From<?, Processo> processo) {
        if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
            if (comunicacoesExpedidas != null && comunicacoesExpedidas) {
                appendLocalizacaoExpedidoraFilter(abstractQuery, processo);
            } else {
                appendDestinoOrDestinatarioFilter(abstractQuery, processo);
            }
        } else if (TipoProcesso.DOCUMENTO.equals(tipoProcesso)) {
            appendPerfilTemplateFilter(abstractQuery, taskInstance);
		} else if (TipoProcesso.COMUNICACAO_INTERNA.equals(tipoProcesso)) {
		    appendDestinoOrDestinatarioFilter(abstractQuery);
        } else {
            appendUnidadeDecisoraFilter(abstractQuery, processo);
            appendPerfilTemplateFilter(abstractQuery, taskInstance);
        }
    }
	
	private void appendSigiloProcessoFilter(AbstractQuery<?> principalQuery, Root<Processo> processo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<Integer> existsSigiloProcesso = principalQuery.subquery(Integer.class);
        Root<SigiloProcesso> sigiloProcesso = existsSigiloProcesso.from(SigiloProcesso.class);
        existsSigiloProcesso.select(cb.literal(1));
        Predicate whereSigiloProcesso = cb.equal(sigiloProcesso.get(SigiloProcesso_.processo).get(Processo_.idProcesso), processo.get(Processo_.idProcesso));
        whereSigiloProcesso = cb.and(cb.equal(sigiloProcesso.get("ativo"), true), whereSigiloProcesso);
        whereSigiloProcesso = cb.and(cb.isTrue(sigiloProcesso.get(SigiloProcesso_.sigiloso)), whereSigiloProcesso);
        existsSigiloProcesso.where(whereSigiloProcesso);
        
        Subquery<Integer> existsSigiloProcessoPermissao = principalQuery.subquery(Integer.class);
        Root<SigiloProcessoPermissao> sigiloProcessoPermissao = existsSigiloProcessoPermissao.from(SigiloProcessoPermissao.class);
        existsSigiloProcessoPermissao.select(cb.literal(1));
        
        Subquery<Integer> subquery3 = existsSigiloProcessoPermissao.subquery(Integer.class);
        Root<SigiloProcesso> sigiloProcesso2 = subquery3.from(SigiloProcesso.class);
        subquery3.select(sigiloProcesso2.get(SigiloProcesso_.id).as(Integer.class));
        Predicate predicateSubquery3 = cb.equal(sigiloProcesso2.get(SigiloProcesso_.processo).get(Processo_.idProcesso), processo.get(Processo_.idProcesso));
        predicateSubquery3 = cb.and(cb.isTrue(sigiloProcesso2.get(SigiloProcesso_.ativo)) , predicateSubquery3);
        subquery3.where(predicateSubquery3);
        
        Integer idUsuarioLogado = Authenticator.getUsuarioLogado().getIdUsuarioLogin();
        Predicate whereSigiloProcessoPermissao = sigiloProcessoPermissao.get(SigiloProcessoPermissao_.sigiloProcesso).get(SigiloProcesso_.id).in(subquery3);
        whereSigiloProcessoPermissao = cb.and(cb.equal(sigiloProcessoPermissao.get(SigiloProcessoPermissao_.ativo), Boolean.TRUE), whereSigiloProcessoPermissao);
        whereSigiloProcessoPermissao = cb.and(cb.equal(sigiloProcessoPermissao.get(SigiloProcessoPermissao_.usuario).get(UsuarioLogin_.idUsuarioLogin), idUsuarioLogado), whereSigiloProcessoPermissao);
        existsSigiloProcessoPermissao.where(whereSigiloProcessoPermissao);
        
        Predicate predicate = principalQuery.getRestriction();
        predicate = cb.and(cb.or(cb.not(cb.exists(existsSigiloProcesso)), cb.exists(existsSigiloProcessoPermissao)), predicate);
        principalQuery.where(predicate);
    }
	
	protected void appendDestinoOrDestinatarioFilter(AbstractQuery<?> abstractQuery, From<?, Processo> processo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<Integer> subqueryDestino = createSubqueryDestino(abstractQuery, processo);
        Subquery<Integer> subqueryPerfilDestino = createSubqueryPerfilDestino(abstractQuery, processo);
        Subquery<Integer> subqueryDestinatario = createSubqueryDestinatario(abstractQuery, processo);
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

	protected void appendLocalizacaoExpedidoraFilter(AbstractQuery<?> abstractQuery, From<?, Processo> processo) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Integer idLocalizacao = Authenticator.getLocalizacaoAtual().getIdLocalizacao();
        Predicate predicate = abstractQuery.getRestriction();
        abstractQuery.where(
                cb.and(
                        cb.equal(processo.get(Processo_.localizacao).get(Localizacao_.idLocalizacao), idLocalizacao), 
                        predicate
                )
        );
	}
	
	private Subquery<Integer> createSubqueryPerfilDestino(AbstractQuery<?> abstractQuery, From<?, Processo> processo) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        String metadadoDestino = EppMetadadoProvider.PERFIL_DESTINO.getMetadadoType();
        Subquery<Integer> subqueryExistsMetadado = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadadoExists = subqueryExistsMetadado.from(MetadadoProcesso.class);
        subqueryExistsMetadado.select(cb.literal(1));
        Predicate predicateSubqueryExists = cb.and(cb.equal(metadadoExists.get(MetadadoProcesso_.metadadoType), metadadoDestino));
        predicateSubqueryExists = cb.and(cb.equal(metadadoExists.get(MetadadoProcesso_.processo).get(Processo_.idProcesso), processo.get(Processo_.idProcesso)), predicateSubqueryExists);
        subqueryExistsMetadado.where(predicateSubqueryExists);
        Integer idPerfilAtual = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getId();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.metadadoType), metadadoDestino));
        predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.valor), idPerfilAtual.toString()), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.processo).get(Processo_.idProcesso), processo.get(Processo_.idProcesso)), predicateSubquery);
        predicateSubquery = cb.or(cb.not(cb.exists(subqueryExistsMetadado)), predicateSubquery);
        subquery.where(predicateSubquery);
        return subquery;
    }

    private Subquery<Integer> createSubqueryDestino(AbstractQuery<?> abstractQuery, From<?, Processo> processo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        String metadadoDestino = EppMetadadoProvider.LOCALIZACAO_DESTINO.getMetadadoType();
        Integer idLocalizacao = Authenticator.getLocalizacaoAtual().getIdLocalizacao();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.metadadoType), metadadoDestino));
        predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.valor), idLocalizacao.toString()), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.processo).get(Processo_.idProcesso), processo.get(Processo_.idProcesso)), predicateSubquery);
        subquery.where(predicateSubquery);
		return subquery;
	}
    
	private Subquery<Integer> createSubqueryDestinatario(AbstractQuery<?> abstractQuery,  From<?, Processo> processo) { 
		String metadadoDestinatario = EppMetadadoProvider.PESSOA_DESTINATARIO.getMetadadoType();
		PessoaFisica pessoaFisica = Authenticator.getUsuarioLogado().getPessoaFisica();
        Integer idPessoaFisica = pessoaFisica == null ? -1 : pessoaFisica.getIdPessoa();
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
		Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
		subquery.select(cb.literal(1));
		Predicate predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.metadadoType), metadadoDestinatario));
		predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.processo).get(Processo_.idProcesso), processo.get(Processo_.idProcesso)), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.valor), idPessoaFisica.toString()), predicateSubquery);
		subquery.where(predicateSubquery);
		return subquery;
	}
	
    private void appendTipoProcessoFilter(AbstractQuery<?> abstractQuery, TipoProcesso tipoProcesso, Root<Processo> processo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        String metadadoTipoProcesso = EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType();
        Predicate predicateSubquery = cb.equal(metadado.get(MetadadoProcesso_.metadadoType), metadadoTipoProcesso);
        if (tipoProcesso != null) {
            predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.valor), tipoProcesso.toString()), predicateSubquery);
        }
        predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.processo).get(Processo_.idProcesso), processo.get(Processo_.idProcesso)), predicateSubquery);
        subquery.where(predicateSubquery);
        Predicate predicate = abstractQuery.getRestriction();
        if (tipoProcesso == null) {
            predicate = cb.and(cb.not(cb.exists(subquery)), predicate);
        } else {
            predicate = cb.and(cb.exists(subquery), predicate);
        }
        abstractQuery.where(predicate);
    }
    
    protected void appendUnidadeDecisoraFilter(AbstractQuery<?> abstractQuery, From<?, Processo> processo) {
    	if (getAuthenticator().isUsuarioLogandoInMonocraticaAndColegiada()) {
    		appendUnidadeDecisoraColegiadaFilter(abstractQuery, processo);
    		appendUnidadeDecisoraMonocraticaFilter(abstractQuery, processo);
        } else if (getAuthenticator().isUsuarioLogadoInColegiada()) {
            appendUnidadeDecisoraColegiadaFilter(abstractQuery, processo);
        } else if (getAuthenticator().isUsuarioLogadoInMonocratica()) {
        	appendUnidadeDecisoraMonocraticaFilter(abstractQuery, processo);
        }
	}
    
	protected void appendUnidadeDecisoraColegiadaFilter(AbstractQuery<?> abstractQuery, From<?, Processo> processo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        String metadadoUnidadeDecisora = EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA.getMetadadoType();
        Integer idUnidadeDecisora = authenticator.getColegiadaLogada().getIdUnidadeDecisoraColegiada();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get("metadadoType"), metadadoUnidadeDecisora));
        predicateSubquery = cb.and(cb.equal(metadado.get("valor"), idUnidadeDecisora.toString()), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(metadado.get("processo").get("idProcesso"), processo.get("idProcesso")), predicateSubquery);
        subquery.where(predicateSubquery);
        Predicate predicate = abstractQuery.getRestriction();
        predicate = cb.and(cb.exists(subquery), predicate);
        abstractQuery.where(predicate);
    }
    
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        String metadadoUnidadeDecisora = EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA.getMetadadoType();
        Integer idUnidadeDecisora = getAuthenticator().getColegiadaLogada().getIdUnidadeDecisoraColegiada();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.metadadoType), metadadoUnidadeDecisora));
        predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.valor), idUnidadeDecisora.toString()), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.processo).get(Processo_.idProcesso), processo.get(Processo_.idProcesso)), predicateSubquery);
        subquery.where(predicateSubquery);
        Predicate predicate = abstractQuery.getRestriction();
    	predicate = cb.and(cb.exists(subquery), predicate);
    	abstractQuery.where(predicate);
	}
	
	protected void appendUnidadeDecisoraMonocraticaFilter(AbstractQuery<?> abstractQuery, From<?, Processo> processo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
        subquery.select(cb.literal(1));
        
        String metadadoUnidadeDecisora = EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA.getMetadadoType();
        Integer idUnidadeDecisora = getAuthenticator().getMonocraticaLogada().getIdUnidadeDecisoraMonocratica();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.metadadoType), metadadoUnidadeDecisora));
        predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.valor), idUnidadeDecisora.toString()), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.processo).get(Processo_.idProcesso),	processo.get(Processo_.processoRoot).get(Processo_.idProcesso)), predicateSubquery);
        subquery.where(predicateSubquery);
        Predicate predicate = abstractQuery.getRestriction();
        
    	predicate = cb.and(cb.exists(subquery), predicate);
    	abstractQuery.where(predicate);
	}
	
    private void appendPerfilTemplateFilter(AbstractQuery<?> abstractQuery, From<?, TaskInstance> taskInstance) {
        Integer idPerfilTemplate = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getId();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        subquery.select(cb.literal(1));
        Root<PooledActor> pooledActor = subquery.from(PooledActor.class);
        Join<PooledActor, TaskInstance> taskInstances = pooledActor.join("taskInstances", JoinType.INNER);
        subquery.where(
                cb.equal(taskInstance.<Long>get("id"), taskInstances.<Long>get("id")),
                cb.equal(pooledActor.<String>get("actorId"), cb.literal(idPerfilTemplate.toString()))
        );
        Predicate predicate = abstractQuery.getRestriction();
        abstractQuery.where(cb.and(cb.exists(subquery), predicate));
    }

	public boolean canOpenTask(long idTaskInstance, TipoProcesso tipoProcesso, Boolean comunicacoesExpedidas) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TaskInstance> taskInstance = cq.from(TaskInstance.class);
        Root<LongInstance> variableInstance = cq.from(LongInstance.class);
        Root<Processo> processo = cq.from(Processo.class);
        Join<TaskInstance, ProcessInstance> processInstance = taskInstance.join("processInstance", JoinType.INNER);
        
        cq.select(cb.count(taskInstance));
        
        cq.where(
                cb.equal(variableInstance.get("processInstance").<Long>get("id"), processInstance.<Long>get("id")),
                cb.equal(variableInstance.<String>get("name"), cb.literal("processo")),
                cb.equal(variableInstance.<Long>get("value"), processo.get(Processo_.idProcesso)),
                cb.isNull(processInstance.<Date>get("end")),
                cb.isTrue(taskInstance.<Boolean>get("isOpen")),
                cb.isFalse(taskInstance.<Boolean>get("isSuspended")),
                cb.equal(taskInstance.<Long>get("id"), cb.literal(idTaskInstance))
        );
        
        appendSigiloProcessoFilter(cq, processo);
        appendTipoProcessoFilter(cq, tipoProcesso, processo);
        appendTipoProcessoFilters(cq, tipoProcesso, comunicacoesExpedidas, taskInstance, processo);
        TypedQuery<Long> query = getEntityManager().createQuery(cq);
        query.setHint("org.hibernate.cacheable", "true");
        Long count = query.getSingleResult(); 
        return count > 0;
	}

	public boolean canAccessProcesso(Integer idProcesso, TipoProcesso tipoProcesso, Boolean comunicacoesExpedidas) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TaskInstance> taskInstance = cq.from(TaskInstance.class);
        Root<LongInstance> variableInstance = cq.from(LongInstance.class);
        Root<Processo> processo = cq.from(Processo.class);
        Join<TaskInstance, ProcessInstance> processInstance = taskInstance.join("processInstance", JoinType.INNER);
        
        cq.select(cb.count(taskInstance));
        
        cq.where(
                cb.equal(variableInstance.get("processInstance").<Long>get("id"), processInstance.<Long>get("id")),
                cb.equal(variableInstance.<String>get("name"), cb.literal("processo")),
                cb.equal(variableInstance.<Long>get("value"), processo.get(Processo_.idProcesso)),
                cb.isNull(processInstance.<Date>get("end")),
                cb.isTrue(taskInstance.<Boolean>get("isOpen")),
                cb.isFalse(taskInstance.<Boolean>get("isSuspended")),
                cb.equal(processo.get(Processo_.idProcesso), cb.literal(idProcesso))
        );
        
        appendSigiloProcessoFilter(cq, processo);
        appendTipoProcessoFilter(cq, tipoProcesso, processo);
        appendTipoProcessoFilters(cq, tipoProcesso, comunicacoesExpedidas, taskInstance, processo);
        TypedQuery<Long> query = getEntityManager().createQuery(cq);
        query.setHint("org.hibernate.cacheable", "true");
        Long count = query.getSingleResult(); 
        return count > 0;
	}
	
	protected Authenticator getAuthenticator() {
	    return Authenticator.instance();
}
