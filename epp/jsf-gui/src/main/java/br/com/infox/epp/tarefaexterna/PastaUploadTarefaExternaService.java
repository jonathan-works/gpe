package br.com.infox.epp.tarefaexterna;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PastaUploadTarefaExternaService {

    @Inject
    @GenericDao
    private Dao<PastaUploadTarefaExterna, Integer> dao;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remover(Integer id) {
        PastaUploadTarefaExterna pasta = dao.getEntityManager().getReference(PastaUploadTarefaExterna.class, id);
        dao.remove(pasta);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void inserir(String codigo, String nome) {
        PastaUploadTarefaExterna pasta = new PastaUploadTarefaExterna();
        pasta.setCodigo(codigo);
        pasta.setNome(nome);
        dao.persist(pasta);
    }

}
