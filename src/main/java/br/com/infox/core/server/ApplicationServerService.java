package br.com.infox.core.server;

import java.io.Serializable;
import java.lang.management.ManagementFactory;

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

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Singleton
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ApplicationServerService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private static final LogProvider LOG = Logging.getLogProvider(ApplicationServerService.class);
    private static final String JBOSS_HTTP_SOCKET_BINDING = "jboss.as:socket-binding-group=standard-sockets,socket-binding=http";
    private static final String JBOSS_HTTPS_SOCKET_BINDING = "jboss.as:socket-binding-group=standard-sockets,socket-binding=https";
    
    private String basePath;
    private ObjectName socketBindingMBean;
    private SocketBindingType sbt;
    private MBeanServer mBeanServer;
    
    public String getBaseResquestUrl() {
        if (basePath == null) buildSocketBindingInfo();
        return basePath;
    }
    
    private void buildSocketBindingInfo() {
        if(sbt != null) return;
        try {
            mBeanServer = ManagementFactory.getPlatformMBeanServer();
            socketBindingMBean = new ObjectName(JBOSS_HTTPS_SOCKET_BINDING);
            String  boundAddress = (String) mBeanServer.getAttribute(socketBindingMBean, "boundAddress");
            if (boundAddress == null) {
                socketBindingMBean = new ObjectName(JBOSS_HTTP_SOCKET_BINDING);
                sbt = SocketBindingType.HTTP;
            } else {
                sbt = SocketBindingType.HTTPS;
            }
            buildBasePath();
        } catch (MalformedObjectNameException | AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException e) {
            LOG.error(e);
        }
    }
    
    private void buildBasePath() {
        Integer port = getServerListeningPort();
        String host = System.getProperty("jboss.bind.address");
        basePath = sbt.getDescricao() + host + ":" + port;
    }
    
    private Integer getServerListeningPort() {
        try {
            Integer port = (Integer) mBeanServer.getAttribute(socketBindingMBean, "boundPort");
            return port;
        } catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException e) {
            LOG.error(e);
        }
        return sbt.defaultPort;
    }

    private enum SocketBindingType {

        HTTP("http://", 80), HTTPS("https://", 443);

        private String descricao;
        private Integer defaultPort;

        SocketBindingType(String description, Integer port) {
            this.setDescricao(description);
            this.setDefaultPort(port);
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public Integer getDefaultPort() {
            return defaultPort;
        }

        public void setDefaultPort(Integer defaultPort) {
            this.defaultPort = defaultPort;
        }
    }

}
