package br.com.infox.epp.documento;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.AbstractQuery;
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
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento_;
import br.com.infox.epp.entrega.documentos.Entrega;
import br.com.infox.epp.entrega.documentos.Entrega_;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.entity.DocumentoTemporario;
import br.com.infox.epp.processo.documento.entity.DocumentoTemporario_;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.PastaRestricao;
import br.com.infox.epp.processo.documento.entity.PastaRestricao_;
import br.com.infox.epp.processo.documento.entity.Pasta_;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumentoPermissao;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumentoPermissao_;
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
        Predicate documentoPublico = createIsDocumentoPublicoPredicate(cq.subquery(Integer.class), documentoBin);

        Predicate uuidIgual = cb.equal(documentoBin.get(DocumentoBin_.uuid), uuid);
        Predicate suficientementeAssinado = cb.isTrue(documentoBin.get(DocumentoBin_.suficientementeAssinado));
        Predicate naoMinuta = cb.isFalse(documentoBin.get(DocumentoBin_.minuta));
        cq.where(uuidIgual, suficientementeAssinado, naoMinuta,
                cb.not(cb.and(isSigiloso, isCertidaoEntrega.not(), documentoPublico.not())));
        try {
            return getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException e){
            return null;
        }
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
        subquery.select(cb.literal(1)).where(cb.equal(docSigiloso.join(Documento_.documentoBin, JoinType.INNER), documentoBin));
        return cb.exists(subquery);
    }

    public DocumentoBin getTermoAdesaoByUUID(String uid) {
        return getTermoAdesaoByUUID(UUID.fromString(uid));
    }
    
    public DocumentoBin getTermoAdesaoByUUID(UUID uuid) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<DocumentoBin> cq = cb.createQuery(DocumentoBin.class);
        Root<PessoaFisica> pf = cq.from(PessoaFisica.class);
        From<?, DocumentoBin> docBin = pf.join(PessoaFisica_.termoAdesao, JoinType.INNER);
        cq = cq.select(docBin).where(cb.equal(docBin.get(DocumentoBin_.uuid), uuid));
        try {
            return getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    public DocumentoBin getDocumentoByUsuarioPerfilUUID(UUID uuid, UsuarioPerfil usuarioPerfil) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        CriteriaQuery<DocumentoBin> cq = cb.createQuery(DocumentoBin.class);

        Root<DocumentoBin> documentoBin = cq.from(DocumentoBin.class);
        
        Predicate uuidIgual = cb.equal(documentoBin.get(DocumentoBin_.uuid), uuid);
        
        cq = cq.select(documentoBin).where(uuidIgual, cb.or(createFiltrosDocumento(cq, documentoBin, usuarioPerfil), createFiltrosDocumentoTemporario(cq, documentoBin, usuarioPerfil)));
        try {
            return getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    protected Predicate createFiltrosDocumento(AbstractQuery<?> cq, From<?,DocumentoBin> documentoBin,UsuarioPerfil usuarioPerfil) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Predicate filtroSigiloDocumento = cb.and(
                createIsDocumentoSigilosoPredicate(cq.subquery(Integer.class), documentoBin),
                createPossuiPermissaoSigiloPredicate(cq.subquery(Integer.class), documentoBin, usuarioPerfil.getUsuarioLogin()).not()
            ).not();
        Predicate filtroUsuarioPerfilDocumento = createFiltroUsuarioPerfilDocumento(cq.subquery(Integer.class), documentoBin, usuarioPerfil);
        return cb.and(filtroUsuarioPerfilDocumento, filtroSigiloDocumento);
    }
    
    protected Predicate createFiltrosDocumentoTemporario(CriteriaQuery<DocumentoBin> cq, From<?,DocumentoBin> documentoBin,
            UsuarioPerfil usuarioPerfil) {
        return createFiltroUsuarioPerfilDocumentoTemporario(cq.subquery(Integer.class), documentoBin, usuarioPerfil);
    }

    protected Predicate createPossuiPermissaoSigiloPredicate(Subquery<Integer> subquery, From<?, DocumentoBin> documentoBin, UsuarioLogin usuario) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<SigiloDocumentoPermissao> permissaoSigiloDocumento = subquery.from(SigiloDocumentoPermissao.class);
        From<?,SigiloDocumento> sigiloDocumento = permissaoSigiloDocumento.join(SigiloDocumentoPermissao_.sigiloDocumento, JoinType.INNER);
        Predicate documentoIgual = cb.equal(sigiloDocumento.join(SigiloDocumento_.documento).join(Documento_.documentoBin), documentoBin);
        Predicate permissaoAtiva = cb.isTrue(permissaoSigiloDocumento.get(SigiloDocumentoPermissao_.ativo));
        Predicate usuarioIgual = cb.equal(permissaoSigiloDocumento.join(SigiloDocumentoPermissao_.usuario, JoinType.INNER), usuario);
        Predicate possuiPermissao = cb.and(permissaoAtiva, usuarioIgual);
        
        return cb.exists(subquery.select(cb.literal(1)).where(cb.and(documentoIgual, possuiPermissao)));
    }
    
    protected Predicate createFiltroUsuarioPerfilDocumento(Subquery<Integer> subquery, From<?, DocumentoBin> documentoBin, UsuarioPerfil usuarioPerfil){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        
        Root<Documento> documento = subquery.from(Documento.class);
        
        Predicate documentoIgual = cb.equal(documento.join(Documento_.documentoBin, JoinType.INNER), documentoBin);
        Predicate documentoSuficienteMenteAssinado = cb.isTrue(documentoBin.get(DocumentoBin_.suficientementeAssinado));
        Predicate localizacaoIgual = cb.equal(documento.join(Documento_.localizacao, JoinType.INNER), usuarioPerfil.getLocalizacao());
        
        return cb.exists(subquery.select(cb.literal(1)).where(cb.and(documentoIgual, cb.or(documentoSuficienteMenteAssinado, localizacaoIgual))));
    }
    
    protected Predicate createFiltroUsuarioPerfilDocumentoTemporario(Subquery<Integer> subquery, From<?, DocumentoBin> documentoBin, UsuarioPerfil usuarioPerfil){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        
        Root<DocumentoTemporario> documento = subquery.from(DocumentoTemporario.class);
        Predicate documentoIgual = cb.equal(documento.join(DocumentoTemporario_.documentoBin, JoinType.INNER), documentoBin);
        Predicate localizacaoIgual = cb.equal(documento.join(DocumentoTemporario_.localizacao, JoinType.INNER), usuarioPerfil.getLocalizacao());
        
        return cb.exists(subquery.select(cb.literal(1)).where(cb.and(documentoIgual, localizacaoIgual)));
    }
    
}
