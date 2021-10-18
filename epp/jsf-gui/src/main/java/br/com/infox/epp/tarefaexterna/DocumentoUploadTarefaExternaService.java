package br.com.infox.epp.tarefaexterna;

import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DocumentoUploadTarefaExternaService {

    @Inject
    @GenericDao
    private Dao<DocumentoUploadTarefaExterna, Integer> dao;
    @Inject
    private DocumentoBinManager documentoBinManager;
    @Inject
    private DocumentoBinarioManager documentoBinarioManager;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remover(Long id) {
        DocumentoUploadTarefaExterna pasta = dao.getEntityManager().getReference(DocumentoUploadTarefaExterna.class, id);
        dao.remove(pasta);
        documentoBinManager.remove(pasta.getDocumentoBin());
        documentoBinarioManager.remove(pasta.getDocumentoBin().getId());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remover(List<Long> listaId) {
        removerByDocBin(getListDocBinByFiltros(listaId, null));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remover(UUID uuid) {
        removerByDocBin(getListDocBinByFiltros(null, uuid));
    }

    private void removerByDocBin(List<Integer> listaDocBin) {
        if(listaDocBin == null || listaDocBin.isEmpty()) {
            return;
        }
        dao.getEntityManager()
            .createQuery("delete from DocumentoUploadTarefaExterna o where o.documentoBin.id in (:lista)")
            .setParameter("lista", listaDocBin)
            .executeUpdate();

        documentoBinManager.remove(listaDocBin);
    }

    private List<Integer> getListDocBinByFiltros(List<Long> listaId, UUID uuid) {
        EntityManager em = dao.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        Root<DocumentoUploadTarefaExterna> documentoUploadTarefaExterna = query.from(DocumentoUploadTarefaExterna.class);
        Join<DocumentoUploadTarefaExterna, DocumentoBin> documentoBin = documentoUploadTarefaExterna.join(DocumentoUploadTarefaExterna_.documentoBin);
        query.select(documentoBin.get(DocumentoBin_.id));
        if(listaId != null && !listaId.isEmpty()) {
            query.where(documentoUploadTarefaExterna.in(listaId));
        } else if(uuid != null) {
            query.where(cb.equal(documentoUploadTarefaExterna.get(DocumentoUploadTarefaExterna_.uuidTarefaExterna), uuid));
        }
        return em.createQuery(query).getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void inserir(CadastroTarefaExternaDocumentoDTO dto) {
        ClassificacaoDocumento classificacaoDocumento = dao.getEntityManager().getReference(ClassificacaoDocumento.class, dto.getIdClassificacaoDocumento());
        for (DocumentoBin documentoBin : dto.getBins()) {
            documentoBinManager.createProcessoDocumentoBin(documentoBin);
            DocumentoUploadTarefaExterna doc = new DocumentoUploadTarefaExterna();
            doc.setDescricao(dto.getDescricao());
            doc.setClassificacaoDocumento(classificacaoDocumento);
            doc.setDocumentoBin(documentoBin);
            if(dto.getIdPasta() != null) {
                doc.setPasta(dao.getEntityManager().getReference(PastaUploadTarefaExterna.class, dto.getIdPasta()));
            }
            doc.setDataInclusao(dto.getDataInclusao());
            doc.setUuidTarefaExterna(dto.getUuidTarefaExterna());
            dao.persist(doc);
        }
    }

}
