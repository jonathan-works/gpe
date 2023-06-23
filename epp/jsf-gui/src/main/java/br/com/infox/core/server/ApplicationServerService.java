package br.com.infox.core.server;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.system.Configuration;

@Singleton
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ApplicationServerService implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(ApplicationServerService.class.getName());
	private static final long serialVersionUID = 1L;

	private static final String JBOSS_HTTP_SOCKET_BINDING = "jboss.as:socket-binding-group=standard-sockets,socket-binding=http";
	private static final String JBOSS_HTTPS_SOCKET_BINDING = "jboss.as:socket-binding-group=standard-sockets,socket-binding=https";
	private static final String TOMCAT_SERVICE_CONNECTOR = "Catalina:type=Service";

	private boolean isSecure;
	private String basePath;

	public String getBaseResquestUrl() {
	    if (basePath == null) {
	        buildBasePath();
	    }
		return basePath;
	}

	private void buildBasePath() {
		String host = System.getProperty("jboss.bind.address");
		Integer port = getServerListeningPort();
		basePath = (isSecure ? "https://" : "http://") + (host == null ? "0.0.0.0" : host) + ":" + port;
	}

	private Integer getServerListeningPort() {
    	MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        try {
			ObjectName socketBindingMBean = new ObjectName(JBOSS_HTTPS_SOCKET_BINDING);
            String  boundAddress = (String) mBeanServer.getAttribute(socketBindingMBean, "boundAddress");
            isSecure = boundAddress != null;
            if (boundAddress == null) {
            	return (Integer) ManagementFactory.getPlatformMBeanServer().getAttribute(new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http"), "port");
            }

        } catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException | MalformedObjectNameException e) {
			LOGGER.info("Porta naop encontrada");
        }
		return 8080;
	}
	
	public TransactionManager getTransactionManager() {
        return Configuration.getInstance().getApplicationServer().getTransactionManager();
	}
	
	public Transaction getTransaction() {
	    try {
            return getTransactionManager().getTransaction();
        } catch (SystemException e) {
            return null;
        }
	}
            
    public String getInstanceName() {
        return Configuration.getInstance().getApplicationServer().getInstanceName();
    }
    
    public String getLogDir() {
        return Configuration.getInstance().getApplicationServer().getLogDir();
    }
    
    public static ApplicationServerService instance() {
        return Beans.getReference(ApplicationServerService.class);
    }
           
}
