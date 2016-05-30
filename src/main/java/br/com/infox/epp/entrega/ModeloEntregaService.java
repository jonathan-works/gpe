package br.com.infox.epp.entrega;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
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
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.modelo.ModeloEntrega;
import br.com.infox.epp.events.FimPrazoModeloEntregaEvent;

@Stateless
public class ModeloEntregaService {
	
    @Inject
    @GenericDao
    private Dao<ModeloEntrega, Integer> modeloEntregaDao;
    @Inject
    @GenericDao
    private Dao<CategoriaEntregaItem, Integer> categoriaEntregaItemDao;
    @Inject
    private CategoriaEntregaItemSearch categoriaEntregaItemSearch;
    @Inject 
    private ModeloEntregaSearch modeloEntregaSearch;
    @Inject 
    private Event<FimPrazoModeloEntregaEvent> eventoPrazoExpirado;
    
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
    
    private CategoriaEntregaItem salvarRestricoesLocalizacao(CategoriaEntregaItem categoriaEntregaItem){
        return categoriaEntregaItemDao.update(categoriaEntregaItem);
    }
    
    public void sinalizarAgendaVencida(ModeloEntrega modeloEntrega) {
    	modeloEntregaDao.lock(modeloEntrega, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        FimPrazoModeloEntregaEvent modeloEntregaEvent = new FimPrazoModeloEntregaEvent();
        modeloEntregaEvent.setModeloEntrega(modeloEntrega);
        eventoPrazoExpirado.fire(modeloEntregaEvent);
        modeloEntrega.setSinalDisparado(Boolean.TRUE);
        modeloEntregaDao.update(modeloEntrega);
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
