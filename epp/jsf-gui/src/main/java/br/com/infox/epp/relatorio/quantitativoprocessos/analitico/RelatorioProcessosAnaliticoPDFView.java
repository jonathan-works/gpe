package br.com.infox.epp.relatorio.quantitativoprocessos.analitico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections.CollectionUtils;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
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
import br.com.infox.jsf.util.JsfUtil;
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

	    JsfUtil jsfUtil = JsfUtil.instance();
	    assuntos = jsfUtil.getFlashParam("assuntos", List.class);
	    status = jsfUtil.getFlashParam("status", List.class);
	    dataAberturaInicio = jsfUtil.getFlashParam("dataAberturaInicio", Date.class);
	    dataAberturaFim = jsfUtil.getFlashParam("dataAberturaFim", Date.class);
	    dataMovimentacaoInicio = jsfUtil.getFlashParam("dataMovimentacaoInicio", Date.class);
	    dataMovimentacaoFim = jsfUtil.getFlashParam("dataMovimentacaoFim", Date.class);
	    dataArquivamentoInicio = jsfUtil.getFlashParam("dataArquivamentoInicio", Date.class);
	    dataArquivamentoFim = jsfUtil.getFlashParam("dataArquivamentoFim", Date.class);

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
        Join<ViewSituacaoProcesso, UsuarioTaskInstance> usuarioTaskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.usuarioTaskInstance);
        Join<UsuarioTaskInstance, Localizacao> localizacao = usuarioTaskInstance.join(UsuarioTaskInstance_.localizacao);
        Join<UsuarioTaskInstance, UsuarioLogin> usuario = usuarioTaskInstance.join(UsuarioTaskInstance_.usuario);
        Join<ViewSituacaoProcesso, Fluxo> fluxo = viewSituacaoProcesso.join(ViewSituacaoProcesso_.fluxo);
	    query.where(
            cb.equal(processo, idProcesso),
            cb.equal(fluxo, idFluxo)
        );

	    query.select(cb.construct(RelatorioProcessosAnaliticoTarefaVO.class,
            taskInstance.get("name"),
            cb.concat(cb.concat(localizacao.get(Localizacao_.localizacao), cb.literal("/")), usuario.get(UsuarioLogin_.nomeUsuario)),
            taskInstance.get("start"),
            taskInstance.get("end")
        ));

	    query.orderBy(cb.asc(taskInstance.get("create")));

	    return em.createQuery(query).getResultList();
	}

	private void baseQueryRelatorioAnalitico(CriteriaQuery<Tuple> query) {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        Root<ViewSituacaoProcesso> viewSituacaoProcesso = query.from(ViewSituacaoProcesso.class);
        Join<ViewSituacaoProcesso, TaskInstance> taskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.taskInstance);
        Join<ViewSituacaoProcesso, Processo> processo = viewSituacaoProcesso.join(ViewSituacaoProcesso_.processo);
        Join<ViewSituacaoProcesso, UsuarioTaskInstance> usuarioTaskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.usuarioTaskInstance);
        Join<UsuarioTaskInstance, UsuarioLogin> usuario = usuarioTaskInstance.join(UsuarioTaskInstance_.usuario);
        Join<UsuarioTaskInstance, Localizacao> localizacao = usuarioTaskInstance.join(UsuarioTaskInstance_.localizacao);
        Join<ViewSituacaoProcesso, Fluxo> fluxo = viewSituacaoProcesso.join(ViewSituacaoProcesso_.fluxo);
        query.where(
            fluxo.get(Fluxo_.idFluxo).in(assuntos),
            cb.equal(usuario, Authenticator.getUsuarioLogado().getIdUsuarioLogin())
        );

        aplicarFiltrosProcesso(query, cb, processo);
        aplicarFiltrosTaskInstance(query, cb, taskInstance);

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
        Join<Processo, UsuarioLogin> usuarioCadastro = processo.join(Processo_.usuarioCadastro);

        Subquery<Integer> sqSituacaoProcesso = query.subquery(Integer.class);
        sqSituacaoProcesso.select(cb.literal(1));

        Root<ViewSituacaoProcesso> viewSituacaoProcesso = sqSituacaoProcesso.from(ViewSituacaoProcesso.class);
        Join<ViewSituacaoProcesso, TaskInstance> taskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.taskInstance);
        Join<ViewSituacaoProcesso, UsuarioTaskInstance> usuarioTaskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.usuarioTaskInstance);
        Join<UsuarioTaskInstance, Localizacao> localizacao = usuarioTaskInstance.join(UsuarioTaskInstance_.localizacao);
        Join<ViewSituacaoProcesso, Fluxo> fluxo = viewSituacaoProcesso.join(ViewSituacaoProcesso_.fluxo);
        sqSituacaoProcesso.where(
            cb.equal(viewSituacaoProcesso.get(ViewSituacaoProcesso_.processo), processo),
            cb.equal(fluxo, idFluxo),
            cb.equal(localizacao, idLocalizacao)
        );
        aplicarFiltrosTaskInstance(sqSituacaoProcesso, cb, taskInstance);

        query.where(
            cb.exists(sqSituacaoProcesso)
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

    private <T> void aplicarFiltrosTaskInstance(AbstractQuery<T> query, CriteriaBuilder cb,
        Path<TaskInstance> taskInstance
    ) {
        if(dataMovimentacaoInicio != null || dataMovimentacaoFim != null) {
            if(dataMovimentacaoInicio != null) {
                query.where(
                    query.getRestriction(),
                    cb.greaterThanOrEqualTo(cb.coalesce(taskInstance.get("start"), taskInstance.get("create")),
                        dataMovimentacaoInicio
                    )
                );
            }

            if(dataMovimentacaoFim != null) {
                query.where(
                    query.getRestriction(),
                    cb.lessThanOrEqualTo(cb.coalesce(taskInstance.get("end"), cb.coalesce(taskInstance.get("start"), taskInstance.get("create"))),
                        dataMovimentacaoFim
                    )
                );
            }
        }
    }
    private <T> void aplicarFiltrosProcesso(CriteriaQuery<T> query, CriteriaBuilder cb,
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
    }

}
