package br.com.infox.jsf.events;

import javax.faces.event.AbortProcessingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.epp.cdi.util.JNDI;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class EppServletEventListener implements ServletContextListener {
	
	 private static final LogProvider log = Logging.getLogProvider(EppServletEventListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		log.info("Injetando o ContextPath criado no RequestInternalPageService");
		ApplicationServerService applicationServerService = JNDI.lookup("java:module/ApplicationServerService");
		if (applicationServerService == null) {
		    throw new AbortProcessingException("RequestInternal page java:module/ApplicationServerService não encontrado");
		}
		applicationServerService.setContextPath(event.getServletContext().getContextPath());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
