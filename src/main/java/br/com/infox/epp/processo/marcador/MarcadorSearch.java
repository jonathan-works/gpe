package br.com.infox.epp.processo.marcador;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.entity.DocumentoTemporario;
import br.com.infox.epp.processo.documento.entity.DocumentoTemporario_;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.Pasta_;
import br.com.infox.epp.processo.entity.Processo_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class MarcadorSearch extends PersistenceController {
    
    public List<String> listByProcesso(Integer idProcesso) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<DocumentoBin> documentoBin = cq.from(DocumentoBin.class);
        Root<Pasta> pasta = cq.from(Pasta.class); 
        Join<DocumentoBin, Documento> documento = documentoBin.join(DocumentoBin_.documentoList, JoinType.LEFT);
        Join<DocumentoBin, DocumentoTemporario> documentoTemporario = documentoBin.join(DocumentoBin_.documentoTemporarioList, JoinType.LEFT);
        Join<DocumentoBin, Marcador> marcador = documentoBin.join(DocumentoBin_.marcadores, JoinType.INNER);
        Expression<String> codigo = marcador.get(Marcador_.codigo);
        cq.select(codigo).distinct(true);
        cq.where(
            cb.or(
                cb.equal(documento.get(Documento_.pasta).get(Pasta_.id), pasta.get(Pasta_.id)),
                cb.equal(documentoTemporario.get(DocumentoTemporario_.pasta).get(Pasta_.id), pasta.get(Pasta_.id))
            ),
            cb.isNotNull(pasta.get(Pasta_.processo)),
            cb.equal(pasta.get(Pasta_.processo).get(Processo_.idProcesso), cb.literal(idProcesso))
        );
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public List<Marcador> listByProcessoAndCodigosMarcadores(Integer idProcesso, Collection<String> codigoMarcadores) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Marcador> cq = cb.createQuery(Marcador.class);
        Root<DocumentoBin> documentoBin = cq.from(DocumentoBin.class);
        Root<Pasta> pasta = cq.from(Pasta.class); 
        Join<DocumentoBin, Documento> documento = documentoBin.join(DocumentoBin_.documentoList, JoinType.LEFT);
        Join<DocumentoBin, DocumentoTemporario> documentoTemporario = documentoBin.join(DocumentoBin_.documentoTemporarioList, JoinType.LEFT);
        Join<DocumentoBin, Marcador> marcador = documentoBin.join(DocumentoBin_.marcadores, JoinType.INNER);
        cq.select(marcador).distinct(true);
        cq.where(
            cb.or(
                cb.equal(documento.get(Documento_.pasta).get(Pasta_.id), pasta.get(Pasta_.id)),
                cb.equal(documentoTemporario.get(DocumentoTemporario_.pasta).get(Pasta_.id), pasta.get(Pasta_.id))
            ),
            cb.isNotNull(pasta.get(Pasta_.processo)),
            cb.equal(pasta.get(Pasta_.processo).get(Processo_.idProcesso), cb.literal(idProcesso)),
            marcador.get(Marcador_.codigo).in(codigoMarcadores)
        );
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public List<String> listByProcessoAndCodigo(Integer idProcesso, String codigoMarcador) {
        return listByProcessoAndCodigo(idProcesso, codigoMarcador, null);
    }
    
    public List<String> listByProcessoAndCodigo(Integer idProcesso, String codigoMarcador, Collection<String> codigoMarcadores) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<DocumentoBin> documentoBin = cq.from(DocumentoBin.class);
        Root<Pasta> pasta = cq.from(Pasta.class); 
        Join<DocumentoBin, Documento> documento = documentoBin.join(DocumentoBin_.documentoList, JoinType.LEFT);
        Join<DocumentoBin, DocumentoTemporario> documentoTemporario = documentoBin.join(DocumentoBin_.documentoTemporarioList, JoinType.LEFT);
        Join<DocumentoBin, Marcador> marcador = documentoBin.join(DocumentoBin_.marcadores, JoinType.INNER);
        Expression<String> codigo = marcador.get(Marcador_.codigo);
        cq.select(codigo).distinct(true);
        cq.where(
            cb.or(
                cb.equal(documento.get(Documento_.pasta).get(Pasta_.id), pasta.get(Pasta_.id)),
                cb.equal(documentoTemporario.get(DocumentoTemporario_.pasta).get(Pasta_.id), pasta.get(Pasta_.id))
            ),
            cb.isNotNull(pasta.get(Pasta_.processo)),
            cb.equal(pasta.get(Pasta_.processo).get(Processo_.idProcesso), cb.literal(idProcesso)),
            cb.like(marcador.get(Marcador_.codigo), cb.literal("%" + codigoMarcador + "%"))
        );
        if (codigoMarcadores != null && !codigoMarcadores.isEmpty()) {
            Predicate where = cq.getRestriction();
            cq.where(
                where,
                cb.not(marcador.get(Marcador_.codigo).in(codigoMarcadores))
            );
        }
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public List<String> listByPastaAndCodigo(Integer idPasta, String codigoMarcador, Collection<String> codigoMarcadores) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<DocumentoBin> documentoBin = cq.from(DocumentoBin.class);
        Join<DocumentoBin, Documento> documento = documentoBin.join(DocumentoBin_.documentoList, JoinType.LEFT);
        Join<DocumentoBin, DocumentoTemporario> documentoTemporario = documentoBin.join(DocumentoBin_.documentoTemporarioList, JoinType.LEFT);
        Join<DocumentoBin, Marcador> marcador = documentoBin.join(DocumentoBin_.marcadores, JoinType.INNER);
        Expression<String> codigo = marcador.get(Marcador_.codigo);
        cq.select(codigo).distinct(true);
        cq.where(
            cb.or(
                cb.equal(documento.get(Documento_.pasta).get(Pasta_.id), cb.literal(idPasta)),
                cb.equal(documentoTemporario.get(DocumentoTemporario_.pasta).get(Pasta_.id), cb.literal(idPasta))
            ),
            cb.like(marcador.get(Marcador_.codigo), cb.literal("%" + codigoMarcador + "%"))
        );
        if (codigoMarcadores != null && !codigoMarcadores.isEmpty()) {
            Predicate where = cq.getRestriction();
            cq.where(
                where,
                cb.not(marcador.get(Marcador_.codigo).in(codigoMarcadores))
            );
        }
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public List<Marcador> listMarcadorByProcessoAndCodigo(Integer idProcesso, String codigoMarcador) {
        return listMarcadorByProcessoAndCodigo(idProcesso, codigoMarcador, null);
    }
    
    public List<Marcador> listMarcadorByProcessoAndCodigo(Integer idProcesso, String codigoMarcador, Collection<String> codigoMarcadores) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Marcador> cq = cb.createQuery(Marcador.class);
        Root<DocumentoBin> documentoBin = cq.from(DocumentoBin.class);
        Root<Pasta> pasta = cq.from(Pasta.class); 
        Join<DocumentoBin, Documento> documento = documentoBin.join(DocumentoBin_.documentoList, JoinType.LEFT);
        Join<DocumentoBin, DocumentoTemporario> documentoTemporario = documentoBin.join(DocumentoBin_.documentoTemporarioList, JoinType.LEFT);
        Join<DocumentoBin, Marcador> marcador = documentoBin.join(DocumentoBin_.marcadores, JoinType.INNER);
        cq.select(marcador).distinct(true);
        cq.where(
            cb.or(
                cb.equal(documento.get(Documento_.pasta).get(Pasta_.id), pasta.get(Pasta_.id)),
                cb.equal(documentoTemporario.get(DocumentoTemporario_.pasta).get(Pasta_.id), pasta.get(Pasta_.id))
            ),
            cb.isNotNull(pasta.get(Pasta_.processo)),
            cb.equal(pasta.get(Pasta_.processo).get(Processo_.idProcesso), cb.literal(idProcesso)),
            cb.like(marcador.get(Marcador_.codigo), cb.literal("%" + codigoMarcador + "%"))
        );
        if (codigoMarcadores != null && !codigoMarcadores.isEmpty()) {
            Predicate where = cq.getRestriction();
            cq.where(
                where,
                cb.not(marcador.get(Marcador_.codigo).in(codigoMarcadores))
            );
        }
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<String> listMarcadoresDocumentoProcesso(Integer idProcesso, String codigoMarcador, List<String> codigoMarcadores) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<DocumentoBin> documentoBin = cq.from(DocumentoBin.class);
        Join<DocumentoBin, Documento> documento = documentoBin.join(DocumentoBin_.documentoList, JoinType.INNER);
        Join<Documento, Pasta> pasta = documento.join(Documento_.pasta, JoinType.INNER); 
        Join<DocumentoBin, Marcador> marcador = documentoBin.join(DocumentoBin_.marcadores, JoinType.INNER);
        Expression<String> codigo = marcador.get(Marcador_.codigo);
        cq.select(codigo).distinct(true);
        cq.where(
            cb.equal(pasta.get(Pasta_.processo).get(Processo_.idProcesso), cb.literal(idProcesso)),
            cb.like(marcador.get(Marcador_.codigo), cb.literal("%" + codigoMarcador + "%"))
        );
        if (codigoMarcadores != null && !codigoMarcadores.isEmpty()) {
            Predicate where = cq.getRestriction();
            cq.where(
                where,
                cb.not(marcador.get(Marcador_.codigo).in(codigoMarcadores))
            );
        }
        return getEntityManager().createQuery(cq).getResultList();
    }
    
