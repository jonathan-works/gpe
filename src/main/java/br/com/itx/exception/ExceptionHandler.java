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

	protected static final LogProvider log = Logging.getLogProvider(ExceptionHandler.class);
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
		log.error("e.getMessage(): " + e.getMessage());
		log.error("Parâmetros: " + getRequestParams());
		log.error("Url Requisição: " + urlRequest);
		printLine();
	}

	protected void printLine() {
		log.error("---------------------------------------------");
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
			//Não há ServletContext
			return null;
		}
	}
	
}
