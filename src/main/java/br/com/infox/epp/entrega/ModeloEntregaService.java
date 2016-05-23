package br.com.infox.epp.entrega;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.epp.entrega.modelo.ClassificacaoDocumentoEntrega;
import br.com.infox.epp.entrega.modelo.ModeloEntrega;
import br.com.infox.epp.entrega.modelo.TipoResponsavelEntrega;

@Stateless
public class ModeloEntregaService implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Inject
    private Dao<ModeloEntrega, Integer> modeloEntregaDao;
    @Inject
    private Dao<ClassificacaoDocumentoEntrega, Integer> classificacaoDocumentoEntregaDao;
    @Inject
    private Dao<TipoResponsavelEntrega, Integer> tipoResponsavelEntregaDao;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ModeloEntrega salvarModeloEntrega(ModeloEntrega modeloEntrega){
        if (modeloEntrega.getId() == null) {
            modeloEntregaDao.persist(modeloEntrega);
        } else {
            modeloEntrega = modeloEntregaDao.update(modeloEntrega);
        }
        return modeloEntrega;
    }

}
