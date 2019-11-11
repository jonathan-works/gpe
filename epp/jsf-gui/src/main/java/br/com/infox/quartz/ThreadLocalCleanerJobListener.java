package br.com.infox.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import br.com.infox.cdi.producer.EntityManagerProducer;

public class ThreadLocalCleanerJobListener implements JobListener {
    
    @Override
    public String getName() {
        return ThreadLocalCleanerJobListener.class.getSimpleName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        EntityManagerProducer.clear();
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        EntityManagerProducer.clear();
    }
}
