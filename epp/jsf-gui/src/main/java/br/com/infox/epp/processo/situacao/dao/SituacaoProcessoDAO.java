package br.com.infox.epp.processo.situacao.dao;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import br.com.infox.epp.view.query.ViewSituacaoProcessoQuery;
import org.jbpm.context.exe.variableinstance.LongInstance;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.fluxo.entity.Categoria_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.fluxo.entity.Natureza_;
import br.com.infox.epp.painel.FluxoBean;
import br.com.infox.epp.painel.TaskBean;
import br.com.infox.epp.painel.caixa.Caixa_;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso_;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao_;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso_;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.view.ViewSituacaoProcesso;
import br.com.infox.epp.view.ViewSituacaoProcesso_;
import br.com.infox.ibpm.type.PooledActorType;

import static br.com.infox.epp.view.query.ViewSituacaoProcessoQuery.*;
import static java.util.stream.Collectors.groupingBy;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SituacaoProcessoDAO extends PersistenceController {

	public List<FluxoBean> getFluxoList(TipoProcesso tipoProcesso, boolean comunicacoesExpedidas, String numeroProcessoRootFilter) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<FluxoBean> cq = cb.createQuery(FluxoBean.class);

	    Root<ViewSituacaoProcesso> viewSituacaoProcesso = cq.from(ViewSituacaoProcesso.class);
	    Join<ViewSituacaoProcesso, TaskInstance> taskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.taskInstance);
	    Join<ViewSituacaoProcesso, ProcessInstance> processInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.processInstance);
        Join<ViewSituacaoProcesso, Processo> processo = viewSituacaoProcesso.join(ViewSituacaoProcesso_.processo);
        Join<Processo, NaturezaCategoriaFluxo> naturezaCategoriaFluxo = processo.join(Processo_.naturezaCategoriaFluxo);
        Join<NaturezaCategoriaFluxo, Fluxo> fluxo = naturezaCategoriaFluxo.join(NaturezaCategoriaFluxo_.fluxo);

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
                cb.isNull(processInstance.<Date>get("end")),
                cb.isTrue(taskInstance.<Boolean>get("isOpen")),
                cb.isFalse(taskInstance.<Boolean>get("isSuspended"))

        );

        appendSigiloProcessoFilter(cq, processo);
        appendTipoProcessoFilter(cq, tipoProcesso, processo);
        appendTipoProcessoFilters(cq, tipoProcesso, comunicacoesExpedidas, taskInstance, processo);
        if (!StringUtil.isEmpty(numeroProcessoRootFilter)) {
            Join<Processo, Processo> processoRoot = processo.join(Processo_.processoRoot, JoinType.INNER);
            appendNumeroProcessoRootFilter(cq, numeroProcessoRootFilter, processoRoot);
        }

        List<FluxoBean> resultList = getEntityManager().createQuery(cq).getResultList();

        List<FluxoBean> byVariable = findByVariable(tipoProcesso, comunicacoesExpedidas, numeroProcessoRootFilter);

        if(byVariable != null && !byVariable.isEmpty()) {
            resultList.addAll(byVariable);

            Map<String, Map<String, List<FluxoBean>>> collect = resultList.stream().collect(groupingBy(FluxoBean::getName, groupingBy(FluxoBean::getProcessDefinitionId)));

            List<FluxoBean> ordered = collect.values().stream().flatMap(a -> a.values().stream()).flatMap(Collection::stream).collect(Collectors.toList());

            return  ordered;
        }

        return resultList;
	}

    private List<FluxoBean> findByVariable(TipoProcesso tipoProcesso, boolean comunicacoesExpedidas, String numeroProcessoRootFilter){

        StringBuilder query = new StringBuilder("");
        query.append("select flux.ds_fluxo, flux.id_fluxo, count(DISTINCT taskins.ID_) as qtd, STUFF(concat(taskins.ID_,','), 1, 0, null) as ids from JBPM_VARIABLEINSTANCE vi ")
                .append("inner join JBPM_PROCESSINSTANCE process on vi.PROCESSINSTANCE_ = process.ID_ ")
                .append("inner join JBPM_TASKINSTANCE taskins on vi.TASKINSTANCE_ = taskins.ID_ ")
                .append("inner join vs_situacao_processo vs on  taskins.ID_ = vs.id_taskinstance ")
                .append("inner join tb_processo proce on  vs.id_processo = proce.id_processo ")
                .append("inner join tb_natureza_categoria_fluxo natcat on proce.id_natureza_categoria_fluxo = natcat.id_natureza_categoria_fluxo ")
                .append("inner join tb_fluxo flux on natcat.id_fluxo = flux.id_fluxo ");

        if (!StringUtil.isEmpty(numeroProcessoRootFilter)) {
            query.append("inner join tb_processo procroot on vs.id_processo_root = procroot.id_processo_root ");
        }

                query.append("where vi.NAME_ = 'perfilVisualizarRecuperar' and vi.STRINGVALUE_ = concat(:")
                .append(PARAM_CODIGO_LOCALIZACAO).append(",'/',:").append(PARAM_CODIGO_PERFIL_TEMPLATE)
                .append(") and process.END_ is null ")
                .append("and taskins.ISOPEN_ = 1 ")
                .append("and taskins.ISSUSPENDED_ = 0 ");

        Map<String, Object> params = new HashMap<>();

        if(tipoProcesso == null){
            query.append(ViewSituacaoProcessoQuery.subQueryTipoProcessoNull);
            params.put(PARAM_TIPO_PROCESSO_METADADO, EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType());
        }else{
            query.append(ViewSituacaoProcessoQuery.subQueryTipoProcesso);
            params.put(PARAM_TIPO_PROCESSO_METADADO, EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType());
            params.put(PARAM_TIPO_PROCESSO_VALOR, tipoProcesso.value());
        }

        if (TipoProcesso.COMUNICACAO.equals(tipoProcesso)) {
            if (comunicacoesExpedidas) {
                query.append(ViewSituacaoProcessoQuery.queryLocExpedidor);
                params.put(PARAM_ID_LOCALIZACAO, Authenticator.getLocalizacaoAtual().getIdLocalizacao());
            } else {
                PessoaFisica pessoaFisica = Authenticator.getUsuarioLogado().getPessoaFisica();
                Integer idPessoaFisica = pessoaFisica == null ? -1 : pessoaFisica.getIdPessoa();
                query.append(ViewSituacaoProcessoQuery.queryDestDestinatario);
                params.put(PARAM_LOCALIZACAO_DESTINO, EppMetadadoProvider.LOCALIZACAO_DESTINO.getMetadadoType());
                params.put(PARAM_ID_LOCALIZACAO, Authenticator.getLocalizacaoAtual().getIdLocalizacao());
                params.put(PARAM_PERFIL_DESTINO, EppMetadadoProvider.PERFIL_DESTINO.getMetadadoType());
                params.put(PARAM_ID_PERFIL_TEMPLATE, Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getId());
                params.put(PARAM_PERFIL_DESTINO, EppMetadadoProvider.PERFIL_DESTINO.getMetadadoType());
                params.put(PARAM_ID_PESSOA_FISICA, idPessoaFisica);
            }
        }

        if (!StringUtil.isEmpty(numeroProcessoRootFilter)) {
            query.append(ViewSituacaoProcessoQuery.queryProcRoot);
            params.put(PARAM_NUMERO_PROCESSO_ROOT, numeroProcessoRootFilter);
        }

        query.append(" group by flux.ds_fluxo, flux.id_fluxo, STUFF(concat(taskins.ID_,','), 1, 0, null) ");
        Query nativeQuery = getEntityManager().createNativeQuery(query.toString());

        params.put(PARAM_CODIGO_PERFIL_TEMPLATE, Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getCodigo());
        params.put(PARAM_CODIGO_LOCALIZACAO, Authenticator.getLocalizacaoAtual().getCodigo());
        params.entrySet().forEach( p -> {
            nativeQuery.setParameter(p.getKey(), p.getValue());
        });

        List<Object[]> resultList = nativeQuery.getResultList();
        List<FluxoBean> result = new LinkedList<>();
        for(Object[] record : resultList){
            result.add(new FluxoBean(record, tipoProcesso == null ? null : tipoProcesso.value(), String.valueOf(comunicacoesExpedidas), numeroProcessoRootFilter));
        }
        return result;
    }

	public List<TaskBean> getTaskIntances(FluxoBean fluxoBean) {

        Map<String, Object> params = new HashMap<>();

        StringBuilder query = new StringBuilder("");
        query.append(ViewSituacaoProcessoQuery.TASK_INSTANCES_QUERY);
        query.append(ViewSituacaoProcessoQuery.subQuerySigilo);

        if(fluxoBean.getTipoProcesso() == null){
            query.append(ViewSituacaoProcessoQuery.subQueryTipoProcessoNull);
            params.put(PARAM_TIPO_PROCESSO_METADADO, EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType());
        }else{
            query.append(ViewSituacaoProcessoQuery.subQueryTipoProcesso);
            params.put(PARAM_TIPO_PROCESSO_METADADO, EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType());
            params.put(PARAM_TIPO_PROCESSO_VALOR, fluxoBean.getTipoProcesso().value());
        }

        if (TipoProcesso.COMUNICACAO.equals(fluxoBean.getTipoProcesso())) {
            if (fluxoBean.getExpedida() != null && fluxoBean.getExpedida()) {
               query.append(ViewSituacaoProcessoQuery.queryLocExpedidor);
               params.put(PARAM_ID_LOCALIZACAO, Authenticator.getLocalizacaoAtual().getIdLocalizacao());
            } else {
                PessoaFisica pessoaFisica = Authenticator.getUsuarioLogado().getPessoaFisica();
                Integer idPessoaFisica = pessoaFisica == null ? -1 : pessoaFisica.getIdPessoa();
                query.append(ViewSituacaoProcessoQuery.queryDestDestinatario);
                params.put(PARAM_LOCALIZACAO_DESTINO, EppMetadadoProvider.LOCALIZACAO_DESTINO.getMetadadoType());
                params.put(PARAM_ID_LOCALIZACAO, Authenticator.getLocalizacaoAtual().getIdLocalizacao());
                params.put(PARAM_PERFIL_DESTINO, EppMetadadoProvider.PERFIL_DESTINO.getMetadadoType());
                params.put(PARAM_ID_PERFIL_TEMPLATE, Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getId());
                params.put(PARAM_PERFIL_DESTINO, EppMetadadoProvider.PERFIL_DESTINO.getMetadadoType());
                params.put(PARAM_ID_PESSOA_FISICA, idPessoaFisica);
            }
        } else {
            query.append(ViewSituacaoProcessoQuery.subQueryTipoProcessoFilter);
            params.put(PARAM_ID_PERFIL_TEMPLATE, Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getId().toString());
            params.put(PARAM_NOME_USUARIO_LOGIN, Authenticator.getUsuarioLogado().getLogin());
            params.put(PARAM_TIPO_USER, PooledActorType.USER.getValue());
            params.put(PARAM_TIPO_GROUP, PooledActorType.GROUP.getValue());
            params.put(PARAM_TIPO_LOCAL, PooledActorType.LOCAL.getValue());
        }

        params.put(PARAM_CODIGO_PERFIL_TEMPLATE, Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getCodigo());
        params.put(PARAM_CODIGO_LOCALIZACAO, Authenticator.getLocalizacaoAtual().getCodigo());

        if (!StringUtil.isEmpty(fluxoBean.getNumeroProcessoRootFilter())) {
            query.append(ViewSituacaoProcessoQuery.queryProcRoot);
            params.put(PARAM_NUMERO_PROCESSO_ROOT, fluxoBean.getNumeroProcessoRootFilter());
        }
        query.append(")");
        query.append(" OPTION (FORCE ORDER)");

        params.put(PARAM_PROCESS_DEFINITION, fluxoBean.getProcessDefinitionId());
        params.put(PARAM_ID_USUARIO_LOGIN, Authenticator.getUsuarioLogado().getIdUsuarioLogin());

        Query nativeQuery = getEntityManager().createNativeQuery(query.toString());

        params.entrySet().forEach( p -> {
            nativeQuery.setParameter(p.getKey(), p.getValue());
        });

        List<Object[]> resultList = nativeQuery.getResultList();
        List<TaskBean> result = new LinkedList<>();
        for(Object[] record : resultList){
            result.add(new TaskBean(record, fluxoBean.isPodeVisualizarComExcessao()));
        }

        if(fluxoBean.isPodeVisualizarComExcessao()){
            params = new HashMap<>();
            StringBuilder queryVariables = new StringBuilder("");
            queryVariables.append(TASK_INSTANCES_QUERY_WITH_VARIABLE_INSTANCE);
            if(fluxoBean.getTipoProcesso() == null){
                queryVariables.append(ViewSituacaoProcessoQuery.subQueryTipoProcessoNull);
                params.put(PARAM_TIPO_PROCESSO_METADADO, EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType());
            }else{
                queryVariables.append(ViewSituacaoProcessoQuery.subQueryTipoProcesso);
                params.put(PARAM_TIPO_PROCESSO_METADADO, EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType());
                params.put(PARAM_TIPO_PROCESSO_VALOR, fluxoBean.getTipoProcesso().value());
            }

            if (TipoProcesso.COMUNICACAO.equals(fluxoBean.getTipoProcesso())) {
                if (fluxoBean.getExpedida() != null && fluxoBean.getExpedida()) {
                    queryVariables.append(ViewSituacaoProcessoQuery.queryLocExpedidor);
                    params.put(PARAM_ID_LOCALIZACAO, Authenticator.getLocalizacaoAtual().getIdLocalizacao());
                } else {
                    PessoaFisica pessoaFisica = Authenticator.getUsuarioLogado().getPessoaFisica();
                    Integer idPessoaFisica = pessoaFisica == null ? -1 : pessoaFisica.getIdPessoa();
                    queryVariables.append(ViewSituacaoProcessoQuery.queryDestDestinatario);
                    params.put(PARAM_LOCALIZACAO_DESTINO, EppMetadadoProvider.LOCALIZACAO_DESTINO.getMetadadoType());
                    params.put(PARAM_ID_LOCALIZACAO, Authenticator.getLocalizacaoAtual().getIdLocalizacao());
                    params.put(PARAM_PERFIL_DESTINO, EppMetadadoProvider.PERFIL_DESTINO.getMetadadoType());
                    params.put(PARAM_ID_PERFIL_TEMPLATE, Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getId());
                    params.put(PARAM_PERFIL_DESTINO, EppMetadadoProvider.PERFIL_DESTINO.getMetadadoType());
                    params.put(PARAM_ID_PESSOA_FISICA, idPessoaFisica);
                }
            }

            if (!StringUtil.isEmpty(fluxoBean.getNumeroProcessoRootFilter())) {
                queryVariables.append(ViewSituacaoProcessoQuery.queryProcRoot);
                params.put(PARAM_NUMERO_PROCESSO_ROOT, fluxoBean.getNumeroProcessoRootFilter());
            }


            queryVariables.append(" OPTION (FORCE ORDER)");

            params.put(PARAM_IDS_TASK_INSTANCE, fluxoBean.getTaskInstancesExcessao());
            params.put(PARAM_PROCESS_DEFINITION, fluxoBean.getProcessDefinitionId());

            Query nativeQueryVariables = getEntityManager().createNativeQuery(queryVariables.toString());

            params.entrySet().forEach( p -> {
                nativeQueryVariables.setParameter(p.getKey(), p.getValue());
            });

            List<Object[]> resultListVariables = nativeQueryVariables.getResultList();
            List<TaskBean> resultVariables = new LinkedList<>();
            for(Object[] record : resultListVariables){
                resultVariables.add(new TaskBean(record, fluxoBean.isPodeVisualizarComExcessao()));
            }

            if(resultVariables != null && !resultVariables.isEmpty()) {
                result.addAll(resultVariables);

                List<TaskBean> collect = result.stream().collect(groupingBy(TaskBean::getTaskName)).values().stream().flatMap(Collection::stream).collect(Collectors.toList());

                return  collect;
            }
        }

        return result;
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
        } else {
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
        Root<ViewSituacaoProcesso> viewSituacaoProcesso = cq.from(ViewSituacaoProcesso.class);
        Join<ViewSituacaoProcesso, TaskInstance> taskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.taskInstance);
        Join<ViewSituacaoProcesso, ProcessInstance> processInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.processInstance);
        Join<ViewSituacaoProcesso, Processo> processo = viewSituacaoProcesso.join(ViewSituacaoProcesso_.processo);

        cq.select(cb.count(taskInstance));

        cq.where(
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

    private Fluxo getFluxoByIdProcessoAndIdTaskInstance(Integer idProcesso, String idTaskInstance) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Fluxo> cq = cb.createQuery(Fluxo.class);
        Root<TaskInstance> taskInstance = cq.from(TaskInstance.class);
        Root<Fluxo> fluxo = cq.from(Fluxo.class);
        Root<Processo> processo = cq.from(Processo.class);
        Join<TaskInstance, ProcessInstance> processInstance = taskInstance.join("processInstance", JoinType.INNER);
        Join<ProcessInstance, ProcessDefinition> processDefinition = processInstance.join("processDefinition", JoinType.INNER);

        cq.select(fluxo);
        cq.where(
           cb.equal(processo.get(Processo_.idProcesso), idProcesso),
           cb.equal(taskInstance.<Long>get("id").as(String.class), idTaskInstance),
           cb.equal(processDefinition.get("name"), fluxo.get(Fluxo_.fluxo))
        );
        return getEntityManager().createQuery(cq).getSingleResult();
    }

	protected Authenticator getAuthenticator() {
	    return Authenticator.instance();
	}
}
