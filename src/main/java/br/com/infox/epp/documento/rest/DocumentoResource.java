package br.com.infox.epp.documento.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.infox.epp.processo.documento.AssinaturaDto;

public interface DocumentoResource {
    
    @GET
    @Path("/download")
    public Response getBinaryData();
    
    @GET
    @Path("/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] getBinario();
    
    @GET
    @Path("/assinatura")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<AssinaturaDto> getAssinaturas();
    
}
