package br.com.infox.jsf.events;

import javax.faces.event.AbortProcessingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import br.com.infox.core.report.RequestInternalPageService;
import br.com.infox.epp.cdi.util.JNDI;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class EppServletEventListener implements ServletContextListener{
	
	 private static final LogProvider log = Logging.getLogProvider(EppServletEventListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		log.info("Injetando o ContextPath criado no RequestInternalPageService");
		RequestInternalPageService requestInternalPageService = JNDI.lookup("java:module/RequestInternalPageService");
		if (requestInternalPageService == null) {
		    throw new AbortProcessingException("RequestInternal page java:module/RequestInternalPageService não encontrado");
		}
		requestInternalPageService.setContextPath(event.getServletContext().getContextPath());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
