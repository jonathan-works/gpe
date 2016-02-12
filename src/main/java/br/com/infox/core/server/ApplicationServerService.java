package br.com.infox.core.server;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;

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

@Singleton
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ApplicationServerService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private static final String JBOSS_HTTP_SOCKET_BINDING = "jboss.as:socket-binding-group=standard-sockets,socket-binding=http";
    private static final String JBOSS_HTTPS_SOCKET_BINDING = "jboss.as:socket-binding-group=standard-sockets,socket-binding=https";
    private static final String TOMCAT_SERVICE_CONNECTOR = "Catalina:type=Service";
    
    private boolean isSecure;
    private String basePath;
    
    public void init() {
    	buildBasePath();
    }
    
    public String getBaseResquestUrl() {
        return basePath;
    }
    
    private void buildBasePath() {
        String host = System.getProperty("jboss.bind.address");
        Integer port = getServerListeningPort();
        basePath = (isSecure ? "https://" : "http://")  + (host == null ? "0.0.0.0" : host) + ":" + port;
    }
    
    private Integer getServerListeningPort() {
    	MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
        	ObjectName socketBindingMBean = new ObjectName(JBOSS_HTTPS_SOCKET_BINDING);
            String  boundAddress = (String) mBeanServer.getAttribute(socketBindingMBean, "boundAddress");
            isSecure = boundAddress != null;
            if (boundAddress == null) {
            	socketBindingMBean = new ObjectName(JBOSS_HTTP_SOCKET_BINDING);
    public String getInstanceName() {
        return System.getProperty("jboss.node.name");
    }
    
    public String getLogDir() {
        return System.getProperty("jboss.server.log.dir");
    }

            }
            return (Integer) mBeanServer.getAttribute(socketBindingMBean, "boundPort");
        } catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException | MalformedObjectNameException e) {
    		try {
				ObjectName socketBindingMBean = new ObjectName(TOMCAT_SERVICE_CONNECTOR);
				Object service = mBeanServer.getAttribute(socketBindingMBean, "managedResource");
				Object[] connectors = (Object[]) service.getClass().getMethod("findConnectors").invoke(service);
				for (Object connector : connectors) {
					boolean isHttp = connector.getClass().getMethod("getProtocol").invoke(connector).equals("HTTP/1.1");
					if (isHttp) {
						isSecure = (boolean) connector.getClass().getMethod("getSecure").invoke(connector);
						return (Integer) connector.getClass().getMethod("getPort").invoke(connector);
					}
				}
    		} catch (MalformedObjectNameException | InstanceNotFoundException | ReflectionException | AttributeNotFoundException | MBeanException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
    			throw new IllegalStateException(e1);
			}
    		throw new IllegalStateException(e);
        }
    }

}
