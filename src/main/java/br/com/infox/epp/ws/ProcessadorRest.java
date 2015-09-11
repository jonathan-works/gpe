package br.com.infox.epp.ws;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.seam.contexts.Contexts;

import br.com.infox.epp.ws.interceptors.InjectSeamContext;
import br.com.infox.epp.ws.messages.WSMessages;

/**
 * Define um processador padrão para serviços REST, responsável por gravar LOG, injetar o contexto do Seam em serviços REST contendo a anotação {@link InjectSeamContext} e tratar exceções
 * @author paulo
 *
 */
//@ServerInterceptor
public class ProcessadorRest implements PreProcessInterceptor, PostProcessInterceptor {
	
	/**
	 * Procura por anotações em métodos, classes e pacotes (nessa ordem) 
	 * @return Anotação mais específica encontrada ou nulo caso ela não tenha sido definida
	 */
	private <T extends Annotation> T getAnotacao(Method metodo, Class<T> classeAnotacao)
	{
		T anotacao =  metodo.getAnnotation(classeAnotacao);
		if(anotacao == null)
		{
			Class<?> classeMetodo = metodo.getDeclaringClass(); 
			anotacao = classeMetodo.getAnnotation(classeAnotacao);
			if(anotacao == null)
			{
				anotacao = classeMetodo.getPackage().getAnnotation(classeAnotacao);
			}
		}
		return anotacao;		
	}
	
	private <T extends Annotation> boolean possuiAnotacao(Method metodo, Class<T> classeAnotacao)
	{
		return getAnotacao(metodo, classeAnotacao) != null;
	}
	
	private ServerResponse getRespostaForbidden() {
		return new ServerResponse(WSMessages.ME_TOKEN_INVALIDO, Status.FORBIDDEN.getStatusCode(), null);
	}

	private static final String TOKEN_NAME = "webserviceToken";
	
	private void validarToken(String token) throws ValidationException {
		String tokenParametro = (String) Contexts.getApplicationContext().get(TOKEN_NAME);
		if (tokenParametro == null || !tokenParametro.equals(token)){
			throw new ValidationException(WSMessages.ME_TOKEN_INVALIDO.codigo());
		}
	}
	
	@Override
	public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException {
		List<String> valoresToken = request.getHttpHeaders().getRequestHeader("token"); 
		if(valoresToken == null || valoresToken.size() == 0)
		{
			return getRespostaForbidden();
		}
		String valorToken = valoresToken.get(0);
		validarToken(valorToken);
		return null;
	}

	@Override
	public void postProcess(ServerResponse response) {
		//if(possuiAnotacao(response.getResourceMethod(), InjectSeamContext.class))
		//{
		//	Lifecycle.endCall();			
		//}
		
		//logWebserviceServerManager.endLog(logWsServer, mensagemRetorno);		
	}

}
