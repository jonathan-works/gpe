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
 * @author Jo�o Paulo Lacerda
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