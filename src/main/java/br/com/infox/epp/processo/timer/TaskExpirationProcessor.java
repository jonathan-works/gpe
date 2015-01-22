package br.com.infox.epp.processo.timer;

import java.util.Date;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.JbpmContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

@Name(TaskExpirationProcessor.NAME)
@AutoCreate
public class TaskExpirationProcessor {
    
    public static final String NAME = "taskExpirationProcessor";

    @Asynchronous
    @Transactional
    public QuartzTriggerHandle endTask(@Expiration Date expiration, TaskExpirationInfo info) {
        JbpmContext context = ManagedJbpmContext.instance();
        TaskInstance task = context.getTaskInstanceForUpdate(info.getTaskId());
        if (task != null && !task.hasEnded()) {
            task.end(info.getTransition());
        }
        return null;
    }
}
