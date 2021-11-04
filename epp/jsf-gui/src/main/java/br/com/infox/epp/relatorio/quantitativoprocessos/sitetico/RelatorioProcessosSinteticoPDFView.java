package br.com.infox.epp.relatorio.quantitativoprocessos.sitetico;

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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections.CollectionUtils;

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
import br.com.infox.epp.relatorio.quantitativoprocessos.StatusProcessoEnum;
import br.com.infox.epp.relatorio.quantitativoprocessos.sitetico.RelatorioProcessosSinteticoVO.RelatorioProcessosSinteticoFluxoVO;
import br.com.infox.epp.view.ViewSituacaoProcesso;
import br.com.infox.epp.view.ViewSituacaoProcesso_;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance_;
import br.com.infox.jsf.util.JsfUtil;
import br.com.infox.seam.exception.BusinessRollbackException;
import lombok.Getter;

@Named
@RequestScoped
public class RelatorioProcessosSinteticoPDFView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	private List<RelatorioProcessosSinteticoVO> relatorioSinteticoList = new ArrayList<RelatorioProcessosSinteticoVO>();

	@Getter
    private String localizacao;

    private List<Integer> assuntos;

    private List<StatusProcessoEnum> status;

    private Date dataInicio;
    private Date dataFim;


	@PostConstruct
	@SuppressWarnings("unchecked")
	private void init() {
	    localizacao = Authenticator.getLocalizacaoAtual().getLocalizacao();

	    JsfUtil jsfUtil = JsfUtil.instance();
	    assuntos = jsfUtil.getFlashParam("assuntos", List.class);
	    status = jsfUtil.getFlashParam("status", List.class);
	    dataInicio = jsfUtil.getFlashParam("dataAberturaInicio", Date.class);
	    dataFim = jsfUtil.getFlashParam("dataAberturaFim", Date.class);

        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        if(CollectionUtils.isEmpty(assuntos)){
            throw new BusinessRollbackException("Nenhum assunto foi informado");
        }

        CriteriaQuery<Tuple> querySwinlane = cb.createQuery(Tuple.class);
        baseQueryRelatorioSintetico(querySwinlane);
        List<Tuple> resultado = em.createQuery(querySwinlane).getResultList();
        for (Tuple rpsVO : resultado) {
            RelatorioProcessosSinteticoVO relatorioProcessosSinteticoVO
                = new RelatorioProcessosSinteticoVO((String) rpsVO.get("localizacao"));
            int indexOf = relatorioSinteticoList.indexOf(relatorioProcessosSinteticoVO);
            if(indexOf > 0) {
                relatorioProcessosSinteticoVO = relatorioSinteticoList.get(indexOf);
            } else {
                relatorioSinteticoList.add(relatorioProcessosSinteticoVO);
            }

            RelatorioProcessosSinteticoFluxoVO relatorioProcessosSinteticoFluxoVO
                = new RelatorioProcessosSinteticoVO.RelatorioProcessosSinteticoFluxoVO(rpsVO.get("fluxo", String.class));
            relatorioProcessosSinteticoVO.getLista().add(relatorioProcessosSinteticoFluxoVO);

            CriteriaQuery<RelatorioProcessosSinteticoVO.RelatorioProcessosSinteticoRowVO>
                query = cb.createQuery(RelatorioProcessosSinteticoVO.RelatorioProcessosSinteticoRowVO.class);
            baseQueryRelatorioSintetico(query, rpsVO.get("idLocalizacao", Integer.class), rpsVO.get("idFluxo", Integer.class));
            relatorioProcessosSinteticoFluxoVO.setLista(em.createQuery(query).getResultList());
        }
    }

	private void baseQueryRelatorioSintetico(CriteriaQuery<Tuple> query) {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        Root<ViewSituacaoProcesso> viewSituacaoProcesso = query.from(ViewSituacaoProcesso.class);
        Join<ViewSituacaoProcesso, Processo> processo = viewSituacaoProcesso.join(ViewSituacaoProcesso_.processo);
        Join<ViewSituacaoProcesso, UsuarioTaskInstance> usuarioTaskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.usuarioTaskInstance);
        Join<UsuarioTaskInstance, Localizacao> localizacao = usuarioTaskInstance.join(UsuarioTaskInstance_.localizacao);
        Join<ViewSituacaoProcesso, Fluxo> fluxo = viewSituacaoProcesso.join(ViewSituacaoProcesso_.fluxo);
        query.where(
            fluxo.get(Fluxo_.idFluxo).in(assuntos),
            cb.equal(usuarioTaskInstance.get(UsuarioTaskInstance_.usuario), Authenticator.getUsuarioLogado().getIdUsuarioLogin())
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

    private <T> void baseQueryRelatorioSintetico(CriteriaQuery<T> query, Integer idLocalizacao, Integer idFluxo) {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        Root<Processo> processo = query.from(Processo.class);
        Join<Processo, UsuarioLogin> usuarioCadastro = processo.join(Processo_.usuarioCadastro);

        Subquery<Integer> sqSituacaoProcesso = query.subquery(Integer.class);
        sqSituacaoProcesso.select(cb.literal(1));

        Root<ViewSituacaoProcesso> viewSituacaoProcesso = sqSituacaoProcesso.from(ViewSituacaoProcesso.class);
        Join<ViewSituacaoProcesso, UsuarioTaskInstance> usuarioTaskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.usuarioTaskInstance);
        Join<UsuarioTaskInstance, Localizacao> localizacao = usuarioTaskInstance.join(UsuarioTaskInstance_.localizacao);
        Join<ViewSituacaoProcesso, Fluxo> fluxo = viewSituacaoProcesso.join(ViewSituacaoProcesso_.fluxo);
        sqSituacaoProcesso.where(
            cb.equal(usuarioTaskInstance.get(UsuarioTaskInstance_.usuario), Authenticator.getUsuarioLogado().getIdUsuarioLogin()),
            cb.equal(viewSituacaoProcesso.get(ViewSituacaoProcesso_.processo), processo),
            cb.equal(fluxo, idFluxo),
            cb.equal(localizacao, idLocalizacao)
        );

        query.where(
            cb.exists(sqSituacaoProcesso)
        );

        aplicarFiltrosProcesso(query, cb, processo);

        query.select(
            cb.construct(query.getResultType(),
                processo.get(Processo_.numeroProcesso)
                , usuarioCadastro.get(UsuarioLogin_.nomeUsuario)
                , processo.get(Processo_.dataFim)
                , processo.get(Processo_.dataInicio)
            )
        );
        query.orderBy(cb.asc(processo.get(Processo_.numeroProcesso)));
    }

    private <T> void aplicarFiltrosProcesso(CriteriaQuery<T> query, CriteriaBuilder cb, Path<Processo> processo) {
        if(dataInicio != null) {
            query.where(
                query.getRestriction(),
                cb.greaterThanOrEqualTo(processo.get(Processo_.dataInicio), cb.literal(dataInicio))
            );
        }
        if(dataFim != null) {
            query.where(
                query.getRestriction(),
                cb.lessThanOrEqualTo(processo.get(Processo_.dataInicio), cb.literal(dataFim))
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
