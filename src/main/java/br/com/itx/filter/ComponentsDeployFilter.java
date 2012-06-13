/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.itx.filter;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Element;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.core.Init;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.XML;
import org.jboss.seam.web.AbstractFilter;
import org.jboss.seam.web.HotDeployFilter;


/**
 * Filtro para hotdeploy dos componentes xml em META-INF
 * @author luiz
 *
 */
@Name("br.com.infox.hotDeployFilter")
@BypassInterceptors
@Scope(APPLICATION)
@Filter
public class ComponentsDeployFilter extends AbstractFilter { 

	private static LogProvider log = Logging.getLogProvider(HotDeployFilter.class);
	
	@Create
	public void init() {
		log.info(getClass().getName() + " init");
	}
	
	@Override
	public boolean isDisabled() {
		return !Init.instance().isDebug();
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		Init init = (Init) getServletContext().getAttribute(
				Seam.getComponentName(Init.class));
		
		if (init != null && init.hasHotDeployableComponents()) {
			init.setDebug(false);
			Set<String> hotComponents = new HashSet<String>();
			for (File file : init.getHotDeployPaths()) {
				if (scan(request, init, file, hotComponents)) {
					break;
				}
			}
			if (!hotComponents.isEmpty()) {
//				Set<String> set = new HashSet<String>(init.getHotDeployableComponents());
//				init.getHotDeployableComponents().clear();
//				init.getHotDeployableComponents().addAll(hotComponents);
				try {
					new Initialization(getServletContext())
							.redeploy((HttpServletRequest) request, init);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//				init.getHotDeployableComponents().addAll(set);
//				init.getHotDeployableComponents().addAll(hotComponents);
			}
			init.setTimestamp(System.currentTimeMillis());
		}
		chain.doFilter(request, response);
	}

	@SuppressWarnings("unchecked")
	private boolean scan(ServletRequest request, Init init, File file, Set<String> hotComponents) {
		if (file.isFile()) {
			if (!file.exists() || (file.lastModified() > init.getTimestamp())) {
				if (log.isDebugEnabled()) {
					log.debug("file updated: " + file.getName());
				}

				String name = file.getName();
				Seam.clearComponentNameCache();
				if (name.indexOf(".component.") != -1) {
					name = name.split(".component.")[0];
					try {
						Element root = XML.getRootElementSafely(new FileInputStream(file));
						List<Element> elements = root.elements("component");
						for (Element component : elements) {
							String cName = component.attributeValue("name");
							hotComponents.add(cName);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					hotComponents.add(name);
				} else {
					try {
						new Initialization(getServletContext())
								.redeploy((HttpServletRequest) request);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					hotComponents.clear();
					return true;
				}
			}
		} else if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				if (scan(request, init, f, hotComponents)) {
					return true;
				}
			}
		}
		return false;
	}
	
}