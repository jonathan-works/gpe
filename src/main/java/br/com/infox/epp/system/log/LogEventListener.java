package br.com.infox.epp.system.log;

import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import br.com.infox.epp.system.type.TipoOperacaoLogEnum;
import br.com.infox.epp.system.util.LogUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class LogEventListener implements PostUpdateEventListener, PostInsertEventListener, PostDeleteEventListener {

    private static final long serialVersionUID = 1L;
    private static final String ENABLE_LOG_VAR_NAME = "executeLog";

    private static final LogProvider LOG = Logging.getLogProvider(LogEventListener.class);

    public void onPostUpdate(PostUpdateEvent event) {
        if (LogUtil.isLogable(event.getEntity()) && isLogEnabled()) {
            ExecuteLog executeLog = new ExecuteLog();
            executeLog.setEntidade(event.getEntity());
            executeLog.setOldState(event.getOldState());
            executeLog.setState(event.getState());
            executeLog.setPersister(event.getPersister());
            executeLog.setTipoOperacao(TipoOperacaoLogEnum.U);
            try {
                executeLog.execute();
            } catch (Exception e) {
                LOG.error(".onPostUpdate()", e);
            }
        }
    }

    public void onPostInsert(PostInsertEvent event) {
        if (LogUtil.isLogable(event.getEntity()) && isLogEnabled()) {
            ExecuteLog executeLog = new ExecuteLog();
            executeLog.setEntidade(event.getEntity());
            executeLog.setOldState(null);
            executeLog.setState(event.getState());
            executeLog.setPersister(event.getPersister());
            executeLog.setTipoOperacao(TipoOperacaoLogEnum.I);
            try {
                executeLog.execute();
            } catch (Exception e) {
                LOG.error(".onPostInsert()", e);
            }
        }
    }

    public void onPostDelete(PostDeleteEvent event) {
        if (LogUtil.isLogable(event.getEntity()) && isLogEnabled()) {
            ExecuteLog executeLog = new ExecuteLog();
            executeLog.setEntidade(event.getEntity());
            executeLog.setOldState(event.getDeletedState());
            executeLog.setState(null);
            executeLog.setPersister(event.getPersister());
            executeLog.setTipoOperacao(TipoOperacaoLogEnum.D);
            try {
                executeLog.execute();
            } catch (Exception e) {
                LOG.error(".onPostDelete()", e);
            }
        }
    }

    private boolean isLogEnabled() {
    	return isLogEnabled(Contexts.getEventContext());
    }
    
    public static boolean isLogEnabled(Context context) {
        if (context == null) {
            return false;
        }
        Object test = context.get(ENABLE_LOG_VAR_NAME);
        try {
            return test == null || "true".equalsIgnoreCase(test.toString());
        } catch (Exception e) {
            LOG.warn(".isLogEnabled()", e);
            return true;
        }
    }

    public static void disableLogForEvent() {
        Contexts.getEventContext().set(ENABLE_LOG_VAR_NAME, "false");
    }

    public static void enableLog() {
        Contexts.getEventContext().set(ENABLE_LOG_VAR_NAME, "true");
    }

}
