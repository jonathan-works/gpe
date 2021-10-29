package br.com.infox.epp.relatorio.processos;

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
import br.com.infox.epp.view.ViewSituacaoProcesso;
import br.com.infox.epp.view.ViewSituacaoProcesso_;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance_;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RelatorioProcessosViewSearch {

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
        Join<Processo, UsuarioLogin> usuarioCadastro = processo.join(Processo_.usuarioCadastro);
        Root<Localizacao> localizacao = query.from(Localizacao.class);
        Root<Fluxo> fluxo = query.from(Fluxo.class);

        Subquery<Integer> sqSituacaoProcesso = query.subquery(Integer.class);
        sqSituacaoProcesso.select(cb.literal(1));

        Root<ViewSituacaoProcesso> viewSituacaoProcesso = sqSituacaoProcesso.from(ViewSituacaoProcesso.class);
        Join<ViewSituacaoProcesso, UsuarioTaskInstance> usuarioTaskInstance = viewSituacaoProcesso.join(ViewSituacaoProcesso_.usuarioTaskInstance);
        sqSituacaoProcesso.where(
            cb.equal(viewSituacaoProcesso.get(ViewSituacaoProcesso_.processo), processo),
            cb.equal(fluxo, viewSituacaoProcesso.join(ViewSituacaoProcesso_.fluxo)),
            cb.equal(localizacao, usuarioTaskInstance.join(UsuarioTaskInstance_.localizacao))
        );

        query.where(
            cb.equal(usuarioCadastro, Authenticator.getUsuarioLogado().getIdUsuarioLogin()),
            fluxo.get(Fluxo_.idFluxo).in(assuntos),
            cb.exists(sqSituacaoProcesso)
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
                , cb.isNull(processo.get(Processo_.dataFim))
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