package br.com.infox.epp.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Classe que carrega páginas customizadas pelo cliente e as insere no ePP
 * dinamicamente, ou substitui as páginas padrão do ePP, caso essas existam.
 * 
 * @author avner
 * 
 */
@Startup
@Name(PropertiesLoader.NAME)
@Scope(ScopeType.APPLICATION)
public class PropertiesLoader implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(PropertiesLoader.class);
	public static final String NAME = "propertiesLoader";
	
	private static final String PAGE_PROPERTIES = "/custom_pages.properties";
	private static final String MENU_PROPERTIES = "/menu.properties";
	
	private Properties pageProperties;
	private Properties menuProperties;
	private List<String> menuItems;
	
	@Create
	public void init() {
		InputStream is = getClass().getResourceAsStream(PAGE_PROPERTIES);
		if (is != null) {
			try {
				pageProperties = new Properties();
				pageProperties.load(is);
				
				Enumeration<Object> keys = pageProperties.keys();
				while (keys.hasMoreElements()) {
					String key = (keys.nextElement().toString());
					String value = pageProperties.getProperty(key);

					performLoad(key, value);
				}
			} catch (IOException e) {
				LOG.error(Messages.instance().get("propertiesLoader.fail"), e);
			}
		} else {
			// Resource not found, noting to do here
		}
	}
	
	private void performLoad(String key, String path) throws IOException {
		InputStream newInputStream = getClass().getResourceAsStream(key);
		if (newInputStream != null) {
			File file = new File(ServletLifecycle.getServletContext().getRealPath(path));
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
	
	@SuppressWarnings("unchecked")
	public List<String> getMenuItems(){
		if (menuProperties == null){
			menuProperties = new Properties();
			InputStream is = getClass().getResourceAsStream(MENU_PROPERTIES);
			if (is != null) {
				try {
					menuProperties.load(is);
					menuItems = Collections.unmodifiableList(new ArrayList<>(menuProperties.values()));
				} catch (IOException e) {
					menuItems = Collections.unmodifiableList(new ArrayList<>());
					LOG.error(Messages.instance().get("propertiesLoader.fail"), e);
				}
			}
		}
		return menuItems;
	}
	
}