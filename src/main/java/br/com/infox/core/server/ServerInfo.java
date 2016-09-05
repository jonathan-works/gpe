package br.com.infox.core.server;

import br.com.infox.epp.cdi.util.JNDI;

public class ServerInfo {
    
    private static final String JBOSS = "jboss";
    private static final String TOMEE = "tomee";
    
    private static String serverName;
    
    static {
        Object object = JNDI.lookup("java:jboss");
        if (object != null) {
            serverName = JBOSS;
        } else {
            object = JNDI.lookup("openejb:Resource");
            if (object != null) {
                serverName = TOMEE;
            } else {
                throw new IllegalStateException("Application Server not supported");
            }
        }
    }
    
    public static String getServerName() {
        return serverName;
    }
    
    public static boolean isJboss() {
        return JBOSS.equals(serverName);
    }
    
    public static boolean isTomee() {
        return TOMEE.equals(serverName);
    }
    
}
