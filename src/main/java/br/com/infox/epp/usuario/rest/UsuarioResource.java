package br.com.infox.epp.usuario.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface UsuarioResource {

    @PUT
    Response atualizarUsuario(UsuarioDTO usuarioDTO);

    @GET
    Response getUsuario();

    @DELETE
    Response removerUsuario();
    
    @GET
    @Path("/situacaoTermoAdesao")
    @Produces(MediaType.TEXT_PLAIN)
    Response getSituacaoTermoAdesao();
}
