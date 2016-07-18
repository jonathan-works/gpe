package br.com.infox.epp.system.security;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.navigation.Pages;

@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Name("pagesModuleLoader")
@Install(precedence = Install.FRAMEWORK, dependencies = "org.jboss.seam.navigation.pages")
@Startup
public class PagesModuleLoader implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(PagesModuleLoader.class);

	@PostConstruct
	public void init() {
		try {
			Pages pages = (Pages) Component.getInstance("org.jboss.seam.navigation.pages");
			Enumeration<URL> urls = getClass().getClassLoader().getResources("pages_xml.txt");
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				try (Scanner scanner = new Scanner(url.openStream())) {
					while (scanner.hasNextLine()) {
						pages.addPage(scanner.nextLine());
					}
				}
			}
		} catch (IOException e) {
			LOG.error("Erro ao carregar os arquivos de configuração de restrição de páginas", e);
			throw new RuntimeException(e);
		}
	}
	
}
