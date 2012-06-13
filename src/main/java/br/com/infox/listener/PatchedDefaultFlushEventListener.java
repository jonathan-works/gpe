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

import org.hibernate.HibernateException;
import org.hibernate.event.EventSource;
import org.hibernate.event.def.DefaultFlushEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Patche para o Hibernate issue:
 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-2763
 *
 * TODO: Remover este patch quando HHH-2763 for resolvido.
 *
 * @author João Paulo Lacerda
 */
public class PatchedDefaultFlushEventListener extends DefaultFlushEventListener{

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(PatchedDefaultFlushEventListener.class);

    @Override
    protected void performExecutions(EventSource session) throws HibernateException {
    	log.trace("executing flush");
    	session.getPersistenceContext().setFlushing(true);
        try {
            session.getJDBCContext().getConnectionManager().flushBeginning();
            // we need to lock the collection caches before
            // executing entity inserts/updates in order to
            // account for bidi associations
            session.getActionQueue().prepareActions();
            session.getActionQueue().executeActions();
        }
        catch (HibernateException he) {
            log.error("Could not synchronize database state with session", he);
            throw he;
        }
        finally {
            session.getPersistenceContext().setFlushing(false);
            session.getJDBCContext().getConnectionManager().flushEnding();
        }
    }
}