package br.com.infox.core.transaction;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.transaction.UserTransaction;

import br.com.infox.core.exception.ApplicationException;

@Name(TransactionService.NAME)
@Scope(ScopeType.APPLICATION)
public class TransactionService {
    
    public static final String NAME = "transactionService";
    private static final LogProvider LOG = Logging.getLogProvider(TransactionService.class);
    
    public static boolean beginTransaction() {
        try {
            UserTransaction ut = Transaction.instance();
            if (ut != null && !ut.isActive()) {
                ut.begin();
                return true;
            }
        } catch (Exception e) {
            LOG.error(".beginTransaction()", e);
            throw new ApplicationException(ApplicationException.createMessage("iniciar transação", "beginTransaction()", "RegistraEventoAction", "BPM"), e);
        }
        return false;
    }

    public static void commitTransction() {
        try {
            UserTransaction ut = Transaction.instance();
            if (ut != null && ut.isActive()) {
                ut.commit();
            }
        } catch (Exception e) {
            LOG.error(".commitTransction()", e);
            throw new ApplicationException(ApplicationException.createMessage("iniciar transação", "beginTransaction()", "RegistraEventoAction", "BPM"), e);
        }
    }

}
