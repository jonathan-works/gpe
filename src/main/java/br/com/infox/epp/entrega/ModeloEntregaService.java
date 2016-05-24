package br.com.infox.epp.entrega;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.modelo.ModeloEntrega;

@Stateless
public class ModeloEntregaService implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Inject
    @GenericDao
    private Dao<ModeloEntrega, Integer> modeloEntregaDao;
    @Inject
    @GenericDao
    private Dao<CategoriaEntregaItem, Integer> categoriaEntregaItemDao;
    @Inject
    private CategoriaEntregaItemSearch categoriaEntregaItemSearch;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ModeloEntrega salvarModeloEntrega(ModeloEntrega modeloEntrega){
        if (modeloEntrega.getId() == null) {
            modeloEntregaDao.persist(modeloEntrega);
        } else {
            modeloEntrega = modeloEntregaDao.update(modeloEntrega);
        }
        return modeloEntrega;
    }
    
    
    public CategoriaEntregaItem salvarRestricoesLocalizacao(String codigoItem, List<Localizacao> localizacoes){
        CategoriaEntregaItem categoriaEntregaItem = categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigoItem);
        categoriaEntregaItem.setRestricoes(localizacoes);
        return salvarRestricoesLocalizacao(categoriaEntregaItem);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private CategoriaEntregaItem salvarRestricoesLocalizacao(CategoriaEntregaItem categoriaEntregaItem){
        return categoriaEntregaItemDao.update(categoriaEntregaItem);
    }

}
