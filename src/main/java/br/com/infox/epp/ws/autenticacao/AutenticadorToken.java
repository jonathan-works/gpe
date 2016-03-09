package br.com.infox.epp.ws.autenticacao;

import javax.servlet.http.HttpServletRequest;

import br.com.infox.epp.ws.exception.UnauthorizedException;

public interface AutenticadorToken {
	public String getValorToken(HttpServletRequest request);
	public void validarToken(HttpServletRequest request) throws UnauthorizedException;
}
