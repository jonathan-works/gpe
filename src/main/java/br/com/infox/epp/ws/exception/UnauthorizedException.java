package br.com.infox.epp.ws.exception;

import javax.ws.rs.core.Response.Status;

public class UnauthorizedException extends RuntimeException implements ExcecaoServico {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ErroServico erro;
	
	public UnauthorizedException(String codigo, String mensagem) {
		this.erro = new ErroServicoImpl(codigo, mensagem);
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
	

}
