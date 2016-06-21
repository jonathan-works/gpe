package br.com.infox.ibpm.logging;

import org.jbpm.logging.LoggingService;
import org.jbpm.logging.log.ProcessLog;

public class EmptyLoggingService implements LoggingService {

    private static final long serialVersionUID = 1L;

    public void log(ProcessLog processLog) {
    }

    public void close() {
    }

}
