/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
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