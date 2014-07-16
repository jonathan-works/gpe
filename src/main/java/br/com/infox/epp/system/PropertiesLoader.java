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

@Name(PropertiesLoader.NAME)
@Startup()
@Scope(ScopeType.APPLICATION)
public class PropertiesLoader extends Properties {

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
					
					if (!file.isDirectory()) {
						if (file.exists()) {
							file.delete();
						}
						
						InputStream newFileStream = getClass().getResourceAsStream(value);
						
						file.createNewFile();
						FileOutputStream f = new FileOutputStream(file);
	
						int read = newFileStream.read();
						while (read != -1) {
							f.write(read);
							read = newFileStream.read();
						}
						f.close();
						newFileStream.close();
					} else {
						
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// Resource not found
		}
	}
}