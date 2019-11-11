package br.com.infox.epp.entrega;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.LockModeType;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.epp.entrega.modelo.ModeloEntrega;
import br.com.infox.epp.events.FimPrazoModeloEntregaEvent;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class SinalizarAgendaService {
	
	@Inject 
    private Event<FimPrazoModeloEntregaEvent> eventoPrazoExpirado;
	@Inject
    @GenericDao
    private Dao<ModeloEntrega, Integer> modeloEntregaDao;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sinalizarAgendaVencida(ModeloEntrega modeloEntrega) {
        modeloEntrega = modeloEntregaDao.lock(modeloEntrega, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
    	FimPrazoModeloEntregaEvent modeloEntregaEvent = new FimPrazoModeloEntregaEvent();
    	modeloEntrega.setSinalDisparado(Boolean.TRUE);
    	modeloEntregaEvent.setModeloEntrega(modeloEntregaDao.update(modeloEntrega));
    	eventoPrazoExpirado.fire(modeloEntregaEvent);
    }
}

