package br.com.infox.quartz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.async.QuartzDispatcher;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;

import br.com.infox.seam.util.ComponentUtil;

@Name(QuartzJobsInfo.NAME)
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@AutoCreate
public class QuartzJobsInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging
            .getLogProvider(QuartzJobsInfo.class);
    public static final String NAME = "quartzJobsInfo";

    private static Pattern patternExpr = Pattern
            .compile("^AsynchronousInvocation\\((.*)\\)$");

    public static Scheduler getScheduler() {
        return QuartzDispatcher.instance().getScheduler();
    }

    public List<Map<String, Object>> getDetailJobsInfo() {
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        try {
            Scheduler scheduler = getScheduler();
            List<String> jobGroupNames = scheduler.getJobGroupNames();
            for (String groupName : jobGroupNames) {
                List<Map<String, Object>> mapInfoGroup = getListMapInfoGroupFromJobs(groupName);
                maps.addAll(mapInfoGroup);
            }
        } catch (SchedulerException e) {
            FacesMessages.instance().add(Severity.ERROR,
                    Messages.instance().get("quartz.error.retrieveData"), e);
        }
        return maps;
    }

    private List<Map<String, Object>> getListMapInfoGroupFromJobs(
            String groupName) throws SchedulerException {
        Scheduler scheduler = getScheduler();
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        for (JobKey jobKey : jobKeys) {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggersOfJob) {
                maps.add(getTrigerDetailMap(jobDetail, trigger));
            }
        }
        return maps;
    }

    private Map<String, Object> getTrigerDetailMap(JobDetail jobDetail,
            Trigger trigger) {
        Map<String, Object> map = new HashMap<String, Object>();
        String jobName = trigger.getJobKey().getName();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        map.put("triggerName", trigger.getKey().getName());
        map.put("jobName", jobName);
        map.put("groupName", jobDetail.getKey().getGroup());
        map.put("nextFireTime", trigger.getNextFireTime());
        map.put("previousFireTime", trigger.getPreviousFireTime());
        String jobExpression = getJobExpression(jobDataMap);
        map.put("jobExpression", jobExpression);
        map.put("jobValid", isJobValid(jobExpression));
        if (trigger instanceof CronTrigger) {
            CronTrigger cronTrigger = (CronTrigger) trigger;
            map.put("cronExpression", cronTrigger.getCronExpression());
        }
        return map;
    }

    public static String getJobExpression(JobDataMap dataMap) {
        Collection<?> values = dataMap.values();
        if (values != null && !values.isEmpty()) {
            String dataJobDetail = values.iterator().next().toString();
            Matcher matcher = patternExpr.matcher(dataJobDetail);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return dataJobDetail;
        }
        return null;
    }

    /**
     * Test se o a expressão do job é valida.
     * 
     * @param jobExpression
     * @return
     */
    private boolean isJobValid(String jobExpression) {
        if (jobExpression == null || jobExpression.indexOf('.') == -1) {
            return false;
        }
        String[] split = jobExpression.split("\\.");
        String componentName = split[0];
        Object component = ComponentUtil.getComponent(componentName);
        if (component == null) {
            return false;
        }
        String medothName = split[1].replaceAll("[()]", "");
        return isMethodValid(component, medothName);
    }

    private boolean isMethodValid(Object component, String medothName) {
        try {
            component.getClass().getDeclaredMethod(medothName, Date.class,
                    String.class);
            return true;
        } catch (Exception e) {
            LOG.error(".isMethodValid(component, medothName)", e);
        }
        try {
            component.getClass().getDeclaredMethod(medothName, String.class);
            return true;
        } catch (Exception e) {
            LOG.error(".isMethodValid(component, medothName)", e);
        }
        return false;
    }

    public void triggerJob(String jobName, String groupName) {
        try {
            getScheduler().triggerJob(JobKey.jobKey(jobName, groupName));
            FacesMessages.instance().add(Severity.INFO,
                    "Job executado com sucesso: " + jobName);
        } catch (SchedulerException e) {
            FacesMessages.instance().add(Severity.ERROR,
                    "Erro ao executar job " + jobName, e);
            LOG.error(".triggerJob()", e);
        }
    }

    public void deleteJob(String jobName, String groupName) {
        try {
            getScheduler().deleteJob(JobKey.jobKey(jobName, groupName));
            FacesMessages.instance().add(Severity.INFO,
                    "Job removido com sucesso: " + jobName);
        } catch (SchedulerException e) {
            FacesMessages.instance().add(Severity.ERROR,
                    "Erro ao remover job " + jobName, e);
            LOG.error(".deleteJob()", e);
        }
    }

    @Observer(value = QuartzDispatcher.QUARTZ_DISPATCHER_INITIALIZED_EVENT)
    public void addGlobalTriggerListener() throws SchedulerException {
        Scheduler scheduler = QuartzJobsInfo.getScheduler();
        if (scheduler.getListenerManager().getTriggerListeners().isEmpty()) {
            scheduler.getListenerManager().addTriggerListener(new TriggerListenerLog());
        }
    }

}
