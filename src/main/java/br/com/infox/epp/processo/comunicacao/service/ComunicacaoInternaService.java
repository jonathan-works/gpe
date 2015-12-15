package br.com.infox.epp.processo.comunicacao.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ComunicacaoInternaService {
    
    @Inject
    private EntityManager entityManager;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void gravarDestinatario(DestinatarioModeloComunicacao destinatarioModeloComunicacao) {
        entityManager.persist(destinatarioModeloComunicacao);
        entityManager.flush();
        destinatarioModeloComunicacao.getModeloComunicacao().getDestinatarios().add(destinatarioModeloComunicacao);
    }
    
}
