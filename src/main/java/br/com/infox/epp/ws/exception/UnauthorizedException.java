package br.com.infox.epp.ws.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

public class UnauthorizedException extends RuntimeException implements ExcecaoServico, MediaTypeSource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ErroServico erro;
	private String mediaType;
	
	
	public UnauthorizedException(String codigo, String mensagem) {
		this(codigo, mensagem, MediaType.APPLICATION_JSON_TYPE);
	}
	
	public UnauthorizedException(String codigo, String mensagem, MediaType mediaType) {
		super(mensagem);
		this.erro = new ErroServicoImpl(codigo, mensagem);
		this.mediaType = mediaType.getType();
	}

	@Override
	public ErroServico getErro() {
		return erro;
	}
	@Override
	public int getStatus() {
		return Status.UNAUTHORIZED.getStatusCode();
	}

	@Override
	public String getCode() {
		return getErro().getCode();
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	@Override
	public String getMediatType() {
		return this.mediaType;
	}
	

}
