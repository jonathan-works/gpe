/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.listener;

import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.ibpm.entity.log.ExecuteLog;
import br.com.infox.ibpm.entity.log.LogUtil;
import br.com.infox.type.TipoOperacaoLogEnum;


public class LogEventListener implements PostUpdateEventListener, 
		PostInsertEventListener, PostDeleteEventListener {

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
		Context eventContext = Contexts.getEventContext();
		if (eventContext == null) {
			return false;
		}
		Object test = eventContext.get(ENABLE_LOG_VAR_NAME);
		try {
			return test == null || test.toString().equalsIgnoreCase("true");
		} catch (Exception e) {
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