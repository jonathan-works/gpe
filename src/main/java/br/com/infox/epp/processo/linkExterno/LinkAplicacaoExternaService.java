package br.com.infox.epp.processo.linkExterno;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

@Stateless
public class LinkAplicacaoExternaService {

    @Inject
    private LinkAplicacaoExternaDao dao;

    public LinkAplicacaoExterna findById(Integer id) {
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void salvar(LinkAplicacaoExterna entity) {
        // TODO Auto-generated method stub
        
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remover(LinkAplicacaoExterna entity) {
        // TODO Auto-generated method stub
        
    }
    
    
    
}
