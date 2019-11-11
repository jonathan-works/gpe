package br.com.infox.index;

import javax.annotation.PostConstruct;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.Search;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.system.Configuration;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Startup
@Name("massiveReindexer")
@Scope(ScopeType.APPLICATION)
public class MassiveReindexer {

	private static final LogProvider LOG = Logging.getLogProvider(MassiveReindexer.class);
	
	@PostConstruct
	public void init() {
		if (canExecute()) {
			TransactionManager transactionManager = ApplicationServerService.instance().getTransactionManager();
			try {
				transactionManager.setTransactionTimeout(10800);
				transactionManager.begin();
				execute();
				transactionManager.commit();
			} catch (Exception exception) {
				LOG.error("", exception);
				try {
					transactionManager.rollback();
				} catch (IllegalStateException | SecurityException | SystemException e) {
					LOG.error("Error rollback", exception);
				}
			}
		}
	}
	
	private boolean canExecute() {
	    Configuration configuration = Configuration.getInstance();
		String valor = getParametroManager().getValorParametro(Parametros.ATIVAR_MASSIVE_REINDEX.getLabel());
		return !configuration.isDesenvolvimento() && valor != null && "true".equals(valor.trim());
	}

	private void execute() throws InterruptedException {
		Session session = HibernateUtil.getSession();
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		fullTextSession.getTransaction().setTimeout(90000);
		MassIndexer massIndexer = fullTextSession.createIndexer()
				.batchSizeToLoadObjects(50)
				.cacheMode(CacheMode.IGNORE)
				.threadsToLoadObjects(5)
				.idFetchSize(150)
				.purgeAllOnStart(true)
				.typesToIndexInParallel(2);
		massIndexer.startAndWait();
	}
	
	private ParametroManager getParametroManager() {
		return Beans.getReference(ParametroManager.class);
	}

}
