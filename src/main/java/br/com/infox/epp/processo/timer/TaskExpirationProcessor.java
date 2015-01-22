package br.com.infox.epp.processo.timer;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

@Name(TaskExpirationProcessor.NAME)
@AutoCreate
public class TaskExpirationProcessor {
    
    public static final String NAME = "taskExpirationProcessor";

    @Asynchronous
    @Transactional
    public QuartzTriggerHandle processTaskExpiration(@IntervalCron String cron) {
//    public QuartzTriggerHandle endTask(@Expiration Date expiration, TaskExpirationInfo info) {
//        JbpmContext context = ManagedJbpmContext.instance();
//        TaskInstance task = context.getTaskInstanceForUpdate(info.getTaskId());
//        if (task != null && !task.hasEnded()) {
//            task.end(info.getTransition());
//        }
    return null;
    }
}
