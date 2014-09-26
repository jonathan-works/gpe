package br.com.infox.epp.system;

import java.io.Serializable;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;

/**
 * Seta as mensagens carregadas pelo EJB PropertiesLoader no contexto de aplicação do Seam
 * @author gabriel
 *
 */

@Name(EppMessagesContextLoader.NAME)
@Scope(ScopeType.APPLICATION)
@Startup
public class EppMessagesContextLoader implements Serializable {
    public static final String NAME = "eppMessagesContextLoader";
    public static final String EPP_MESSAGES = "eppmessages";

    private static final long serialVersionUID = 1L;

    @Create
    public void init() throws NamingException {
        InitialContext ic = new InitialContext();
        PropertiesLoader propertiesLoader = (PropertiesLoader) ic.lookup(PropertiesLoader.JNDI_PORTABLE_NAME);
        Contexts.getApplicationContext().set(EPP_MESSAGES, propertiesLoader.getMessages());
    }
}
