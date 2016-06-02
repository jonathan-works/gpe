package br.com.infox.epp.processo.comunicacao.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao_;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao_;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.type.TipoProcesso;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ComunicacaoSearch extends PersistenceController {

    public Integer getMaximoDiasCienciaMaisPrazo(Integer idProcesso) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<DestinatarioModeloComunicacao> destinatarioComunicacao = cq.from(DestinatarioModeloComunicacao.class);
        Join<DestinatarioModeloComunicacao, Processo> processo = destinatarioComunicacao.join(DestinatarioModeloComunicacao_.processo, JoinType.INNER);
        Join<Processo, MetadadoProcesso> tipoProcesso = processo.join(Processo_.metadadoProcessoList, JoinType.INNER);
        Join<DestinatarioModeloComunicacao, ModeloComunicacao> modeloComunicacao = destinatarioComunicacao.join(DestinatarioModeloComunicacao_.modeloComunicacao, JoinType.INNER);
        Join<ModeloComunicacao, TipoComunicacao> tipoComunicacao = modeloComunicacao.join(ModeloComunicacao_.tipoComunicacao, JoinType.INNER);
        cq.select(cb.sum(cb.max(tipoComunicacao.get(TipoComunicacao_.quantidadeDiasCiencia)), cb.max(destinatarioComunicacao.get(DestinatarioModeloComunicacao_.prazo))));
        cq.where(
            cb.isTrue(destinatarioComunicacao.get(DestinatarioModeloComunicacao_.expedido)),
            cb.isNotNull(processo.get(Processo_.idJbpm)),
            cb.isNotNull(processo.get(Processo_.processoPai)),
            cb.isNull(processo.get(Processo_.dataFim)),
            cb.equal(processo.get(Processo_.processoPai).get(Processo_.idProcesso), cb.literal(idProcesso)),
            cb.equal(tipoProcesso.get(MetadadoProcesso_.metadadoType), cb.literal(EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType())),
            cb.equal(tipoProcesso.get(MetadadoProcesso_.valor), cb.literal(TipoProcesso.COMUNICACAO.value()))
        );
        Integer integer = getEntityManager().createQuery(cq).getSingleResult();
        return integer == null ? 0 : integer;
    }
    
}
