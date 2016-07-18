package br.com.infox.epp.entrega;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
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
import br.com.infox.epp.entrega.modelo.ModeloEntregaItem;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Stateless
public class ModeloEntregaService {
	
	private static final LogProvider LOG = Logging.getLogProvider(ModeloEntregaService.class);
	
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
    private SinalizarAgendaService sinalizarAgendaService;
    @Inject
    @GenericDao
    private Dao<ModeloEntregaItem, Long> modeloEntregaItemDao;
    
    public ModeloEntrega salvarModeloEntrega(ModeloEntrega modeloEntrega){
        if (modeloEntrega.getId() == null) {
            modeloEntregaDao.persist(modeloEntrega);
            for (ModeloEntregaItem item : modeloEntrega.getItensModelo()) {
            	modeloEntregaItemDao.persist(item);
            }
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
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void sinalizarAgendasVencidas() {
        sinalizarAgendasVencidas(new Date());
    }
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void sinalizarAgendasVencidas(Date data) {
        try {
            for (ModeloEntrega modeloEntrega : modeloEntregaSearch.getAgendasvencidas(data)) {
                try {
                	sinalizarAgendaService.sinalizarAgendaVencida(modeloEntrega);
                } catch (Exception e) {
                	if (e instanceof OptimisticLockException) {
                		throw e;
                	} else {
                		LOG.error("Erro ao sinalizar agenda do modelo de entrega " + modeloEntrega.getId(), e);
                	}
				}
            }
        } catch (OptimisticLockException e) {
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", "Serviço já executado, ou em execução");
            throw new WebApplicationException(Response.status(400).entity(gson.toJson(jsonObject)).build());
        }
    }

}
