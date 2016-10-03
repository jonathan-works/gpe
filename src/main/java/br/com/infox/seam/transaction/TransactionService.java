package br.com.infox.seam.transaction;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.transaction.UserTransaction;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.ApplicationException;

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
    
    public static void rollbackTransaction() {
        try {
            UserTransaction ut = Transaction.instance();
            if (ut != null && ut.isActive()) {
                ut.rollback();
            }
        } catch (Exception e) {
            throw new ApplicationException(ApplicationException.createMessage("rollback da transação", "rollbackTransaction()", "Util", "ePP"), e);
        }
    }

}
