package br.com.infox.epp.entrega;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.epp.entrega.modelo.ModeloEntrega;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ModeloEntregaDao extends Dao<ModeloEntrega, Integer>{

    public ModeloEntregaDao() {
        super(ModeloEntrega.class);
    }

}
