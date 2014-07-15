package br.com.infox.epp.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
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

			InputStream is = getClass().getResourceAsStream("/src/main/resources/custom_pages.properties");
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(reader);
		
			try {
				String line = null;
				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			
		}
	}
}
