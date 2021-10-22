package br.com.infox.epp.tarefaexterna;

import java.util.ArrayList;
import java.util.Calendar;
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
import br.com.infox.epp.loglab.contribuinte.type.TipoParticipanteEnum;
import br.com.infox.epp.loglab.service.ParticipanteProcessoLoglabService;
import br.com.infox.epp.loglab.vo.AnonimoVO;
import br.com.infox.epp.loglab.vo.ParticipanteProcessoVO;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.tarefaexterna.view.CadastroTarefaExternaVO;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DocumentoUploadTarefaExternaService {

    @Inject
    @GenericDao
    private Dao<DocumentoUploadTarefaExterna, Integer> dao;
    @Inject
    private DocumentoManager documentoManager;
    @Inject
    private ParticipanteProcessoLoglabService participanteProcessoLoglabService;
    @Inject
    private PastaManager pastaManager;
    @Inject
    private DocumentoBinManager documentoBinManager;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remover(List<Long> listaId) {
        if(listaId == null || listaId.isEmpty()) {
            return;
        }
        dao.getEntityManager()
            .createQuery("delete from DocumentoUploadTarefaExterna o where o.id in (:lista)")
            .setParameter("lista", listaId)
            .executeUpdate();
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removerTodosCascade() {
        removerByDocBinCascade(getListDocBinByFiltros(null, null));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removerCascade(List<Long> listaId) {
        removerByDocBinCascade(getListDocBinByFiltros(listaId, null));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removerCascade(UUID uuid) {
        removerByDocBinCascade(getListDocBinByFiltros(null, uuid));
    }

    private void removerByDocBinCascade(List<Integer> listaDocBin) {
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

    private List<DocumentoUploadTarefaExterna> getListDocumentoUploadTarefaExternaByFiltros(UUID uuid) {
        EntityManager em = dao.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DocumentoUploadTarefaExterna> query = cb.createQuery(DocumentoUploadTarefaExterna.class);
        Root<DocumentoUploadTarefaExterna> documentoUploadTarefaExterna = query.from(DocumentoUploadTarefaExterna.class);
        query.where(cb.equal(documentoUploadTarefaExterna.get(DocumentoUploadTarefaExterna_.uuidTarefaExterna), uuid));
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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persistirNoProcesso(String codigoTipoParticipante, UUID uuid, CadastroTarefaExternaVO ctVO, Processo processo) {
        if("A".equals(ctVO.getCodTipoManifestacao())) {
            AnonimoVO anonimoVO = new AnonimoVO();
            anonimoVO.setEmail(ctVO.getEmail());
            anonimoVO.setTipoParticipante(TipoParticipanteEnum.ANON);
            ParticipanteProcessoVO participanteProcessoVO = new ParticipanteProcessoVO();
            participanteProcessoVO.setTipoPessoa(TipoPessoaEnum.A);
            participanteProcessoVO.setCdTipoParte(codigoTipoParticipante);
            participanteProcessoVO.setDataInicio(Calendar.getInstance().getTime());
            participanteProcessoVO.setIdProcesso(processo.getIdProcesso());
            participanteProcessoVO.setAnonimoVO(anonimoVO);
            participanteProcessoLoglabService.gravarParticipanteProcesso(participanteProcessoVO);
        }

        List<Long> idsParaRemover = new ArrayList<>();
        for (DocumentoUploadTarefaExterna docUploadTarefaExterna : getListDocumentoUploadTarefaExternaByFiltros(uuid)) {
            Pasta pasta = null;
            if(docUploadTarefaExterna.getPasta() != null) {
                pasta = pastaManager.getByCodigoAndProcesso(docUploadTarefaExterna.getPasta().getCodigo(), processo);
            }
            documentoManager.createDocumento(
                processo,
                docUploadTarefaExterna.getDescricao(),
                docUploadTarefaExterna.getDocumentoBin(),
                docUploadTarefaExterna.getClassificacaoDocumento(),
                pasta
            );
            idsParaRemover.add(docUploadTarefaExterna.getId());
        }
        remover(idsParaRemover);
    }

}
