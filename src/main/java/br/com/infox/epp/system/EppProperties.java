package br.com.infox.epp.system;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.seam.util.ComponentUtil;

@Name(EppProperties.NAME)
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class EppProperties implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String NAME = "eppProperties";
    
    private Properties properties;
    
    public static EppProperties getInstance() {
    	return ComponentUtil.getComponent(EppProperties.NAME);
	}

	@Create
    public void init() throws IOException {
        properties = new Properties();
        properties.load(getClass().getResourceAsStream("/epp.properties"));
    }
    
    public String getProperty(String property) {
        return properties.getProperty(property);
    }
    
    public static final String PROPERTY_TIPO_BANCO_DADOS = "tipoBancoDados";
}
