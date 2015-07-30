package br.com.infox.jsf.events;

import javax.faces.event.AbortProcessingException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import br.com.infox.core.report.RequestInternalPageService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class EppServletEventListener implements ServletContextListener{
	
	 private static final LogProvider log = Logging.getLogProvider(EppServletEventListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		InitialContext ic;
		try {
			ic = new InitialContext();
			log.info("Injetando o ContextPath criado no RequestInternalPageService");
			RequestInternalPageService requestInternalPageService = (RequestInternalPageService) ic.lookup("java:module/RequestInternalPageService");
			requestInternalPageService.setContextPath(event.getServletContext().getContextPath());
		} catch (NamingException e) {
			throw new AbortProcessingException(e);
		}
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}
