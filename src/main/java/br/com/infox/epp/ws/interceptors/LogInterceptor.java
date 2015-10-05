package br.com.infox.epp.ws.interceptors;

import static br.com.infox.epp.ws.services.MensagensErroService.CODIGO_ERRO_INDEFINIDO;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.webservice.log.entity.LogWebserviceServer;
import br.com.infox.epp.ws.exception.ExcecaoServico.ErroServico;
import br.com.infox.epp.ws.services.LogWebserviceServerManagerNewTransaction;
import br.com.infox.epp.ws.services.MensagensErroService;;

@Log(codigo = "") @Interceptor
/**
 * Interceptador responsável por gravar log no banco
 * @author paulo
 *
 */
public class LogInterceptor {
	
	@Inject
	private ServletRequest request;
	
	@Inject
	private LogWebserviceServerManagerNewTransaction servico;
	
	@Inject
	private MensagensErroService mensagensErroService;
	
	@AroundInvoke
	private Object gerarLog(InvocationContext ctx) throws Exception {
		//TODO: Alterar log de token ao ser definido novo método de autenticação
		String token =((HttpServletRequest) request).getHeader(TokenAuthenticationInterceptor.NOME_TOKEN_HEADER_HTTP);
		if(token == null) {
			token = "";
		}
		
		Log log = ctx.getMethod().getAnnotation(Log.class);
		if(log == null) {
			log = ctx.getTarget().getClass().getAnnotation(Log.class);
		}
		
		List<Object> parametros = new ArrayList<>();
		for(Object parametro : ctx.getParameters()) {
			parametros.add(parametro);
		}
		
		LogWebserviceServer logWsServer = servico.beginLog(log.codigo(), token, parametros.toString());
		if(logWsServer == null) {
			throw new DAOException("Erro ao gerar Log do serviço no banco de dados");
		}
		
		Object retorno = null;
		try {
			retorno = ctx.proceed();
			servico.endLog(logWsServer, retorno == null ? null : retorno.toString());
			return retorno;
		}
		catch(Exception e) {
			ErroServico erro = mensagensErroService.getErro(e);
			String codigoErro = erro.getCodigo();
			if(CODIGO_ERRO_INDEFINIDO.equals(codigoErro)) {
				codigoErro = erro.getMensagem(); 
			}
			servico.endLog(logWsServer, codigoErro);
			throw e;
		}
	}
	

}
