package br.com.infox.ibpm.logging;

import org.jbpm.svc.Service;
import org.jbpm.svc.ServiceFactory;

public class EmptyLoggingServiceFactory implements ServiceFactory {

    private static final long serialVersionUID = 1L;

    public void close() {
    }

    public Service openService() {
        return new EmptyLoggingService();
    }

}
