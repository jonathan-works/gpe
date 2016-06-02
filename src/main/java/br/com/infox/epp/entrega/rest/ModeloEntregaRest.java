package br.com.infox.epp.entrega.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("modeloEntrega")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ModeloEntregaRest {
    
    @GET
    @Path("/categoria")
    List<Categoria> getCategoria(@QueryParam("localizacao") String codigoLocalizacao, @QueryParam("data") String data);
    
    @GET
    @Path("/item/{codigo}")
    ModeloEntregaRest getCategoria(@PathParam("codigo") String codigoItemPai, @QueryParam("localizacao") String codigoLocalizacao, @QueryParam("data") String data); 
    
    
}
