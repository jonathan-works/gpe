package br.com.infox.epp.relatorio.quantitativoprocessos;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
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
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.entity.TipoParte_;
import br.com.infox.epp.relatorio.quantitativoprocessos.analitico.RelatorioProcessosAnaliticoExcelVO;
import br.com.infox.epp.relatorio.quantitativoprocessos.sitetico.RelatorioProcessosSinteticoExcelVO;
import br.com.infox.epp.view.ViewParticipanteProcesso;
import br.com.infox.epp.view.ViewParticipanteProcesso_;
import br.com.infox.epp.view.ViewSituacaoProcesso;
import br.com.infox.epp.view.ViewSituacaoProcesso_;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance_;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RelatorioProcessosViewSearch {

    public List<RelatorioProcessosAnaliticoExcelVO> getRelatorioAnalitico(
        List<Integer> assuntos,
        Date dataAberturaInicio,
        Date dataAberturaFim,
        Date dataMovimentacaoInicio,
        Date dataMovimentacaoFim,
        Date dataArquivamentoInicio,
        Date dataArquivamentoFim,
        List<StatusProcessoEnum> status
    ){
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<RelatorioProcessosAnaliticoExcelVO> query = cb.createQuery(RelatorioProcessosAnaliticoExcelVO.class);
        Root<ViewSituacaoProcesso> viewSituacaoProcesso = query.from(ViewSituacaoProcesso.class);
        Join<ViewSituacaoProcesso, TaskInstance> taskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.taskInstance);
        Join<ViewSituacaoProcesso, Processo> processo = viewSituacaoProcesso.join(ViewSituacaoProcesso_.processo);
        Join<Processo, NaturezaCategoriaFluxo> naturezaCategoriaFluxo = processo.join(Processo_.naturezaCategoriaFluxo);
        Join<NaturezaCategoriaFluxo, Fluxo> fluxo = naturezaCategoriaFluxo.join(NaturezaCategoriaFluxo_.fluxo);
        Join<Processo, UsuarioLogin> usuarioCadastro = processo.join(Processo_.usuarioCadastro);
        Join<Processo, Localizacao> localizacaoProcesso = processo.join(Processo_.localizacao);
        Join<ViewSituacaoProcesso, UsuarioTaskInstance> usuarioTaskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.usuarioTaskInstance, JoinType.LEFT);
        Join<UsuarioTaskInstance, Localizacao> localizacao = usuarioTaskInstance.join(UsuarioTaskInstance_.localizacao, JoinType.LEFT);
        Join<UsuarioTaskInstance, UsuarioLogin> usuario = usuarioTaskInstance.join(UsuarioTaskInstance_.usuario, JoinType.LEFT);
        Join<ViewSituacaoProcesso, ViewParticipanteProcesso> viewParticipanteProcesso = viewSituacaoProcesso.join(ViewSituacaoProcesso_.participantes, JoinType.LEFT);
        Join<ViewParticipanteProcesso, TipoParte> tipoParte = viewParticipanteProcesso.join(ViewParticipanteProcesso_.tipoParte, JoinType.LEFT);

        query.select(
            cb.construct(query.getResultType(),
                localizacaoProcesso.get(Localizacao_.localizacao)
                , fluxo.get(Fluxo_.fluxo)
                , processo.get(Processo_.numeroProcesso)
                , usuarioCadastro.get(UsuarioLogin_.nomeUsuario)
                , processo.get(Processo_.dataFim)
                , processo.get(Processo_.dataInicio)
                , viewParticipanteProcesso.get(ViewParticipanteProcesso_.nomePessoa)
                , viewParticipanteProcesso.get(ViewParticipanteProcesso_.tipoPessoa)
                , viewParticipanteProcesso.get(ViewParticipanteProcesso_.cpfCnpj)
                , tipoParte.get(TipoParte_.descricao)
                , taskInstance.get("name")
                , cb.concat(cb.concat(localizacao.get(Localizacao_.localizacao), cb.literal("/")), usuario.get(UsuarioLogin_.nomeUsuario))
                , cb.coalesce(taskInstance.get("start"), taskInstance.get("create"))
                , taskInstance.get("end")
            )
        );

        query.where(
            cb.equal(localizacaoProcesso.get(Localizacao_.estruturaFilho), Authenticator.getLocalizacaoAtual().getEstruturaFilho()),
            fluxo.get(Fluxo_.idFluxo).in(assuntos)
        );

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
        if(dataMovimentacaoInicio != null || dataMovimentacaoFim != null) {
            Subquery<Integer> sqExistsMovimentacao = query.subquery(Integer.class);
            sqExistsMovimentacao.select(cb.literal(1));
            Root<ViewSituacaoProcesso> sqViewSituacaoProcesso = sqExistsMovimentacao.from(ViewSituacaoProcesso.class);
            Join<ViewSituacaoProcesso, TaskInstance> sqTaskInstance = sqViewSituacaoProcesso.join(ViewSituacaoProcesso_.taskInstance);
            sqExistsMovimentacao.where(
                cb.equal(sqViewSituacaoProcesso.get(ViewSituacaoProcesso_.processInstance), viewSituacaoProcesso.get(ViewSituacaoProcesso_.processInstance))
            );

            if(dataMovimentacaoInicio != null) {
                sqExistsMovimentacao.where(
                    sqExistsMovimentacao.getRestriction(),
                    cb.greaterThanOrEqualTo(cb.coalesce(sqTaskInstance.get("start"), sqTaskInstance.get("create")),
                        dataMovimentacaoInicio
                    )
                );
            }

            if(dataMovimentacaoFim != null) {
                sqExistsMovimentacao.where(
                    sqExistsMovimentacao.getRestriction(),
                    cb.lessThanOrEqualTo(cb.coalesce(sqTaskInstance.get("end"), cb.coalesce(sqTaskInstance.get("start"), sqTaskInstance.get("create"))),
                        dataMovimentacaoFim
                    )
                );
            }

            query.where(
                query.getRestriction(),
                cb.exists(sqExistsMovimentacao)
            );
        }

        if(!CollectionUtils.isEmpty(status)
                && !status.containsAll(Arrays.asList(StatusProcessoEnum.values()))){
            if(status.contains(StatusProcessoEnum.A)) {
                query.where(
                    query.getRestriction(),
                    cb.isNull(processo.get(Processo_.dataFim))
                );
            } else if(status.contains(StatusProcessoEnum.F)) {
                query.where(
                    query.getRestriction(),
                    cb.isNotNull(processo.get(Processo_.dataFim))
                );
            }
        }

        query.orderBy(
            cb.asc(localizacao.get(Localizacao_.localizacao)),
            cb.asc(fluxo.get(Fluxo_.fluxo)),
            cb.asc(processo.get(Processo_.numeroProcesso)),
            cb.asc(taskInstance.get("create"))
        );

        return em.createQuery(query).getResultList();
    }
    public List<RelatorioProcessosSinteticoExcelVO> getRelatorioSintetico(
        List<Integer> assuntos,
        Date dataInicio,
        Date dataFim,
        List<StatusProcessoEnum> status
    ){
        if(CollectionUtils.isEmpty(assuntos)){
            throw new BusinessRollbackException("Nenhum assunto foi informado");
        }

        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<RelatorioProcessosSinteticoExcelVO> query = cb.createQuery(RelatorioProcessosSinteticoExcelVO.class);
        Root<Processo> processo = query.from(Processo.class);
        Join<Processo, NaturezaCategoriaFluxo> naturezaCategoriaFluxo = processo.join(Processo_.naturezaCategoriaFluxo);
        Join<NaturezaCategoriaFluxo, Fluxo> fluxo = naturezaCategoriaFluxo.join(NaturezaCategoriaFluxo_.fluxo);
        Join<Processo, UsuarioLogin> usuarioCadastro = processo.join(Processo_.usuarioCadastro);
        Join<Processo, Localizacao> localizacao = processo.join(Processo_.localizacao);

        query.where(
            cb.like(localizacao.get(Localizacao_.caminhoCompleto), cb.literal(Authenticator.getLocalizacaoAtual().getCaminhoCompleto() + "%")),
            fluxo.get(Fluxo_.idFluxo).in(assuntos)
        );

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

        if(!CollectionUtils.isEmpty(status)
                && !status.containsAll(Arrays.asList(StatusProcessoEnum.values()))){
            if(status.contains(StatusProcessoEnum.A)) {
                query.where(
                    query.getRestriction(),
                    cb.isNull(processo.get(Processo_.dataFim))
                );
            } else if(status.contains(StatusProcessoEnum.F)) {
                query.where(
                    query.getRestriction(),
                    cb.isNotNull(processo.get(Processo_.dataFim))
                );
            }
        }

        query.select(
            cb.construct(query.getResultType(),
                localizacao.get(Localizacao_.localizacao)
                , fluxo.get(Fluxo_.fluxo)
                , processo.get(Processo_.numeroProcesso)
                , usuarioCadastro.get(UsuarioLogin_.nomeUsuario)
                , processo.get(Processo_.dataFim)
                , processo.get(Processo_.dataInicio)
            )
        );

        query.orderBy(
            cb.asc(localizacao.get(Localizacao_.localizacao)),
            cb.asc(fluxo.get(Fluxo_.fluxo)),
            cb.asc(processo.get(Processo_.numeroProcesso))
        );

        return em.createQuery(query).getResultList();
    }


}