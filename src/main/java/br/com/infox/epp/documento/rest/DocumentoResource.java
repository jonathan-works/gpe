package br.com.infox.epp.documento.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public interface DocumentoResource {
    
    @GET
    @Path("/download")
    public Response getBinaryData();
    
    
}
