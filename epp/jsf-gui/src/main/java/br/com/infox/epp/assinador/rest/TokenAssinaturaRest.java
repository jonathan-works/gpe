package br.com.infox.epp.assinador.rest;

import javax.ws.rs.Path;

import br.com.infox.assinador.rest.api.TokenAssinaturaBaseResource;

@Path("tokenAssinatura")
public interface TokenAssinaturaRest {

    @Path("/")
    TokenAssinaturaBaseResource getBaseResource();

}