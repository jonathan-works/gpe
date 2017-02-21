package org.jbpm.job.executor;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.transaction.TransactionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.contexts.Lifecycle;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.JobSession;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.job.Job;
import org.jbpm.persistence.db.DbPersistenceService;
import org.jbpm.persistence.db.StaleObjectLogConfigurer;
import org.joda.time.DateTime;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.cdi.producer.JbpmContextProducer;
import br.com.infox.core.server.ApplicationServerService;

public class EppJobExecutorThread extends Thread implements Deactivable {

    private final JobExecutor jobExecutor;
    private volatile boolean active = true;

    public EppJobExecutorThread(String name, JobExecutor jobExecutor) {
        super(jobExecutor.getThreadGroup(), name);
        this.jobExecutor = jobExecutor;
    }

    public void run() {
        while (active) {
            // take on next job
            Job job = jobExecutor.getJob();
            // if an exception occurs, acquireJob() returns null
            if (job != null) {
                try {
                    executeJobAction(job);
                } catch (Exception | Error e) {
                    log.error("Erro ao executar job " + job.getId(),  e);
                }
            }
        }
        log.info(getName() + " leaves cyberspace");
    }

    private void executeJobAction(Job job) throws Exception, Error {
        Lifecycle.beginCall();
        try {
            executeJob(job);
        } catch (Exception e) {
            // save exception stack trace
            // if another exception occurs, it is not rethrown
            saveJobException(job, e);
            throw e;
        } catch (Error e) {
            // unlock job so it can be dispatched again
            // if another exception occurs, it is not rethrown
            unlockJob(job);
            throw e;
        } finally {
            Lifecycle.endCall();
        }
    }

    protected void executeJob(Job job) throws Exception {
        TransactionManager transactionManager = ApplicationServerService.instance().getTransactionManager();
        transactionManager.begin();
        JbpmContext jbpmContext = JbpmContextProducer.getJbpmContext();
        try {
            JobSession jobSession = jbpmContext.getJobSession();
            
            job = (Job) jbpmContext.getSession().merge(job);
            job.setException(null);
            // register process instance for automatic save
            // https://jira.jboss.org/browse/JBPM-1015
            ProcessInstance processInstance = job.getProcessInstance();
            jbpmContext.addAutoSaveProcessInstance(processInstance);
            
            // if job is exclusive, lock process instance
            if (job.isExclusive()) {
                jbpmContext.getGraphSession().lockProcessInstance(processInstance);
            } 
            
            if (log.isDebugEnabled()) log.debug("executing " + job);
            if (job.execute(jbpmContext)) {
                jobSession.deleteJob(job);
            }
            transactionManager.commit();
        } catch (Exception  | Error e) {
            transactionManager.rollback();
            throw e;
        }
    }

    private void saveJobException(Job job, Exception exception) throws Exception {
        TransactionManager transactionManager = ApplicationServerService.instance().getTransactionManager();
        transactionManager.begin();
        JbpmContext jbpmContext = JbpmContextProducer.getJbpmContext();
        // if this is a locking exception, keep it quiet
        if (DbPersistenceService.isLockingException(exception)) {
            StaleObjectLogConfigurer.getStaleObjectExceptionsLog().error("failed to execute " + job, exception);
        }
        try {
            // do not reattach existing job as it contains undesired updates
            jbpmContext.getSession().refresh(job);

            // print and save exception
            StringWriter out = new StringWriter();
            exception.printStackTrace(new PrintWriter(out));
            job.setException(out.toString());

            // unlock job so it can be dispatched again
            job.setLockOwner(null);
            job.setLockTime(null);
            job.setDueDate(DateTime.now().plusMinutes(1).toDate());
            transactionManager.commit();
        } catch (RuntimeException e) {
            log.warn("failed to save exception for " + job, e);
            transactionManager.rollback();
        } catch (Error e) {
            transactionManager.rollback();
            throw e;
        }
        // notify job executor
        synchronized (jobExecutor) {
            jobExecutor.notify();
        }
    }

    private void unlockJob(Job job) {
        JbpmContext jbpmContext = jobExecutor.getJbpmConfiguration().createJbpmContext();
        try {
            // do not reattach existing job as it contains undesired updates
            jbpmContext.getSession().refresh(job);

            // unlock job
            job.setLockOwner(null);
            job.setLockTime(null);
            if (job.getException() != null) {
                job.setRetries(job.getRetries() + 1);
            }
        } catch (RuntimeException e) {
            jbpmContext.setRollbackOnly();
            log.warn("failed to unlock " + job, e);
        } catch (Error e) {
            jbpmContext.setRollbackOnly();
            // do not rethrow as this method is already called in response to an
            // Error
            log.warn("failed to unlock " + job, e);
        } finally {
            try {
                jbpmContext.close();
            } catch (RuntimeException e) {
                log.warn("failed to unlock " + job, e);
            }
        }
        // notify job executor
        synchronized (jobExecutor) {
            jobExecutor.notify();
        }
    }

    public void deactivate() {
        if (active) {
            active = false;
            interrupt();
        }
    }

    private static final Log log = LogFactory.getLog(JobExecutorThread.class);

    /**
     * ==================== CRUFT! CRUFT! CRUFT! ====================
     */

    /**
     * @deprecated use {@link #JobExecutorThread(String, JobExecutor)} instead
     */
    public EppJobExecutorThread(String name, JobExecutor jobExecutor,
            JbpmConfiguration jbpmConfiguration, int idleInterval,
            int maxIdleInterval, long maxLockTime, int maxHistory) {
        super(jobExecutor.getThreadGroup(), name);
        this.jobExecutor = jobExecutor;
    }

    /**
     * @deprecated As of jBPM 3.2.3, replaced by {@link #deactivate()}
     */
    public void setActive(boolean isActive) {
        if (isActive == false)
            deactivate();
    }

}
