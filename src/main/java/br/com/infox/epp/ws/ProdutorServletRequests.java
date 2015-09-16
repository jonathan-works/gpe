package br.com.infox.epp.ws;

import javax.enterprise.inject.Produces;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;

/**
 * Classe responsável por injetar {@link ServletRequest} utilizando CDI
 * @author paulo
 *
 */
@WebListener
public class ProdutorServletRequests implements ServletRequestListener {

    private static ThreadLocal<ServletRequest> SERVLET_REQUESTS = new ThreadLocal<>();

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        SERVLET_REQUESTS.set(sre.getServletRequest());
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        SERVLET_REQUESTS.remove();
    }

    @Produces
    private ServletRequest obtain() {
        return SERVLET_REQUESTS.get();
    }

}