package br.com.infox.epp.pessoa.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface PessoaJuridicaResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PessoaJuridicaDTO get();

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response edit(PessoaJuridicaDTO pjDTO);

    @DELETE
    public Response delete();
}
