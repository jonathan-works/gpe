package br.com.infox.ibpm.logging;

import org.jbpm.svc.Service;
import org.jbpm.svc.ServiceFactory;

public class LoggingServiceFactory implements ServiceFactory {

    private static final long serialVersionUID = 1L;

    public void close() {
    }

    public Service openService() {
        return new LoggingServiceImpl();
    }

}
