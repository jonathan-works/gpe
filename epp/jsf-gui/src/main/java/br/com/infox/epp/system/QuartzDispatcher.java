package br.com.infox.epp.system;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

@Startup
@Scope(ScopeType.APPLICATION)
@Name("org.jboss.seam.async.dispatcher")
@Install(value = true, precedence = Install.DEPLOYMENT)
@BypassInterceptors
public class QuartzDispatcher extends org.jboss.seam.async.QuartzDispatcher {

    private static final LogProvider log = Logging.getLogProvider(QuartzDispatcher.class);

    @Override
    @Observer("org.jboss.seam.postInitialization")
    public void initScheduler() throws SchedulerException {
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Configuration configuration = Configuration.getInstance();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("seam.quartz.properties");
        int schedulerDelay = configuration.isDesenvolvimento() ? 90 : 600;
        if (inputStream != null) {
            try {
                Properties properties = new Properties();
                properties.load(inputStream);
                configuration.configureQuartz(properties);
                schedulerFactory.initialize(properties);
            } catch (IOException e) {
                log.debug("", e);
            }
            log.debug("Found seam.quartz.properties file. Using it for Quartz config.");
        } else {
            schedulerFactory.initialize();
            log.warn("No seam.quartz.properties file. Using in-memory job store.");
        }

        scheduler = schedulerFactory.getScheduler();
        scheduler.startDelayed(schedulerDelay); // Wait for server completely starts
        Events.instance().raiseEvent(QUARTZ_DISPATCHER_INITIALIZED_EVENT);
    }

}
