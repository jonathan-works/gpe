package br.com.infox.epp.system;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

@Name(PropertiesLoader.NAME)
@Startup(depends={"poc-login.jar"})
@Scope(ScopeType.APPLICATION)
public class PropertiesLoader extends Properties {

	public static final String NAME = "propertiesLoader";
	private static final long serialVersionUID = 1L;
	
	@Create
	public void init() {
		URL resource = getClass().getResource("/src/main/resources/custom_pages.properties");
		if (resource != null) {
			String path = resource.getPath();
			
			File file = new File(path);
			file.exists();
		}
	}
}
