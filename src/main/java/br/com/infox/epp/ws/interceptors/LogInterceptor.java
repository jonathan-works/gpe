package br.com.infox.epp.ws.interceptors;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.Component;


import br.com.infox.epp.webservice.log.entity.LogWebserviceServer;
import br.com.infox.epp.webservice.log.manager.LogWebserviceServerManager;

@Log(codigo = "", mensagem = "") @Interceptor
public class LogInterceptor {
	
	@Inject
	private HttpServletRequest request;
	
	@AroundInvoke
	private Object gerarLog(InvocationContext ctx) throws Exception {
		LogWebserviceServerManager logWebserviceServerManager = (LogWebserviceServerManager) Component.getInstance(LogWebserviceServerManager.class);
		
		//TODO: Alterar log de token ao ser definido novo método de autenticação
		String token = request.getHeader("token");
		//FIXME: implementar anotação para definir qual bean deve ser logado
		Object bean = ctx.getParameters()[0];
		Log log = ctx.getMethod().getAnnotation(Log.class);
		LogWebserviceServer logWsServer = logWebserviceServerManager.beginLog(log.codigo(), token, bean.toString());
		Object retorno = null;
		try {
			retorno = ctx.proceed();
			return retorno;
		} finally {
			logWebserviceServerManager.endLog(logWsServer, retorno == null ? null : retorno.toString());
		}
	}
	

}
