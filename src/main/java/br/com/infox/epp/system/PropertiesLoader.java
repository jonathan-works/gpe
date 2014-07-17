package br.com.infox.epp.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.international.Messages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Classe que carrega páginas customizadas pelo cliente e as insere no ePP
 * dinamicamente, ou substitui as páginas padrão do ePP, caso essas existam.
 * 
 * @author avner
 * 
 */
@Name(PropertiesLoader.NAME)
@Startup()
@Scope(ScopeType.APPLICATION)
public class PropertiesLoader extends Properties {
	private static final LogProvider LOG = Logging.getLogProvider(PropertiesLoader.class);
	public static final String NAME = "propertiesLoader";
	private static final long serialVersionUID = 1L;
	
	@Create
	public void init() {
		URL resource = getClass().getResource("/src/main/resources/custom_pages.properties");
		if (resource != null) {
			InputStream is = getClass().getResourceAsStream("/src/main/resources/custom_pages.properties");
			
			try {
				load(is);
				
				Enumeration<Object> keys = keys();
				while (keys.hasMoreElements()) {
					String key = (keys.nextElement().toString());
					String value = getProperty(key);

					File file = new File(ServletLifecycle.getServletContext().getRealPath(key));

					performLoad(file, value);
				}
			} catch (IOException e) {
				LOG.error(Messages.instance().get("propertiesLoader.fail"), e);
			}
		} else {
			// Resource not found, noting to do here
		}
	}
	
	private void performLoad(File file, String path) throws IOException {
		InputStream newInputStream = getClass().getResourceAsStream(path);
		if (newInputStream != null) {
			if (file.exists()) {
				file.delete();
			} else {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
			FileOutputStream newOutputStream = new FileOutputStream(file);
			
			int read = newInputStream.read();
			while (read != -1) {
				newOutputStream.write(read);
				read = newInputStream.read();
			}
			newInputStream.close();
			newOutputStream.close();
		}
	}
}