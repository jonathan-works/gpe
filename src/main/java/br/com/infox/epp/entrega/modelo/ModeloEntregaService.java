package br.com.infox.epp.entrega.modelo;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.epp.events.FimPrazoModeloEntregaEvent;

@Stateless
public class ModeloEntregaService {

	@Inject
	@GenericDao
    private Dao<ModeloEntrega, Integer> modeloEntregadao;
    @Inject 
    private ModeloEntregaSearch modeloEntregaSearch;
    @Inject 
    private Event<FimPrazoModeloEntregaEvent> eventoPrazoExpirado;
    
	public void remove(ModeloEntrega object) {
		modeloEntregadao.remove(object);
	}

	public void persist(ModeloEntrega object) {
		modeloEntregadao.persist(object);
	}

	public void update(ModeloEntrega object) {
		modeloEntregadao.update(object);
	}

	public ModeloEntrega find(Integer id) {
		return modeloEntregadao.findById(id);
	}

	public List<ModeloEntrega> findAll() {
		return modeloEntregadao.findAll();
	}

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void sinalizarAgendaVencida(ModeloEntrega modeloEntrega) {
    	modeloEntregadao.lock(modeloEntrega, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        FimPrazoModeloEntregaEvent modeloEntregaEvent = new FimPrazoModeloEntregaEvent();
        modeloEntregaEvent.setModeloEntrega(modeloEntrega);
        eventoPrazoExpirado.fire(modeloEntregaEvent);
        modeloEntrega.setSinalDisparado(Boolean.TRUE);
        modeloEntregadao.update(modeloEntrega);
    }

    public void sinalizarAgendasVencidas() {
        sinalizarAgendasVencidas(new Date());
    }

    public void sinalizarAgendasVencidas(Date data) {
        try {
            for (ModeloEntrega modeloEntrega : modeloEntregaSearch.getAgendasvencidas(data)) {
                sinalizarAgendaVencida(modeloEntrega);
            }
        } catch (OptimisticLockException e) {
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", "Serviço já executado, ou em execução");
            throw new WebApplicationException(Response.status(400).entity(gson.toJson(jsonObject)).build());
        }
    }
}
