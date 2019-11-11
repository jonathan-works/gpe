package br.com.infox.epp.pessoa.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/pessoaJuridica")
public interface PessoaJuridicaRest {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(@Context UriInfo uriInfo, PessoaJuridicaDTO pjDTO);

    @Path("{cnpj}")
    public PessoaJuridicaResource getPessoaJuridicaResource(@PathParam("cnpj") String cnpj);
}
