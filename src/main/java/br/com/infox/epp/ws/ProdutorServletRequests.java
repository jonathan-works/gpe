package br.com.infox.epp.ws;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

/**
 * Classe responsável por injetar {@link ServletRequest} utilizando CDI
 * @author paulo
 *
 */
@WebListener
public class ProdutorServletRequests implements ServletRequestListener {

    private static ThreadLocal<ServletRequest> SERVLET_REQUESTS = new ThreadLocal<>();
    
    @Inject
    private Logger logger;

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
    	logger.finer("ServletRequest inicializado: " + sre.getServletRequest().toString());
        SERVLET_REQUESTS.set(sre.getServletRequest());
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
    	logger.finer("ServletRequest destruído: " + sre.getServletRequest().toString());
        SERVLET_REQUESTS.remove();
    }

    @Produces
    @RequestScoped
    private HttpServletRequest criarServletRequest() {
    	ServletRequest req = SERVLET_REQUESTS.get();
    	if(req == null) {
    		throw new RuntimeException("Não existe um ServletRequest associado à thread atual");
    	}
    	if(!(req instanceof HttpServletRequest)) {
    		throw new RuntimeException("ServletRequest da thread atual não é um HttpServletRequest");    		
    	}
    	HttpServletRequest retorno = (HttpServletRequest)req;
    	logger.finer("Injetando ServletRequest: " + retorno.toString());
        return retorno;
    }

}