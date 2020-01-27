package br.com.infox.epp.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

//import org.primefaces.util.Constants;

@WebListener
public class WebApplicationSetupListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
//		sce.getServletContext().setInitParameter(Constants.ContextParams.THEME, "epp");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
