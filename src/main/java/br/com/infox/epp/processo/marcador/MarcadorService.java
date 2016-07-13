package br.com.infox.epp.processo.marcador;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class MarcadorService {
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void gravar(Marcador marcador) {
        
    }

}
