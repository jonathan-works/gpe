package br.com.infox.epp.processo.documento.action;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.entity.DocumentoCompartilhamento;
import br.com.infox.epp.processo.documento.entity.DocumentoCompartilhamento_;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.documento.list.DocumentoList;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumentoPermissao;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumentoPermissao_;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento_;
import br.com.infox.epp.processo.entity.Processo;

@Stateless
public class DocumentoCompartilhamentoSearch {

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }

    /**
     * Retorna true caso o documento esteja compartilhado com algum processo
     */
    public Boolean possuiCompartilhamento(Documento documento) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<DocumentoCompartilhamento> dc = cq.from(DocumentoCompartilhamento.class);
        cq.select(cb.literal(1));
        cq.where(cb.equal(dc.get(DocumentoCompartilhamento_.documento), documento),
                cb.isTrue(dc.get(DocumentoCompartilhamento_.ativo)));
        try {
            Integer result = getEntityManager().createQuery(cq).getSingleResult();
            return Integer.valueOf(1).equals(result);
        } catch (NoResultException nre) {
            return false;
        }
    }

    /**
     * Retorna true caso já exista compartilhamento entre o documento e o processo do parâmetro
     */
    public Boolean possuiCompartilhamento(Documento documento, Processo processoAlvo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<DocumentoCompartilhamento> dc = cq.from(DocumentoCompartilhamento.class);
        cq.select(cb.literal(1));
        cq.where(cb.equal(dc.get(DocumentoCompartilhamento_.documento), documento),
                cb.equal(dc.get(DocumentoCompartilhamento_.processoAlvo), processoAlvo),
                cb.isTrue(dc.get(DocumentoCompartilhamento_.ativo)));
        try {
            Integer result = getEntityManager().createQuery(cq).getSingleResult();
            return Integer.valueOf(1).equals(result);
        } catch (NoResultException nre) {
            return false;
        }
    }

    /**
     * Retorna o compartilhamento de um documento com um processo, caso exista
     */
    public DocumentoCompartilhamento getByDocumentoProcesso(Documento documento, Processo processoAlvo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<DocumentoCompartilhamento> cq = cb.createQuery(DocumentoCompartilhamento.class);
        Root<DocumentoCompartilhamento> dc = cq.from(DocumentoCompartilhamento.class);
        cq.select(dc);
        cq.where(cb.equal(dc.get(DocumentoCompartilhamento_.documento), documento),
                cb.equal(dc.get(DocumentoCompartilhamento_.processoAlvo), processoAlvo));
        try {
            return getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Retorna a lista de documentos compartilhados seguindo a mesma regra de {@link DocumentoList}
     */
    public List<Documento> listDocumentosCompartilhados(Processo processo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Documento> cq = cb.createQuery(Documento.class);
        Root<DocumentoCompartilhamento> dc = cq.from(DocumentoCompartilhamento.class);
        Join<DocumentoCompartilhamento, Documento> doc = dc.join(DocumentoCompartilhamento_.documento, JoinType.INNER);
        Join<Documento, DocumentoBin> docBin = doc.join(Documento_.documentoBin, JoinType.INNER);

        Subquery<Integer> sqSemSigilo = cq.subquery(Integer.class);
        Root<SigiloDocumento> semSigilo = sqSemSigilo.from(SigiloDocumento.class);
        sqSemSigilo.select(cb.literal(1));
        sqSemSigilo.where(cb.isTrue(semSigilo.get(SigiloDocumento_.ativo)),
                cb.equal(semSigilo.get(SigiloDocumento_.documento), doc));

        Subquery<Integer> sqSigilo = cq.subquery(Integer.class);
        Root<SigiloDocumentoPermissao> sigiloPerm = sqSigilo.from(SigiloDocumentoPermissao.class);
        Join<SigiloDocumentoPermissao, SigiloDocumento> sigilo = sigiloPerm.join(SigiloDocumentoPermissao_.sigiloDocumento, JoinType.INNER);
        sqSigilo.select(cb.literal(1));
        sqSigilo.where(cb.equal(sigiloPerm.get(SigiloDocumentoPermissao_.usuario), Authenticator.getUsuarioLogado()),
                cb.equal(sigilo.get(SigiloDocumento_.documento), doc),
                cb.isTrue(sigiloPerm.get(SigiloDocumentoPermissao_.ativo)),
                cb.isTrue(sigilo.get(SigiloDocumento_.ativo)));

        cq.select(doc);
        cq.distinct(true);
        cq.where(cb.isFalse(docBin.get(DocumentoBin_.minuta)),
                cb.equal(dc.get(DocumentoCompartilhamento_.processoAlvo), processo),
                cb.isTrue(dc.get(DocumentoCompartilhamento_.ativo)),
                cb.or(cb.not(cb.exists(sqSemSigilo)), cb.exists(sqSigilo)),
                cb.or(
                    cb.isTrue(docBin.get(DocumentoBin_.suficientementeAssinado)),
                    cb.equal(doc.get(Documento_.localizacao), Authenticator.getLocalizacaoAtual())
                ));
        cq.orderBy(cb.desc(doc.get(Documento_.dataInclusao)));
        return getEntityManager().createQuery(cq).getResultList();
    }
}
