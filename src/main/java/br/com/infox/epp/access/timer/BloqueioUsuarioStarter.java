package br.com.infox.epp.access.timer;

import java.util.Properties;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.async.QuartzDispatcher;
import org.jbpm.util.ClassLoaderUtil;
import org.quartz.SchedulerException;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.manager.BloqueioUsuarioTimerManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.quartz.QuartzConstant;

@Name(BloqueioUsuarioStarter.NAME)
@Scope(ScopeType.STATELESS)
@AutoCreate
public class BloqueioUsuarioStarter {

	private static final String DEFAULT_CRON_EXPRESSION = "0 0 0 * * ?";
	private static final LogProvider LOG = Logging.getLogProvider(BloqueioUsuarioStarter.class);
	public static final String NAME = "bloqueioUsuarioStarter";
	public static final String ID_TIMER_BLOQUEIO_USUARIO = "idTimerBloqueioUsuario";
	
	private static final Properties QUARTZ_PROPERTIES = ClassLoaderUtil.getProperties(QuartzConstant.QUARTZ_PROPERTIES);
	
	public BloqueioUsuarioStarter() {}
	
	@Observer(value = QuartzDispatcher.QUARTZ_DISPATCHER_INITIALIZED_EVENT)
	@Transactional
	public void init() {
		if (!Boolean.parseBoolean(QUARTZ_PROPERTIES.getProperty(QuartzConstant.QUARTZ_TIMER_ENABLED))) {
            return;
        }
		
		initTimerBloqueioUsuario();
	}

	private void initTimerBloqueioUsuario() {
		final BloqueioUsuarioTimerManager manager = (BloqueioUsuarioTimerManager) Component.getInstance(BloqueioUsuarioTimerManager.NAME);
		BloqueioUsuarioProcessor processor = (BloqueioUsuarioProcessor) Component.getInstance(BloqueioUsuarioProcessor.NAME);
		
		try {
			String idBloqueioUsuarioTimer = null;
			try {
				idBloqueioUsuarioTimer = manager.getParametro(ID_TIMER_BLOQUEIO_USUARIO);
			} catch (IllegalArgumentException e) {
				LOG.error("BloqueioUsuarioStarter.init()", e);
			}
			if (idBloqueioUsuarioTimer == null) {
				manager.createTimerInstance(DEFAULT_CRON_EXPRESSION, ID_TIMER_BLOQUEIO_USUARIO, "ID do Timer de desbloqueio de usuários", processor);
			}
		} catch (SchedulerException | DAOException e) {
			LOG.error(".initTimerBloqueioUsuario", e);
		}
	}
}
