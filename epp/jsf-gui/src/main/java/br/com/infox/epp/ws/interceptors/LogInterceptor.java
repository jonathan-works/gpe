package br.com.infox.epp.ws.interceptors;

import static br.com.infox.epp.ws.services.MensagensErroService.CODIGO_ERRO_INDEFINIDO;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.webservice.log.entity.LogWebserviceServer;
import br.com.infox.epp.ws.exception.ErroServico;
import br.com.infox.epp.ws.services.LogWebserviceServerManagerNewTransaction;
import br.com.infox.epp.ws.services.MensagensErroService;;

@Log 
@Interceptor
/**
 * Interceptador responsável por gravar log no banco
 * @author paulo
 *
 */
public class LogInterceptor {
	
	/**
	 * Nome do parâmetro token no {@link InvocationContext} que será utilizado no log
	 */
	public static final String NOME_PARAMETRO_TOKEN = "token";
	
	@Inject
	private LogWebserviceServerManagerNewTransaction servico;
	
	@Inject
	private MensagensErroService mensagensErroService;
	@Inject
	private HttpServletRequest request;
	
	private String getToken(InvocationContext ctx) {
		String token = (String)ctx.getContextData().get(NOME_PARAMETRO_TOKEN);
		return token == null ? "" : token;
	}
	
	@AroundInvoke
	private Object gerarLog(InvocationContext ctx) throws Exception {
		//TODO: Alterar log de token ao ser definido novo método de autenticação
		String token = getToken(ctx);
		
	
		
		List<Object> parametros = new ArrayList<>();
		for(Object parametro : ctx.getParameters()) {
			parametros.add(parametro);
		}
		
		LogWebserviceServer logWsServer = servico.beginLog(getWebServiceName(ctx), token, parametros.toString() + getSessionInfo());
		if(logWsServer == null) {
			throw new DAOException("Erro ao gerar Log do serviço no banco de dados");
		}
		
		Object retorno = null;
		try {
			retorno = ctx.proceed();
			token = getToken(ctx);
			logWsServer.setToken(token);
			servico.endLog(logWsServer, getResponseText(retorno));
			return retorno;
		}
		catch(Exception e) {
			ErroServico erro = mensagensErroService.getErro(e);
			String codigoErro = erro.getCode();
			if(CODIGO_ERRO_INDEFINIDO.equals(codigoErro)) {
				codigoErro = erro.getMessage(); 
			}
			token = getToken(ctx);
			logWsServer.setToken(token);
			servico.endLog(logWsServer, getResponseText(erro) + " " + codigoErro);
			throw e;
		}
		
	}
	
	private String getWebServiceName(InvocationContext ctx){
		Log log = ctx.getMethod().getAnnotation(Log.class);
		if(log == null) {
			log = ctx.getTarget().getClass().getAnnotation(Log.class);
		}
		
		if(!log.codigo().trim().isEmpty())
			return log.codigo();
		else {
			return ctx.getTarget().getClass().getName();	
		}
	}
	
	private String getResponseText(Object response){
		if(response == null)
			return null;
		
		if(Response.class.isAssignableFrom(response.getClass()))
			if(((Response)response).getEntity() != null)
				return ((Response)response).getEntity().toString();
			else{
				return "HTTP Status Code: " + Integer.toString(((Response)response).getStatus());
			}
		else
			return response.toString();
	}
	
	private String getSessionInfo() {
		String separator = " \n ";
		StringBuffer sessionContent = new StringBuffer( separator + "Session Content: ");
		sessionContent.append(separator);
		HttpSession session = request != null ? request.getSession() : null;
		if(session != null){	
			sessionContent.append("Session ID: " + session.getId() + " \n ") ;
			sessionContent.append(separator);
			sessionContent.append("Session Creation Time: " + session.getCreationTime());
			sessionContent.append(separator);
			sessionContent.append("Session Max Inactive Interval: " + session.getMaxInactiveInterval());
			sessionContent.append(separator);
			sessionContent.append("Session Last Accessed Time: " + session.getLastAccessedTime());
			sessionContent.append(separator);
			Enumeration<String> attrs =  session.getAttributeNames();
			while(attrs.hasMoreElements()) {
			 String nextElement = attrs.nextElement();
			 sessionContent.append(nextElement + ": " + session.getAttribute(nextElement));
			 sessionContent.append(separator);
			}
			return sessionContent.toString();
		}
		return separator + "No Session created.";
	}
	

}
