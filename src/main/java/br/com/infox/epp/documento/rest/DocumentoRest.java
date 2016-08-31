package br.com.infox.epp.documento.rest;

import java.util.UUID;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("documento")
public interface DocumentoRest {
    
    @Path("/{uuid}")
    public DocumentoResource getBinaryData(@PathParam("uuid") UUID uuid);
    
    
}
