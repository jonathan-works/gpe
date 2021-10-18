package br.com.infox.epp.tarefaexterna.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento_;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.tarefaexterna.DocumentoUploadTarefaExterna;
import br.com.infox.epp.tarefaexterna.DocumentoUploadTarefaExterna_;
import br.com.infox.epp.tarefaexterna.PastaUploadTarefaExterna;
import br.com.infox.epp.tarefaexterna.PastaUploadTarefaExterna_;
import br.com.infox.seam.path.PathResolver;
import lombok.Getter;
import lombok.Setter;

public class CadastroTarefaDocumentoDataModel extends LazyDataModel<DocumentoVO> {

    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private UUID uuidTarefaExterna;

    @Getter @Setter
    private List<DocumentoVO> selecionados = new ArrayList<>();

    @Getter @Setter
    private DataTable dataTable;

    @Inject
    private PathResolver pathResolver;

    @Getter
    private String jsOpenPopup;

    @PostConstruct
    private void init() {
        this.jsOpenPopup = "infox.openPopUp('_blank', '" + pathResolver.getContextPath() + "/downloadDocumento.seam')";
    }

    @Override
    public List<DocumentoVO> load(int first, int pageSize, String sortField
            ,SortOrder sortOrder,
            Map<String, Object> filters) {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();


        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        baseQuery(countQuery);
        setRowCount(em.createQuery(countQuery).getSingleResult().intValue());

        CriteriaQuery<DocumentoVO> query = cb.createQuery(DocumentoVO.class);
        baseQuery(query);
        return em.createQuery(query)
            .setMaxResults(pageSize)
            .setFirstResult(first)
            .getResultList();

    }


    private void baseQuery(CriteriaQuery<?> query) {
        baseQuery(query, null);
    }

    @SuppressWarnings("unchecked")
    private void baseQuery(CriteriaQuery<?> query, Long id) {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        Root<DocumentoUploadTarefaExterna> documentoUploadTarefaExterna = query.from(DocumentoUploadTarefaExterna.class);
        Join<DocumentoUploadTarefaExterna, DocumentoBin> documentoBin = documentoUploadTarefaExterna.join(DocumentoUploadTarefaExterna_.documentoBin);
        Join<DocumentoUploadTarefaExterna, ClassificacaoDocumento> classificacaoDocumento = documentoUploadTarefaExterna.join(DocumentoUploadTarefaExterna_.classificacaoDocumento);
        Join<DocumentoUploadTarefaExterna, PastaUploadTarefaExterna> pasta = documentoUploadTarefaExterna.join(DocumentoUploadTarefaExterna_.pasta, JoinType.LEFT);

        if(query.getResultType().equals(Long.class)) {
            ((CriteriaQuery<Long>)query).select(cb.count(documentoUploadTarefaExterna));
        } else {
            ((CriteriaQuery<DocumentoVO>)query).select(
                cb.construct(DocumentoVO.class,
                    documentoUploadTarefaExterna.get(DocumentoUploadTarefaExterna_.id),
                    documentoBin.get(DocumentoBin_.id),
                    documentoUploadTarefaExterna.get(DocumentoUploadTarefaExterna_.descricao),
                    pasta.get(PastaUploadTarefaExterna_.nome),
                    classificacaoDocumento.get(ClassificacaoDocumento_.descricao),
                    documentoUploadTarefaExterna.get(DocumentoUploadTarefaExterna_.dataInclusao)
                )
            );

        }

        query.where(cb.and()
            , cb.equal(documentoUploadTarefaExterna.get(DocumentoUploadTarefaExterna_.uuidTarefaExterna), uuidTarefaExterna)
        );

        if(id != null) {
            query.where(query.getRestriction(), cb.equal(documentoUploadTarefaExterna, id));
        }
    }




    @Override
    @SuppressWarnings("unchecked")
    public DocumentoVO getRowData(String rowKey) {
        Object wrappedData = getWrappedData();
        if(wrappedData != null) {
            for(DocumentoVO doc : (List<DocumentoVO>)wrappedData) {
                if(doc.getId().equals(Long.valueOf(rowKey))) {
                    return doc;
                }
            }
        }
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DocumentoVO> query = cb.createQuery(DocumentoVO.class);
        baseQuery(query, Long.valueOf(rowKey));
        try {
            return em.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Object getRowKey(DocumentoVO object) {
        return object.getId();
    }

}