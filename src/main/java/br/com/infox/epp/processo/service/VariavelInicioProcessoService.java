package br.com.infox.epp.processo.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Query;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.VariavelInicioProcesso;
import br.com.infox.epp.processo.form.variable.value.TypedValue;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class VariavelInicioProcessoService extends PersistenceController {
    
    @Inject
    private VariavelInicioProcessoSearch variavelInicioProcessoSearch;
    @Inject @GenericDao
    private Dao<VariavelInicioProcesso, Long> dao;
    
    public Object getVariavel(Processo processo, String name) {
        VariavelInicioProcesso variavel = variavelInicioProcessoSearch.getVariavelInicioProcesso(processo, name);
        if (variavel != null) {
            return variavel.getValue();
        }
        return null;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setVariavel(Processo processo, String name, TypedValue value) {
        VariavelInicioProcesso variavel = variavelInicioProcessoSearch.getVariavelInicioProcesso(processo, name);
        if (variavel == null) {
            variavel = new VariavelInicioProcesso();
            variavel.setName(name);
            variavel.setProcesso(processo);
            variavel.setType(value.getType().getName());
            variavel.setValue(value.getType().convertToStringValue(value));
            dao.persist(variavel);
        } else {
            variavel.setValue(value.getType().convertToStringValue(value));
            dao.update(variavel);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeAll(Processo processo) {
        String jpql = "delete from VariavelInicioProcesso var where var.processo.idProcesso = :idProcesso ";
        Query query = getEntityManager().createQuery(jpql).setParameter("idProcesso", processo.getIdProcesso());
        query.executeUpdate();
    }

}
