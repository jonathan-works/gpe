package br.com.infox.epp.tarefaexterna;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ClassificacaoDocumentoUploadTarefaExternaService {

    @Inject
    @GenericDao
    private Dao<ClassificacaoDocumentoUploadTarefaExterna, Integer> dao;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remover(Integer idClassificacaoDocumento) {
        ClassificacaoDocumentoUploadTarefaExterna classificacaoDocumentoUploadTarefaExterna = dao.getEntityManager().getReference(ClassificacaoDocumentoUploadTarefaExterna.class, idClassificacaoDocumento);
        dao.remove(classificacaoDocumentoUploadTarefaExterna);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void inserir(Integer idClassificacaoDocumento) {
        ClassificacaoDocumentoUploadTarefaExterna classificacaoDocumentoUploadTarefaExterna = new ClassificacaoDocumentoUploadTarefaExterna();
        classificacaoDocumentoUploadTarefaExterna.setId(idClassificacaoDocumento);
        dao.persist(classificacaoDocumentoUploadTarefaExterna);
    }

}
