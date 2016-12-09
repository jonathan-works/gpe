package br.com.infox.jbpm.application;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.Jbpm;
import org.jbpm.job.executor.EppJobExecutorThread;
import org.jbpm.job.executor.JobExecutor;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

/**
 * Componente responsavel por inicializar o serviço de Job utilizado pelos
 * componentes Timer do jBPM no projeto
 * 
 * @author luizruiz
 * 
 */

@Name(JobExecutorLauncher.NAME)
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Install(dependencies = "org.jboss.seam.bpm.jbpm", precedence = BUILT_IN)
@Startup(depends = "org.jboss.seam.bpm.jbpm")
public class JobExecutorLauncher {

    static final String NAME = "JobExecutorLauncher";
    private static final LogProvider LOG = Logging.getLogProvider(JobExecutorLauncher.class);

    @Create
    public void init() {
        try {
            JobExecutor jobExecutor = Jbpm.instance().getJbpmConfiguration().getJobExecutor();
            jobExecutor.setJobExecutorThread(EppJobExecutorThread.class);
            jobExecutor.start();
        } catch (RuntimeException e) {
            LOG.error(".init()", e);
        }
    }

}
