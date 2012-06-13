/*
 * IBPM - Ferramenta de produtividade Java
 * Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 * sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 * Free Software Foundation; versão 2 da Licença.
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 * ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 * 
 * Consulte a GNU GPL para mais detalhes.
 * Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 * veja em http://www.gnu.org/licenses/  
 */
package br.com.infox.filter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.Strings;
import org.jboss.seam.web.AbstractFilter;


@Name("resourceFilter")
@BypassInterceptors
@Scope(ScopeType.APPLICATION)
@Filter
public class ResourceFilter extends AbstractFilter {

	
	private static final String PATTERN = "/img/.*|/js/.*|/stylesheet/.*|/styleSkinInfox/.*";
 
	@Override
	public String getRegexUrlPattern() {
		return PATTERN;
	}
	
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		String path = request.getServletPath();
		ServletContext servletContext = request.getSession().getServletContext();
		String realPath = servletContext.getRealPath(path);
		if (new File(realPath).exists()) {
			chain.doFilter(request, response);
		} else {
			InputStream is = Resources.getResourceAsStream(path, servletContext);
			if (is != null) {
				writeResponse(response, path, is);
			}
		}
	}

	private void writeResponse(HttpServletResponse response, String path, InputStream is) {
		if (path.endsWith(".png") || path.endsWith(".gif") || path.endsWith(".jpg")) {
			try {
				ServletOutputStream os = response.getOutputStream();
				byte[] trecho = new byte[10240];
			    for ( int n; (n = is.read(trecho)) != -1; ) {
			    	os.write(trecho, 0, n);
			    }
                is.close();
    	        os.flush();
            } catch (IOException err) {
				err.printStackTrace();
			}
		} else {
			try {
				String text = Strings.toString(is);
				response.getWriter().write(text.trim());
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

}