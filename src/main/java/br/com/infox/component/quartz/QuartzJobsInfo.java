package br.com.infox.component.quartz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.async.QuartzDispatcher;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import br.com.infox.ibpm.entity.Parametro;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name("quartzJobsInfo")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Startup(depends="org.jboss.seam.async.dispatcher")
@Install(dependencies={"org.jboss.seam.async.dispatcher"})
public class QuartzJobsInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static Pattern patternExpr = Pattern.compile("^AsynchronousInvocation\\((.*)\\)$");
	
	public static Scheduler getScheduler() {
		return QuartzDispatcher.instance().getScheduler();
	}
	
	public List<Map<String, Object>> getDetailJobsInfo() {
		List<Map<String, Object>> maps = new ArrayList<Map<String,Object>>();
		try {
			Scheduler scheduler = getScheduler();
			String[] jobGroupNames = scheduler.getJobGroupNames();
			for (String groupName : jobGroupNames) {
				List<Map<String,Object>> mapInfoGroup = getListMapInfoGroupFromJobs(groupName);
				maps.addAll(mapInfoGroup);
			}
		} catch (SchedulerException e) {
			FacesMessages.instance().add(Severity.ERROR, "Erro ao obter os detalhes dos jobs do quartz.", e);
		}
		return maps;
	}

	private List<Map<String, Object>> getListMapInfoGroupFromJobs(String groupName)
			throws SchedulerException {
		Scheduler scheduler = getScheduler();
		String[] jobNames = scheduler.getJobNames(groupName);
		List<Map<String, Object>> maps = new ArrayList<Map<String,Object>>();
		for (String jobName : jobNames) {
			JobDetail jobDetail = scheduler.getJobDetail(jobName, groupName);
			Trigger[] triggersOfJob = scheduler.getTriggersOfJob(jobName, groupName);
			for (Trigger trigger : triggersOfJob) {
				maps.add(getTrigerDetailMap(jobDetail, trigger));
			}
		}
		return maps;
	}	
	
	private Map<String,Object> getTrigerDetailMap(JobDetail jobDetail, Trigger trigger) {
		Map<String,Object> map = new HashMap<String, Object>();
		String jobName = trigger.getJobName();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		map.put("triggerName", trigger.getName());
		map.put("jobName", jobName);
		map.put("groupName", jobDetail.getGroup());
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
			} else {
				return dataJobDetail;
			}
		}
		return null;
	}
	
	
	/**
	 * Test se o a expressão do job é valida.
	 * @param jobExpression
	 * @return
	 */
	private boolean isJobValid(String jobExpression) {
		if (jobExpression == null || jobExpression.indexOf(".") == -1) {
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
			component.getClass().getDeclaredMethod(medothName, Date.class, String.class);
			return true;
		} catch (Exception e) { /* not found */ }
		try {
			component.getClass().getDeclaredMethod(medothName, String.class);
			return true;
		} catch (Exception e) { /* not found */ }	
		return false;
	}

	public void triggerJob(String jobName, String groupName) {
		try {
			getScheduler().triggerJob(jobName, groupName);
			FacesMessages.instance().add(Severity.INFO, "Job executado com sucesso: " + jobName);
		} catch (SchedulerException e) {
			FacesMessages.instance().add(Severity.ERROR, "Erro ao executar job " + jobName, e);
			e.printStackTrace();
		}
	}
	
	public void deleteJob(String jobName, String groupName) {
		try {
			getScheduler().deleteJob(jobName, groupName);
			FacesMessages.instance().add(Severity.INFO, "Job removido com sucesso: " + jobName);
		} catch (SchedulerException e) {
			FacesMessages.instance().add(Severity.ERROR, "Erro ao remover job " + jobName, e);
			e.printStackTrace();
		}
	}	
	
	@Create
	public void addGlobalTriggerListener() throws SchedulerException {
		Scheduler scheduler = QuartzJobsInfo.getScheduler();
		if (scheduler.getGlobalTriggerListeners().isEmpty()) {
			scheduler.addGlobalTriggerListener(new TriggerListenerLog());
		}
	}
	
	public void apagarJobs() {
		String sql = "delete from qrtz_cron_triggers; " +
			"delete from qrtz_fired_triggers; " +
			"delete from qrtz_triggers; " +
			"delete from qrtz_job_details; " +
			"delete from core.tb_parametro " + 
			"where vl_variavel like '________:___________:_____';";
		Query query = EntityUtil.getEntityManager().createNativeQuery(sql);
		query.executeUpdate();
		FacesMessages.instance().add(Severity.INFO, "Jobs apagados com sucesso. Reinicie o servidor para que os Jobs sejam refeitos.");
	}
	
	public void apagarHistoricoEstatisticaEventoProcesso() {
		String hql = "delete from HistoricoEstatisticaEventoProcesso o where cast(o.dtUltimaAtualizacao as date) = current_date";
		FacesMessages.instance().add(Severity.INFO, "HistoricoEstatisticaEventoProcesso para o dia atual foi apagado com sucesso.");
		EntityUtil.createQuery(hql).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMapParametroTriggers() throws SchedulerException {
		List<String> triggersNames = getTriggersNames();
		if (triggersNames.isEmpty()) {
			return Collections.emptyList();
		}
		String hql = "select new map(o.nomeVariavel as nomeVariavel, " +
				"o.descricaoVariavel as descricaoVariavel, " +
				"o.valorVariavel as valorVariavel, " +
				"o.idParametro as idParametro," +
				"case when o.valorVariavel in (:triggersNames) then true else false end as valido) " +
				"from Parametro o where o.valorVariavel like '________:___________:_____'";
		Query query = EntityUtil.createQuery(hql);
		query.setParameter("triggersNames", triggersNames);
		return query.getResultList();
	}
	
	public void removeParametro(int idParametro) {
		EntityManager em = EntityUtil.getEntityManager();
		Parametro parametro = em.find(Parametro.class, idParametro);
		em.remove(parametro);
		em.flush();
	}
	
	private List<String> getTriggersNames() throws SchedulerException {
		Scheduler scheduler = getScheduler();
		String[] groupNames = scheduler.getTriggerGroupNames();
		List<String> list = new ArrayList<String>();
		for (String groupName : groupNames) {
			String[] triggerNames = scheduler.getTriggerNames(groupName);
			list.addAll(Arrays.asList(triggerNames));
		}
		return list;
	}
	
}
