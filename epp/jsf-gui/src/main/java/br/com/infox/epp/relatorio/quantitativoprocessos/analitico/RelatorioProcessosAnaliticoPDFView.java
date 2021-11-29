package br.com.infox.epp.relatorio.quantitativoprocessos.analitico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections.CollectionUtils;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.entity.TipoParte_;
import br.com.infox.epp.relatorio.quantitativoprocessos.StatusProcessoEnum;
import br.com.infox.epp.relatorio.quantitativoprocessos.analitico.RelatorioProcessosAnaliticoVO.RelatorioProcessosAnaliticoParticipanteVO;
import br.com.infox.epp.relatorio.quantitativoprocessos.analitico.RelatorioProcessosAnaliticoVO.RelatorioProcessosAnaliticoRowVO;
import br.com.infox.epp.relatorio.quantitativoprocessos.analitico.RelatorioProcessosAnaliticoVO.RelatorioProcessosAnaliticoTarefaVO;
import br.com.infox.epp.view.ViewParticipanteProcesso;
import br.com.infox.epp.view.ViewParticipanteProcesso_;
import br.com.infox.epp.view.ViewSituacaoProcesso;
import br.com.infox.epp.view.ViewSituacaoProcesso_;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance_;
import br.com.infox.seam.exception.BusinessRollbackException;
import lombok.Getter;

