package br.com.infox.ibpm.logging;

import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.logging.LoggingService;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.persistence.PersistenceService;
import org.jbpm.persistence.db.DbPersistenceService;

public class LoggingServiceImpl implements LoggingService {

    private static final long serialVersionUID = 1L;

    private PersistenceService persistenceService;

    public LoggingServiceImpl() {
        JbpmContext jbpmContext = JbpmContext.getCurrentJbpmContext();
        if (jbpmContext == null) {
            throw new JbpmException("no active jbpm context");
        }
        persistenceService = jbpmContext.getServices().getPersistenceService();
    }
    
    //TODO: Precisa ver o porque a transação não chega aberta aqui. 
    //Precisa resolver isso pois caso seja retirado, já tem um bug cadastrado
    public void log(ProcessLog processLog) {
        // check if transaction is active before saving log
        // https://jira.jboss.org/browse/JBPM-2983
        if (persistenceService instanceof DbPersistenceService) {
            DbPersistenceService dbPersistenceService = (DbPersistenceService) persistenceService;
            if (dbPersistenceService.isTransactionActive()) {
                // Improvement suggestions:
                // db-level: use hi-lo id strategy to avoid repetitive insert
                // (dependent on db-lock)
                // session: use stateless session or at least different session
                // can we borrow connection safely? (open on top of another
                // session)
                dbPersistenceService.getSession().save(processLog);
            }
        }
    }

    public void close() {
    }

}
