package br.com.infox.epp.relacionamentoprocessos;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.manager.ProcessoManager;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class RelacionamentoManager {

    public static final String NAME = "relacionamentoManager";
    
    @Inject
    private RelacionamentoDAO relacionamentoDAO;
    
    @Inject
    private ProcessoManager processoManager;

    public EntityManager getEntityManager() {
    	return EntityManagerProducer.getEntityManager();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Relacionamento persist(final Relacionamento o) throws DAOException {
        getEntityManager().persist(o);
        getEntityManager().flush();
        try {
            return getEntityManager().find(Relacionamento.class, o.getIdRelacionamento());
        } catch (IllegalArgumentException e) {
            throw new DAOException(e);
        }
    }
    
	public void remove(Integer idRelacionamento) {
		Relacionamento relacionamento = relacionamentoDAO.find(idRelacionamento);
		relacionamentoDAO.remove(relacionamento);
	}

    
	public void remove(Integer idProcesso, Integer idProcessoInternoRelacionado) {
		Processo processo = processoManager.find(idProcesso);
		Processo processoInternoRelacionado = processoManager.find(idProcessoInternoRelacionado);
		relacionamentoDAO.remove(processo, processoInternoRelacionado);
	}

	public void remove(Integer idProcesso, String numeroProcessoExterno) {
		Processo processo = processoManager.find(idProcesso);
		relacionamentoDAO.remove(processo, numeroProcessoExterno);
	}    
}
