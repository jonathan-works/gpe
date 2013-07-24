/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.core.Init;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.web.AbstractFilter;
import org.jboss.util.StopWatch;


@Name("meeterFilter")
@BypassInterceptors
@Scope(ScopeType.APPLICATION)
@Filter
public class MeeterFilter extends AbstractFilter {

	private static final LogProvider LOG = Logging.getLogProvider(MeeterFilter.class);	
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		StopWatch sw = new StopWatch(true);
		HttpServletRequest hsr = (HttpServletRequest) req;
		chain.doFilter(req, resp);
		String ajaxPushHeader = null;
		long time = sw.getTime();
		if (ajaxPushHeader == null && time > 100) {
			LOG.info(((HttpServletRequest)req).getRequestURI() +  ": " + time);
		}
	}

	@Override
	public boolean isDisabled() {
		return !Init.instance().isDebug();
	}

}