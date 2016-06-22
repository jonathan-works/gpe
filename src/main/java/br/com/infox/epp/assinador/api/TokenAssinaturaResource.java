package br.com.infox.epp.assinador.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.infox.epp.rest.RestException;

public interface TokenAssinaturaResource {

	@Path("/")
	@DELETE
	public void apagar();
	
	/**
	 * Indica que ocorreu um erro no processamento de assinatura do documento
	 */
	@Path("/erro")
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	public void erroProcessamento(RestException erro);
	
	/**
	 * Indica que o processamento foi finalizado com sucesso
	 */
	@Path("/processado")
	@POST
	public void processamentoFinalizado();
	
	@Path("/cancelado")
	@POST
	public void processamentoCancelado();
	
	@Path("/documento")
	@Produces(MediaType.APPLICATION_JSON)
	public DocumentoRest getDocumentoRest();
	
	@Path("/assinatura")
	public AssinaturaRest getAssinaturaRest();
	
	@GET
	@Path("/status")
	public String getStatus();
}
