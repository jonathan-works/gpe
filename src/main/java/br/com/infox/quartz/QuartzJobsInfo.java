package br.com.infox.quartz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.async.QuartzDispatcher;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Name(QuartzJobsInfo.NAME)
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class QuartzJobsInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(QuartzJobsInfo.class);
    public static final String NAME = "quartzJobsInfo";

    @In
    private ParametroManager parametroManager;
    @In
    private InfoxMessages infoxMessages;

    private static Pattern patternExpr = Pattern.compile("^AsynchronousInvocation\\((.*)\\)$");

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
            FacesMessages.instance().add(Severity.ERROR, infoxMessages.get("quartz.error.retrieveData"), e);
        }
        return maps;
    }

    private List<Map<String, Object>> getListMapInfoGroupFromJobs(String groupName) throws SchedulerException {
        Scheduler scheduler = getScheduler();
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        for (JobKey jobKey : jobKeys) {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggersOfJob) {
                Map<String, Object> map = getTrigerDetailMap(jobDetail, trigger);
                if (map != null)
                    maps.add(map);
            }
        }
        return maps;
    }

    private Map<String, Object> getTrigerDetailMap(JobDetail jobDetail, Trigger trigger) {
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
            String cronExpression = cronTrigger.getCronExpression();
            if (cronExpression != null && cronExpression.trim().length() > 0)
                map.put("cronExpression", cronExpression);
            else
                return null;
        } else
            return null;

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

    private boolean isMethodValid(Object component, String methodName) {
        try {
            component.getClass().getDeclaredMethod(methodName, String.class);
            return true;
        } catch (Exception e) {
            LOG.error(".isMethodValid(component, medothName)", e);
        }
        try {
            component.getClass().getDeclaredMethod(methodName, Date.class, String.class);
            return true;
        } catch (Exception e) {
            LOG.error(".isMethodValid(component, medothName)", e);
        }
        return false;
    }

    @Transactional
    public void triggerJob(String jobName, String groupName) {
        try {
            String sql = "UPDATE QRTZ_TRIGGERS SET NEXT_FIRE_TIME = :nextFireTime WHERE JOB_NAME = :jobName AND JOB_GROUP = :groupName";
            Calendar nextFireTime = Calendar.getInstance();
            nextFireTime.roll(Calendar.MINUTE, -1);
            getEntityManager().createNativeQuery(sql).setParameter("nextFireTime", nextFireTime.getTime().getTime())
                    .setParameter("jobName", jobName).setParameter("groupName", groupName).executeUpdate();
            FacesMessages.instance().add(Severity.INFO, "Job executado com sucesso: " + jobName);
        } catch (Exception e) {
            FacesMessages.instance().add(Severity.ERROR, "Erro ao executar job " + jobName, e);
            LOG.error(".triggerJob()", e);
        }
    }

    public void deleteJob(String jobName, String groupName, String triggerName) {
        try {
            getScheduler().deleteJob(JobKey.jobKey(jobName, groupName));
            Parametro parametro = parametroManager.getParametroByValorVariavel(triggerName);
            if (parametro != null) {
                parametroManager.remove(parametro);
            }
            FacesMessages.instance().add(Severity.INFO, "Job removido com sucesso: " + jobName);
        } catch (SchedulerException | DAOException e) {
            FacesMessages.instance().add(Severity.ERROR, "Erro ao remover job " + jobName, e);
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

    public void apagarJobs() {
        List<Map<String, Object>> jobs = getDetailJobsInfo();

        for (Map<String, Object> job : jobs) {
            deleteJob((String) job.get("jobName"), (String) job.get("groupName"), (String) job.get("triggerName"));
        }
    }

    public EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
}
