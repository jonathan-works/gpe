package br.com.infox.epp.ws.interceptors;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.webservice.log.entity.LogWebserviceServer;
import br.com.infox.epp.webservice.log.manager.LogWebserviceServerManager;

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
	private LogWebserviceServerManager servico;
	
	@AroundInvoke
	private Object gerarLog(InvocationContext ctx) throws Exception {
		//TODO: Alterar log de token ao ser definido novo método de autenticação
		String token =((HttpServletRequest) request).getHeader("token");
		//FIXME: implementar anotação para definir qual bean deve ser logado
		Object bean = ctx.getParameters()[0];
		Log log = ctx.getMethod().getAnnotation(Log.class);
		if(log == null) {
			log = ctx.getTarget().getClass().getAnnotation(Log.class);
		}
		LogWebserviceServer logWsServer = servico.beginLog(log.codigo(), token, bean.toString());
		if(logWsServer == null) {
			throw new DAOException("Erro ao gerar Log do serviço no banco de dados");
		}
		Object retorno = null;
		try {
			retorno = ctx.proceed();
			return retorno;
		} finally {
			servico.endLog(logWsServer, retorno == null ? null : retorno.toString());
		}
	}
	

}
