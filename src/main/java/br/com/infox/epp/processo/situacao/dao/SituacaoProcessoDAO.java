package br.com.infox.epp.processo.situacao.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.jbpm.context.exe.variableinstance.LongInstance;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.access.entity.PerfilTemplate;
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
import br.com.infox.epp.painel.FluxoBean;
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
import br.com.infox.ibpm.type.PooledActorType;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SituacaoProcessoDAO extends PersistenceController {
	
	public List<FluxoBean> getFluxoList(TipoProcesso tipoProcesso, boolean comunicacoesExpedidas, String numeroProcessoRootFilter) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<FluxoBean> cq = cb.createQuery(FluxoBean.class);
        Root<TaskInstance> taskInstance = cq.from(TaskInstance.class);
        Root<LongInstance> variableInstance = cq.from(LongInstance.class);
        Root<Processo> processo = cq.from(Processo.class);
        Root<Fluxo> fluxo = cq.from(Fluxo.class);
        Join<TaskInstance, ProcessInstance> processInstance = taskInstance.join("processInstance", JoinType.INNER);
        Join<ProcessInstance, ProcessDefinition> processDefinition = processInstance.join("processDefinition", JoinType.INNER);
        Join<Processo, Processo> processoRoot = processo.join(Processo_.processoRoot, JoinType.INNER);
        
        Selection<String> nomeFluxo =  fluxo.get(Fluxo_.fluxo);
        Selection<String> idFluxo =  fluxo.get(Fluxo_.idFluxo).as(String.class);
        Selection<Long> qtProcessos =  cb.countDistinct(taskInstance.<Long>get("id"));
        Selection<String> expedida = cb.literal(String.valueOf(comunicacoesExpedidas));
        Selection<String> tipo = tipoProcesso == null ? cb.literal("null") : cb.literal(tipoProcesso.value());
        Selection<String> numeroProcessoRoot = numeroProcessoRootFilter == null ? cb.literal("") : cb.literal(numeroProcessoRootFilter);
        
        cq.groupBy(fluxo.get(Fluxo_.fluxo), fluxo.get(Fluxo_.idFluxo));
        cq.orderBy(cb.asc(fluxo.get(Fluxo_.fluxo)));
        
        cq.select(cb.construct(FluxoBean.class, idFluxo, nomeFluxo, qtProcessos, tipo, expedida, numeroProcessoRoot));
        
        cq.where(
                cb.equal(variableInstance.get("processInstance").<Long>get("id"), processInstance.<Long>get("id")),
                cb.equal(variableInstance.<String>get("name"), cb.literal("processo")),
                cb.equal(variableInstance.<Long>get("value"), processo.get(Processo_.idProcesso)),
                cb.equal(processDefinition.get("name"), fluxo.get(Fluxo_.fluxo)),
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
	
	public List<TaskBean> getTaskIntances(FluxoBean fluxoBean) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<TaskBean> cq = cb.createQuery(TaskBean.class);
        Root<TaskInstance> taskInstance = cq.from(TaskInstance.class);
        Root<LongInstance> variableInstance = cq.from(LongInstance.class);
        Root<Processo> processo = cq.from(Processo.class);
        Root<Fluxo> fluxo = cq.from(Fluxo.class);
        Join<TaskInstance, ProcessInstance> processInstance = taskInstance.join("processInstance", JoinType.INNER);
        Join<ProcessInstance, ProcessDefinition> processDefinition = processInstance.join("processDefinition", JoinType.INNER);
        Join<Processo, NaturezaCategoriaFluxo> natCatFluxo = processo.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);
        Join<TaskInstance, Task> task = taskInstance.join("task", JoinType.INNER);
        Join<Task, TaskNode> taskNode = task.join("taskNode", JoinType.INNER);
        Join<NaturezaCategoriaFluxo, Natureza> natureza = natCatFluxo.join(NaturezaCategoriaFluxo_.natureza, JoinType.INNER);
        Join<NaturezaCategoriaFluxo, Categoria> categoria = natCatFluxo.join(NaturezaCategoriaFluxo_.categoria, JoinType.INNER);
        Join<Processo, UsuarioLogin> usuarioSolicitante = processo.join(Processo_.usuarioCadastro, JoinType.INNER);
        Join<Processo, Processo> processoRoot = processo.join(Processo_.processoRoot, JoinType.INNER);
        Join<Processo, NaturezaCategoriaFluxo> natCatFluxoRoot = processoRoot.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);
        Join<NaturezaCategoriaFluxo, Natureza> naturezaRoot = natCatFluxoRoot.join(NaturezaCategoriaFluxo_.natureza, JoinType.INNER);
        Join<NaturezaCategoriaFluxo, Categoria> categoriaRoot = natCatFluxoRoot.join(NaturezaCategoriaFluxo_.categoria, JoinType.INNER);
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
        Selection<String> nomeNaturezaProcessoRoot = naturezaRoot.get(Natureza_.natureza); 
        Selection<String> nomeCategoriaProcessoRoot = categoriaRoot.get(Categoria_.categoria);
        Selection<String> nomeUsuarioSolicitante = usuarioSolicitante.get(UsuarioLogin_.nomeUsuario);
        Selection<String> nomePrioridade = prioridadeProcesso.get(PrioridadeProcesso_.descricaoPrioridade);
        Selection<Integer> pesoPrioridade = prioridadeProcesso.get(PrioridadeProcesso_.peso);
        Selection<Date> dataInicio = processo.get(Processo_.dataInicio);
        
        cq.select(cb.construct(TaskBean.class, idTaskInstance, taskName, assignee, idProcessInstance, taskNodeKey, idProcesso, nomeCaixa, idCaixa, nomeFluxo, idFluxo,
                nomeNatureza, nomeCategoria, numeroProcesso, numeroProcessoRoot, nomeUsuarioSolicitante, nomePrioridade, pesoPrioridade, dataInicio,
                nomeNaturezaProcessoRoot, nomeCategoriaProcessoRoot));

        cq.where(
                cb.equal(processDefinition.get("name"), fluxo.get(Fluxo_.fluxo)),
                cb.equal(variableInstance.get("processInstance").<Long>get("id"), processInstance.<Long>get("id")),
                cb.equal(variableInstance.<String>get("name"), cb.literal("processo")),
                cb.equal(variableInstance.<Long>get("value"), processo.get(Processo_.idProcesso)),
                cb.isNull(processInstance.<Date>get("end")),
                cb.isTrue(taskInstance.<Boolean>get("isOpen")),
                cb.equal(fluxo.get(Fluxo_.idFluxo).as(String.class), cb.literal(fluxoBean.getProcessDefinitionId())),
                cb.isFalse(taskInstance.<Boolean>get("isSuspended"))
        );
        
        appendSigiloProcessoFilter(cq, processo);
        appendTipoProcessoFilter(cq, fluxoBean.getTipoProcesso(), processo);
        appendTipoProcessoFilters(cq, fluxoBean.getTipoProcesso(), fluxoBean.getExpedida(), taskInstance, processo);
        if (!StringUtil.isEmpty(fluxoBean.getNumeroProcessoRootFilter())) {
            appendNumeroProcessoRootFilter(cq, fluxoBean.getNumeroProcessoRootFilter(), processoRoot);
        }
        return getEntityManager().createQuery(cq).getResultList();
    }	
	
    protected void appendNumeroProcessoRootFilter(AbstractQuery<?> abstractQuery, String numeroProcesso, Path<Processo> processoRoot) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        abstractQuery.where(cb.like(processoRoot.get(Processo_.numeroProcesso), cb.literal("%" + numeroProcesso + "%")),
                abstractQuery.getRestriction());
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
            appendPooledActorFilter(abstractQuery, taskInstance);
        } else {
            appendUnidadeDecisoraFilter(abstractQuery, processo);
            appendPooledActorFilter(abstractQuery, taskInstance);
        }
    }
	
	protected void appendSigiloProcessoFilter(AbstractQuery<?> principalQuery, Path<Processo> processo) {
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
    
    protected Subquery<Integer> createSubqueryDestinatario(AbstractQuery<?> abstractQuery,  From<?, Processo> processo) { 
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
	
    protected void appendTipoProcessoFilter(AbstractQuery<?> abstractQuery, TipoProcesso tipoProcesso, Path<Processo> processo) {
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
        Integer idUnidadeDecisora = getAuthenticator().getColegiadaLogada().getIdUnidadeDecisoraColegiada();
        Predicate predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.metadadoType), metadadoUnidadeDecisora));
        predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.valor), idUnidadeDecisora.toString()), predicateSubquery);
        predicateSubquery = cb.and(cb.equal(metadado.get(MetadadoProcesso_.processo).get(Processo_.idProcesso), processo.get(Processo_.processoRoot).get(Processo_.idProcesso)), predicateSubquery);
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
	
    protected void appendPooledActorFilter(AbstractQuery<?> abstractQuery, From<?, TaskInstance> taskInstance) {
        PerfilTemplate perfilTemplate = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate();
        String login = Authenticator.getUsuarioLogado().getLogin();
        String localizacao = Authenticator.getLocalizacaoAtual().getCodigo();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<Integer> subquery = abstractQuery.subquery(Integer.class);
        subquery.select(cb.literal(1));
        Root<PooledActor> pooledActor = subquery.from(PooledActor.class);
        Join<PooledActor, TaskInstance> taskInstances = pooledActor.join("taskInstances", JoinType.INNER);
        subquery.where(
            cb.equal(taskInstance.<Long>get("id"), taskInstances.<Long>get("id")),
            cb.or(
                cb.and(
                    cb.isNull(pooledActor.get("type")),
                    cb.or(
                        cb.equal(pooledActor.<String>get("actorId"), cb.literal(perfilTemplate.getId().toString())),
                        cb.equal(pooledActor.<String>get("actorId"), cb.literal(perfilTemplate.getCodigo()))
                    )
                ),
                cb.and(
                    cb.equal(pooledActor.<String>get("actorId"), cb.literal(login)),
                    cb.equal(pooledActor.<String>get("type"), PooledActorType.USER.getValue())
                ),
                cb.and(
                    cb.equal(pooledActor.<String>get("actorId"), cb.literal(localizacao+"&"+perfilTemplate.getCodigo())),
                    cb.equal(pooledActor.<String>get("type"), PooledActorType.GROUP.getValue())
                ),
                cb.and(
                    cb.equal(pooledActor.<String>get("actorId"), cb.literal(localizacao)),
                    cb.equal(pooledActor.<String>get("type"), PooledActorType.LOCAL.getValue())
                )
            )
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
	
    public void appendMandatoryFilters(AbstractQuery<?> abstractQuery, TipoProcesso tipoProcesso, Path<Processo> processo) {
		appendSigiloProcessoFilter(abstractQuery, processo);
		appendTipoProcessoFilter(abstractQuery, tipoProcesso, processo);
	}
	
	
	protected Authenticator getAuthenticator() {
	    return Authenticator.instance();
	}
}
