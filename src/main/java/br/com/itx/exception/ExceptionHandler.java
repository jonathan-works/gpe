package br.com.itx.exception;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.exception.Exceptions;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.itx.component.Util;

@Name("org.jboss.seam.exception.exceptions")
@Scope(ScopeType.APPLICATION)
@Install(precedence = Install.FRAMEWORK)
@BypassInterceptors
public class ExceptionHandler extends Exceptions {

	protected static final LogProvider LOG = Logging.getLogProvider(ExceptionHandler.class);
	private static Util util = new Util();
	
	@Override
	public void handle(Exception e) throws Exception {
		printErroInfo(e);
		super.handle(e);
	}

	protected void printErroInfo(Exception e) {
		String urlRequest = getUrlRequest();
		System.out.println(e.getMessage());
		printLine();
		LOG.error("e.getMessage(): " + e.getMessage());
		LOG.error("Par�metros: " + getRequestParams());
		LOG.error("Url Requisi��o: " + urlRequest);
		printLine();
	}

	protected void printLine() {
		LOG.error("---------------------------------------------");
	}

	private String getRequestParams() {
		try {
			return util.getRequestParams();
		} catch (NullPointerException e) {
			return null;
		}
	}

	private String getUrlRequest() {
		try {
			return util.getUrlRequest();
		} catch (NullPointerException e) {
			//N�o h� ServletContext
			return null;
		}
	}
	
}
