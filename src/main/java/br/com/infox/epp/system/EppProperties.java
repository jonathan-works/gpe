package br.com.infox.epp.system;

import java.io.IOException;
import java.util.Properties;

public class EppProperties {
    private static Properties properties;
    
    private static synchronized void init() {
    	if (properties == null) {
	        try {
	        	properties = new Properties();
		        properties.load(EppProperties.class.getResourceAsStream("/epp.properties"));
	        } catch (IOException e) {
	        	throw new RuntimeException(e);
	        }
    	}
    }
    
    public static String getProperty(String property) {
    	if (properties == null) {
    		init();
    	}
        return properties.getProperty(property);
    }
    
    public static final String PROPERTY_TIPO_BANCO_DADOS = "tipoBancoDados";
    public static final String PROPERTY_DATASOURCE_PREFIX = "datasourceJndiPrefix";
    public static final String PROPERTY_TRANSACTION_MANAGER_JNDI = "transactionManager.jndi";
    public static final String PROPERTY_DATASOURCE = "datasource";
    
    public static final String PROPERTY_DESENVOLVIMENTO = "desenvolvimento";
}
