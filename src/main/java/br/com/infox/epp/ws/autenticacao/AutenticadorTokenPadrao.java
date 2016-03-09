package br.com.infox.epp.ws.autenticacao;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.ws.exception.UnauthorizedException;
import br.com.infox.epp.ws.messages.WSMessages;

@Default
public class AutenticadorTokenPadrao implements AutenticadorToken {

	public static final String NOME_TOKEN_HEADER_PADRAO = "token";
	private static final String NOME_PARAMETRO_TOKEN = "webserviceToken";
	
	private String nomeHeader = NOME_TOKEN_HEADER_PADRAO;
	

	@Inject
	private ParametroManager parametroManager;
	
	public AutenticadorTokenPadrao() {
	}
	
	public AutenticadorTokenPadrao(String nomeHeader) {
		super();
		this.nomeHeader = nomeHeader;
	}
	
	@Override
	public void validarToken(HttpServletRequest request) throws UnauthorizedException {
		String tokenRequest = getValorToken(request);
		String tokenParametro = parametroManager.getValorParametro(NOME_PARAMETRO_TOKEN);
		if (tokenParametro == null || !tokenParametro.equals(tokenRequest)) {
			throw new UnauthorizedException(WSMessages.ME_TOKEN_INVALIDO.codigo(),
					WSMessages.ME_TOKEN_INVALIDO.label());				
		}
	}

	@Override
	public String getValorToken(HttpServletRequest request) {
		return ((HttpServletRequest) request).getHeader(nomeHeader);
	}

}