@Named
@RequestScoped
public class RelatorioProcessosAnaliticoPDFView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	private List<RelatorioProcessosAnaliticoVO> relatorioAnaliticoList = new ArrayList<RelatorioProcessosAnaliticoVO>();

	@Getter
    private String localizacao;

    private List<Integer> assuntos;

    private List<StatusProcessoEnum> status;

    private Date dataAberturaInicio;
    private Date dataAberturaFim;
    private Date dataMovimentacaoInicio;
    private Date dataMovimentacaoFim;
    private Date dataArquivamentoInicio;
    private Date dataArquivamentoFim;


	@PostConstruct
	@SuppressWarnings("unchecked")
	private void init() {
	    localizacao = Authenticator.getLocalizacaoAtual().getLocalizacao();
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        assuntos = (List<Integer>) sessionMap.get("assuntos");
        status = (List<StatusProcessoEnum>) sessionMap.get("status");
        dataAberturaInicio = (Date) sessionMap.get("dataAberturaInicio");
        dataAberturaFim = (Date) sessionMap.get("dataAberturaFim");
        dataMovimentacaoInicio = (Date) sessionMap.get("dataMovimentacaoInicio");
        dataMovimentacaoFim = (Date) sessionMap.get("dataMovimentacaoFim");
        dataArquivamentoInicio = (Date) sessionMap.get("dataArquivamentoInicio");
        dataArquivamentoFim = (Date) sessionMap.get("dataArquivamentoFim");

        sessionMap.remove("assuntos");
        sessionMap.remove("status");
        sessionMap.remove("dataAberturaInicio");
        sessionMap.remove("dataAberturaFim");
        sessionMap.remove("dataMovimentacaoInicio");
        sessionMap.remove("dataMovimentacaoFim");
        sessionMap.remove("dataArquivamentoInicio");
        sessionMap.remove("dataArquivamentoFim");

        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        if(CollectionUtils.isEmpty(assuntos)){
            throw new BusinessRollbackException("Nenhum assunto foi informado");
        }
        CriteriaQuery<Tuple> querySwinlane = cb.createQuery(Tuple.class);
        baseQueryRelatorioAnalitico(querySwinlane);
        List<Tuple> resultado = em.createQuery(querySwinlane).getResultList();
        for (Tuple rpsVO : resultado) {
            RelatorioProcessosAnaliticoVO relatorioProcessosAnaliticoVO
                = new RelatorioProcessosAnaliticoVO((String) rpsVO.get("localizacao"));
            int indexOf = relatorioAnaliticoList.indexOf(relatorioProcessosAnaliticoVO);
            if(indexOf > 0) {
                relatorioProcessosAnaliticoVO = relatorioAnaliticoList.get(indexOf);
            } else {
                relatorioAnaliticoList.add(relatorioProcessosAnaliticoVO);
            }

            RelatorioProcessosAnaliticoVO.RelatorioProcessosAnaliticoFluxoVO relatorioProcessosAnaliticoFluxoVO
                = new RelatorioProcessosAnaliticoVO.RelatorioProcessosAnaliticoFluxoVO(rpsVO.get("fluxo", String.class));
            relatorioProcessosAnaliticoVO.getLista().add(relatorioProcessosAnaliticoFluxoVO);

            CriteriaQuery<RelatorioProcessosAnaliticoVO.RelatorioProcessosAnaliticoRowVO>
                query = cb.createQuery(RelatorioProcessosAnaliticoVO.RelatorioProcessosAnaliticoRowVO.class);
            baseQueryRelatorioAnalitico(query, rpsVO.get("idLocalizacao", Integer.class), rpsVO.get("idFluxo", Integer.class));
            relatorioProcessosAnaliticoFluxoVO.setLista(em.createQuery(query).getResultList());


            for (RelatorioProcessosAnaliticoRowVO relatorioProcessosAnaliticoRowVO : relatorioProcessosAnaliticoFluxoVO.getLista()) {
                try {
                    relatorioProcessosAnaliticoRowVO.setParticipantes(
                        getParticipantes(relatorioProcessosAnaliticoRowVO.getIdProcesso())
                    );
                }catch (Exception e) {
                    throw new BusinessRollbackException("erro", e);
                }
                try {
                    relatorioProcessosAnaliticoRowVO.setTarefas(
                        getTarefas(relatorioProcessosAnaliticoRowVO.getIdProcesso(), rpsVO.get("idFluxo", Integer.class))
                    );
                }catch (Exception e) {
                    throw new BusinessRollbackException("erro", e);
                }
            }

        }
    }

	private List<RelatorioProcessosAnaliticoVO.RelatorioProcessosAnaliticoParticipanteVO> getParticipantes(Integer idProcesso) {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<RelatorioProcessosAnaliticoParticipanteVO> query = cb.createQuery(RelatorioProcessosAnaliticoVO.RelatorioProcessosAnaliticoParticipanteVO.class);
        Root<ViewParticipanteProcesso> viewParticipanteProcesso = query.from(ViewParticipanteProcesso.class);
        Join<ViewParticipanteProcesso, TipoParte> tipoParte = viewParticipanteProcesso.join(ViewParticipanteProcesso_.tipoParte);
        query.where(
            cb.equal(viewParticipanteProcesso.get(ViewParticipanteProcesso_.processo), idProcesso)
        );

        query.select(cb.construct(RelatorioProcessosAnaliticoParticipanteVO.class,
            viewParticipanteProcesso.get(ViewParticipanteProcesso_.nomePessoa),
            viewParticipanteProcesso.get(ViewParticipanteProcesso_.cpfCnpj),
            viewParticipanteProcesso.get(ViewParticipanteProcesso_.tipoPessoa),
            tipoParte.get(TipoParte_.descricao)
        ));

        query.orderBy(cb.asc(viewParticipanteProcesso.get(ViewParticipanteProcesso_.nomePessoa)));

        return em.createQuery(query).getResultList();
	}

	private List<RelatorioProcessosAnaliticoVO.RelatorioProcessosAnaliticoTarefaVO> getTarefas(Integer idProcesso, Integer idFluxo) {
	    EntityManager em = EntityManagerProducer.getEntityManager();
	    CriteriaBuilder cb = em.getCriteriaBuilder();

	    CriteriaQuery<RelatorioProcessosAnaliticoTarefaVO> query = cb.createQuery(RelatorioProcessosAnaliticoVO.RelatorioProcessosAnaliticoTarefaVO.class);
	    Root<ViewSituacaoProcesso> viewSituacaoProcesso = query.from(ViewSituacaoProcesso.class);
        Join<ViewSituacaoProcesso, TaskInstance> taskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.taskInstance);
        Join<ViewSituacaoProcesso, Processo> processo = viewSituacaoProcesso.join(ViewSituacaoProcesso_.processo);
        Join<ViewSituacaoProcesso, UsuarioTaskInstance> usuarioTaskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.usuarioTaskInstance, JoinType.LEFT);
        Join<UsuarioTaskInstance, Localizacao> localizacao = usuarioTaskInstance.join(UsuarioTaskInstance_.localizacao, JoinType.LEFT);
        Join<UsuarioTaskInstance, UsuarioLogin> usuario = usuarioTaskInstance.join(UsuarioTaskInstance_.usuario, JoinType.LEFT);
        Join<ViewSituacaoProcesso, Fluxo> fluxo = viewSituacaoProcesso.join(ViewSituacaoProcesso_.fluxo);
        Join<TaskInstance, SwimlaneInstance> swimlaneInstance = taskInstance.join("swimlaneInstance", JoinType.LEFT);
	    query.where(
            cb.equal(processo, idProcesso),
            cb.equal(fluxo, idFluxo)
        );

	    query.select(cb.construct(RelatorioProcessosAnaliticoTarefaVO.class,
            taskInstance.get("name"),
            cb.concat(cb.concat(cb.coalesce(localizacao.get(Localizacao_.localizacao), swimlaneInstance.get("name")), cb.literal("/")), usuario.get(UsuarioLogin_.nomeUsuario)),
            cb.coalesce(taskInstance.get("start"), taskInstance.get("create")),
            taskInstance.get("end")
        ));

	    query.orderBy(cb.asc(taskInstance.get("create")));

	    return em.createQuery(query).getResultList();
	}

	private void baseQueryRelatorioAnalitico(CriteriaQuery<Tuple> query) {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        Root<Processo> processo = query.from(Processo.class);
        Join<Processo, Localizacao> localizacao = processo.join(Processo_.localizacao);
        Join<Processo, NaturezaCategoriaFluxo> naturezaCategoriaFluxo = processo.join(Processo_.naturezaCategoriaFluxo);
        Join<NaturezaCategoriaFluxo, Fluxo> fluxo = naturezaCategoriaFluxo.join(NaturezaCategoriaFluxo_.fluxo);

        query.where(
            cb.like(localizacao.get(Localizacao_.caminhoCompleto), cb.literal(Authenticator.getLocalizacaoAtual().getCaminhoCompleto() + "%")),
            fluxo.get(Fluxo_.idFluxo).in(assuntos)
        );
        aplicarFiltrosProcesso(query, cb, processo);

        query.select(cb.construct(query.getResultType(),
            localizacao.get(Localizacao_.localizacao).alias("localizacao"),
            localizacao.get(Localizacao_.idLocalizacao).alias("idLocalizacao"),
            fluxo.get(Fluxo_.fluxo).alias("fluxo"),
            fluxo.get(Fluxo_.idFluxo).alias("idFluxo")
        ));
        query.groupBy(
            localizacao.get(Localizacao_.idLocalizacao),
            localizacao.get(Localizacao_.localizacao),
            fluxo.get(Fluxo_.fluxo),
            fluxo.get(Fluxo_.idFluxo)
        );
        query.orderBy(cb.asc(localizacao.get(Localizacao_.localizacao)), cb.asc(fluxo.get(Fluxo_.fluxo)));
	}

    private <T> void baseQueryRelatorioAnalitico(CriteriaQuery<T> query, Integer idLocalizacao, Integer idFluxo) {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        Root<Processo> processo = query.from(Processo.class);
        Join<Processo, Localizacao> localizacao = processo.join(Processo_.localizacao);
        Join<Processo, UsuarioLogin> usuarioCadastro = processo.join(Processo_.usuarioCadastro);
        Join<Processo, NaturezaCategoriaFluxo> naturezaCategoriaFluxo = processo.join(Processo_.naturezaCategoriaFluxo);
        Join<NaturezaCategoriaFluxo, Fluxo> fluxo = naturezaCategoriaFluxo.join(NaturezaCategoriaFluxo_.fluxo);

        query.where(
            cb.equal(localizacao, idLocalizacao)
            , cb.equal(fluxo, idFluxo)
        );
        aplicarFiltrosProcesso(query, cb, processo);

        query.select(
            cb.construct(query.getResultType(),
                processo.get(Processo_.idProcesso),
                processo.get(Processo_.numeroProcesso)
                , usuarioCadastro.get(UsuarioLogin_.nomeUsuario)
                , processo.get(Processo_.dataFim)
                , processo.get(Processo_.dataInicio)
            )
        );
        query.orderBy(cb.asc(processo.get(Processo_.numeroProcesso)));
    }

    private <T> void aplicarFiltrosProcesso(
        CriteriaQuery<T> query,
        CriteriaBuilder cb,
        Path<Processo> processo
    ){
        if(dataAberturaInicio != null) {
            query.where(
                query.getRestriction(),
                cb.greaterThanOrEqualTo(processo.get(Processo_.dataInicio), cb.literal(dataAberturaInicio))
            );
        }
        if(dataAberturaFim != null) {
            query.where(
                query.getRestriction(),
                cb.lessThanOrEqualTo(processo.get(Processo_.dataInicio), cb.literal(dataAberturaFim))
            );
        }
        if(dataArquivamentoInicio != null) {
            query.where(
                query.getRestriction(),
                cb.greaterThanOrEqualTo(processo.get(Processo_.dataFim), cb.literal(dataArquivamentoInicio))
            );
        }
        if(dataArquivamentoFim != null) {
            query.where(
                query.getRestriction(),
                cb.lessThanOrEqualTo(processo.get(Processo_.dataFim), cb.literal(dataArquivamentoFim))
            );
        }

        if(!CollectionUtils.isEmpty(this.status)
                && !this.status.containsAll(Arrays.asList(StatusProcessoEnum.values()))){
            if(this.status.contains(StatusProcessoEnum.A)) {
                query.where(
                    query.getRestriction(),
                    cb.isNull(processo.get(Processo_.dataFim))
                );
            } else if(this.status.contains(StatusProcessoEnum.F)) {
                query.where(
                    query.getRestriction(),
                    cb.isNotNull(processo.get(Processo_.dataFim))
                );
            }
        }

        if(dataMovimentacaoInicio != null || dataMovimentacaoFim != null) {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            subquery.select(cb.literal(1));
            Root<ViewSituacaoProcesso> viewSituacaoProcesso = subquery.from(ViewSituacaoProcesso.class);
            Join<ViewSituacaoProcesso, TaskInstance> taskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.taskInstance);
            subquery.where(
                cb.equal(viewSituacaoProcesso.get(ViewSituacaoProcesso_.processo), processo)
            );
            if(dataMovimentacaoInicio != null) {
                subquery.where(
                    subquery.getRestriction(),
                    cb.greaterThanOrEqualTo(cb.coalesce(taskInstance.get("start"), taskInstance.get("create")),
                        dataMovimentacaoInicio
                    )
                );
            }

            if(dataMovimentacaoFim != null) {
                subquery.where(
                    subquery.getRestriction(),
                    cb.lessThanOrEqualTo(cb.coalesce(taskInstance.get("end"), cb.coalesce(taskInstance.get("start"), taskInstance.get("create"))),
                        dataMovimentacaoFim
                    )
                );
            }
            query.where(
                query.getRestriction(),
                cb.exists(subquery)
            );
        }
    }

}
