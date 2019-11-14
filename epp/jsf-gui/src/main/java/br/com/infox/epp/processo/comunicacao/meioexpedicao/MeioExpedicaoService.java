package br.com.infox.epp.processo.comunicacao.meioexpedicao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class MeioExpedicaoService {

    @Inject
    @GenericDao
    private Dao<MeioExpedicao, Long> meioExpedicaoDao;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persist(MeioExpedicao meioExpedicao) {
        meioExpedicaoDao.persist(meioExpedicao);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public MeioExpedicao update(MeioExpedicao meioExpedicao) {
        return meioExpedicaoDao.update(meioExpedicao);
    }
}