//    public List<Marcador> listByEntrega(Long idEntrega) {
//        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
//        CriteriaQuery<String> cq = cb.createQuery(String.class);
//        Root<Documento> documento = cq.from(Documento.class);
//        Join<Documento, Marcador> marcador = documento.join(Documento_.marcadores, JoinType.INNER);
//        Expression<String> codigo = marcador.get(Marcador_.codigo);
//        cq.select(codigo);
//        cq.where(
//            cb.equal(marcador.get(Marcador_.processo).get(Processo_.idProcesso), cb.literal(idProcesso))
//        );
//        cq.groupBy(codigo);
//        cq.orderBy(cb.desc(cb.count(documento)));
//        return getEntityManager().createQuery(cq).getResultList();
//    }
    
//    public List<String> listByEntregaAndCodigo(Integer idProcesso, String codigoMarcador) {
//        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
//        CriteriaQuery<String> cq = cb.createQuery(String.class);
//        Root<Documento> documento = cq.from(Documento.class);
//        Join<Documento, Marcador> marcador = documento.join(Documento_.marcadores, JoinType.INNER);
//        Expression<String> codigo = marcador.get(Marcador_.codigo);
//        cq.select(codigo);
//        cq.where(
//            cb.equal(marcador.get(Marcador_.processo).get(Processo_.idProcesso), cb.literal(idProcesso)),
//            cb.like(marcador.get(Marcador_.codigo), cb.literal("%" + codigoMarcador + "%"))
//        );
//        cq.groupBy(codigo);
//        cq.orderBy(cb.desc(cb.count(documento)));
//        return getEntityManager().createQuery(cq).getResultList();
//    }

}
