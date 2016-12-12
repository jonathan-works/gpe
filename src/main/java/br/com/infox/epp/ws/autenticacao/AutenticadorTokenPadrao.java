package br.com.infox.epp.ws.autenticacao;

import java.net.URLConnection;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.ws.exception.UnauthorizedException;
import br.com.infox.epp.ws.messages.WSMessages;
import br.com.infox.ws.request.TokenInjector;

/**
 * Autenticador padrão de serviços REST que utiliza o valor de um token definido por um parâmetro de sistema em um header da requisição HTTP 
 * 
 * @author paulo
 */
@Default
public class AutenticadorTokenPadrao implements AutenticadorToken, TokenInjector {

	public static final String NOME_TOKEN_HEADER_PADRAO = "token";
	private static final String NOME_PARAMETRO_TOKEN = "webserviceToken";
	
	private String nomeHeader = NOME_TOKEN_HEADER_PADRAO;
	private String nomeParametro = NOME_PARAMETRO_TOKEN;
	

	@Inject
	private ParametroManager parametroManager;
	
	public AutenticadorTokenPadrao() {
	}
	
	public AutenticadorTokenPadrao(String nomeParametro, String nomeHeader) {
		super();
		this.nomeParametro = nomeParametro;
		this.nomeHeader = nomeHeader;
	}
	
	private String getTokenParametro() {
		return parametroManager.getValorParametro(nomeParametro);
	}
	
	private void gerarErro(String mensagem) {
		throw new UnauthorizedException(WSMessages.ME_TOKEN_INVALIDO.codigo(),mensagem);						
	}
	
	@Override
	public void validarToken(HttpServletRequest request) throws UnauthorizedException {
		String tokenRequest = getValorToken(request);
		String tokenParametro = getTokenParametro();
		if (tokenRequest == null) {
			gerarErro(String.format("Token de autenticação '%s' não definido no cabeçalho HTTP", nomeHeader));
		}
		else if(tokenParametro == null) {
			gerarErro(String.format("Token de autenticação '%s' não definido no cabeçalho HTTP", nomeHeader));
		}
		else if(!tokenParametro.equals(tokenRequest)) {
			gerarErro(WSMessages.ME_TOKEN_INVALIDO.label());
		}
	}

	@Override
	public String getValorToken(HttpServletRequest request) {
		return ((HttpServletRequest) request).getHeader(nomeHeader);
	}

	@Override
	public void injectAuthenticationToken(URLConnection urlConnection) {
		urlConnection.setRequestProperty(nomeHeader, getTokenParametro());
	}

}
