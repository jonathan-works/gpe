package br.com.infox.epp.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

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
	private static final String MESSAGES_PROPERTIES = "/extended_messages.properties";
	private static final String ENTITY_MESSAGES_EPP_PATH = "/entity_messages_pt_BR.properties";
	private static final String MESSAGES_EPP_PATH = "/messages_pt_BR.properties";
	private static final String STANDARD_MESSAGES_EPP_PATH = "/standard_messages_pt_BR.properties";
	private static final String PROCESS_DEFINITION_MESSAGES_EPP_PATH = "/process_definition_messages_pt_BR.properties";
	private static final String VALIDATION_MESSAGES = "/ValidationMessages.properties";
	public static final String EPP_MESSAGES = "eppmessages";
	
	private Properties pageProperties;
	private Properties menuProperties;
	private List<String> menuItems;
	
	@Create
	public void init() {
		loadPageProperties();
		loadMessagesProperties();
	}
	
	private void loadPageProperties(){
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
			    LOG.error("Falha ao recuperar arquivos especificados no Properties Loader.", e);
			}
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
	
	private void loadMessagesProperties(){
		InputStream isEntityMessagesEpp = getClass().getResourceAsStream(ENTITY_MESSAGES_EPP_PATH);
		InputStream isMessagesEpp = getClass().getResourceAsStream(MESSAGES_EPP_PATH);
		InputStream isProcessDefinitionMessagesEpp = getClass().getResourceAsStream(PROCESS_DEFINITION_MESSAGES_EPP_PATH);
		InputStream isStandardMessagesEpp = getClass().getResourceAsStream(STANDARD_MESSAGES_EPP_PATH);
		InputStream isValidationMessages = getClass().getResourceAsStream(VALIDATION_MESSAGES);
		InputStream isMessagesExt = getClass().getResourceAsStream(MESSAGES_PROPERTIES);
		
		try {
			Map<String, String> messages = new HashMap<>();

			Properties source = new Properties();
            source.load(isEntityMessagesEpp);
			copyProperties(source, messages);
			
			source = new Properties();
			source.load(isMessagesEpp);
			copyProperties(source, messages);
			
			source = new Properties();
            source.load(isProcessDefinitionMessagesEpp);
            copyProperties(source, messages);
            
            source = new Properties();
            source.load(isStandardMessagesEpp);
            copyProperties(source, messages);
            
            source = new Properties();
            source.load(isValidationMessages);
            copyProperties(source, messages);
            
            if (isMessagesExt != null) {
                source = new Properties();
                source.load(isMessagesExt);
                copyProperties(source, messages); 
            }
             
			Contexts.getApplicationContext().set(EPP_MESSAGES, messages);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void copyProperties(Properties source, Map<String, String> destination) {
	    Enumeration<Object> srcKeys = source.keys();
        while (srcKeys.hasMoreElements()) {
            String key = srcKeys.nextElement().toString();
            String value = source.getProperty(key);
            destination.put(key, value);
        }
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> getMenuItems(){
		if (menuProperties == null){
			menuProperties = new Properties();
			InputStream is = getClass().getResourceAsStream(MENU_PROPERTIES);
			if (is != null) {
				try {
					menuProperties.load(is);
					menuItems = Collections.unmodifiableList(new ArrayList(menuProperties.values()));
				} catch (IOException e) {
				    LOG.error("Falha ao recuperar arquivos especificados no Properties Loader.", e);
				}
			}
		}
		return (menuItems == null ? (menuItems = Collections.unmodifiableList(new ArrayList<String>())) : menuItems);
	}
	
}