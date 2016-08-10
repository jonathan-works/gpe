package br.com.infox.epp.documento;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento_;
import br.com.infox.epp.entrega.documentos.Entrega;
import br.com.infox.epp.entrega.documentos.Entrega_;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.PastaRestricao;
import br.com.infox.epp.processo.documento.entity.PastaRestricao_;
import br.com.infox.epp.processo.documento.entity.Pasta_;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento_;
import br.com.infox.epp.processo.documento.type.PastaRestricaoEnum;

@Stateless
public class DocumentoBinSearch extends PersistenceController {

    public DocumentoBin getDocumentoPublicoByUUID(UUID uuid) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        CriteriaQuery<DocumentoBin> cq = cb.createQuery(DocumentoBin.class);

        Root<DocumentoBin> documentoBin = cq.from(DocumentoBin.class);

        Predicate isSigiloso = createIsDocumentoSigilosoPredicate(cq.subquery(Integer.class), documentoBin);
        Predicate isCertidaoEntrega = createIsCertidaoEntregaPredicate(cq.subquery(Integer.class), documentoBin);
        Predicate documentoNaoPublico = createIsDocumentoPublicoPredicate(cq.subquery(Integer.class), documentoBin);

        Predicate uuidIgual = cb.equal(documentoBin.get(DocumentoBin_.uuid), uuid);
        Predicate suficientementeAssinado = cb.isTrue(documentoBin.get(DocumentoBin_.suficientementeAssinado));
        Predicate naoMinuta = cb.isFalse(documentoBin.get(DocumentoBin_.minuta));
        cq.where(uuidIgual, suficientementeAssinado, naoMinuta,
                cb.not(cb.and(isSigiloso, isCertidaoEntrega.not(), documentoNaoPublico.not())));
        return getEntityManager().createQuery(cq).getSingleResult();
    }

    private Predicate createIsDocumentoPublicoPredicate(Subquery<Integer> subquery, Path<DocumentoBin> documentoBin) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<PastaRestricao> restricao = subquery.from(PastaRestricao.class);
        Join<?, Pasta> pasta = restricao.join(PastaRestricao_.pasta, JoinType.INNER);
        From<?, Documento> documento = pasta.join(Pasta_.documentosList, JoinType.INNER);
        Join<?, ClassificacaoDocumento> classificacaoDocumento = documento.join(Documento_.classificacaoDocumento, JoinType.INNER);

        subquery.select(cb.literal(1)).where(
                cb.equal(documento.get(Documento_.documentoBin), documentoBin),
                cb.isTrue(classificacaoDocumento.get(ClassificacaoDocumento_.publico)),
                cb.isTrue(restricao.get(PastaRestricao_.read)),
                cb.equal(restricao.get(PastaRestricao_.tipoPastaRestricao), PastaRestricaoEnum.D));
        return cb.exists(subquery);
    }

    private Predicate createIsCertidaoEntregaPredicate(Subquery<Integer> subquery, Path<DocumentoBin> documentoBin) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<Entrega> entrega = subquery.from(Entrega.class);
        subquery.select(cb.literal(1)).where(cb.equal(entrega.get(Entrega_.certidaoEntrega), documentoBin));
        return cb.exists(subquery);
    }

    private Predicate createIsDocumentoSigilosoPredicate(Subquery<Integer> subquery, Path<DocumentoBin> documentoBin) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<SigiloDocumento> sigiloDocumento = subquery.from(SigiloDocumento.class);
        Join<?, Documento> docSigiloso = sigiloDocumento.join(SigiloDocumento_.documento, JoinType.INNER);
        subquery.select(cb.literal(1)).where(cb.equal(docSigiloso.get(Documento_.documentoBin), documentoBin));
        return cb.exists(subquery);
    }

}
