package br.com.infox.epp.processo.timer;

import java.util.Date;
import java.util.Properties;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.async.QuartzDispatcher;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jbpm.util.ClassLoaderUtil;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.quartz.QuartzConstant;
import br.com.infox.seam.util.ComponentUtil;

@Name(TaskExpirationTimerManager.NAME)
@AutoCreate
public class TaskExpirationTimerManager {
    private static final String PARAMETER_DESCRIPTION = "ID do Timer de verificação da data de expiração da tarefa";
    static final String NAME = "taskExpirationTimerManager";
    private static final String DEFAULT_CRON_EXPRESSION = "0 0 0 * * ?";
    private static final LogProvider LOG = Logging.getLogProvider(TaskExpirationTimerManager.class);
    public static final String ID_TIMER_TASK_EXPIRATION = "idTimerTaskExpiration";
    private static final Properties QUARTZ_PROPERTIES = ClassLoaderUtil.getProperties(QuartzConstant.QUARTZ_PROPERTIES);

    @In
    private ParametroManager parametroManager;
    
    @Observer(value = QuartzDispatcher.QUARTZ_DISPATCHER_INITIALIZED_EVENT)
    @Transactional
    public void init() {
        if (!Boolean.parseBoolean(QUARTZ_PROPERTIES.getProperty(QuartzConstant.QUARTZ_TIMER_ENABLED))) {
            return;
        }
        initTimerTaskExpiration();
    }

    private void initTimerTaskExpiration() {
        try {
            String idTaskExpirationTimer = getParametro(ID_TIMER_TASK_EXPIRATION);
            if (idTaskExpirationTimer == null) {
                createTimerInstance(DEFAULT_CRON_EXPRESSION, ID_TIMER_TASK_EXPIRATION, PARAMETER_DESCRIPTION);
            }
        } catch (SchedulerException | DAOException e) {
            LOG.error(".initTimerTaskExpiration()", e);
        }
    }
    
    public void createTimerInstance(String cronExpression,
            String idTaskExpirationTimerParameter, String description) throws SchedulerException, DAOException {
        TaskExpirationProcessor processor = ComponentUtil.getComponent(TaskExpirationProcessor.NAME);
        QuartzTriggerHandle handle = processor.processTaskExpiration(cronExpression);
        Trigger trigger = handle.getTrigger();
        saveSystemParameter(idTaskExpirationTimerParameter, trigger.getKey().getName(), description);
    }

    private void saveSystemParameter(String nome, String valor, String descricao) throws DAOException {
        Parametro p = new Parametro();
        p.setNomeVariavel(nome);
        p.setValorVariavel(valor);
        p.setDescricaoVariavel(descricao);
        p.setDataAtualizacao(new Date());
        p.setSistema(true);
        p.setAtivo(true);
        parametroManager.persist(p);
    }
    
    public String getParametro(String nome) {
        Parametro parametro = parametroManager.getParametro(nome);
        return parametro != null ? parametro.getValorVariavel() : null;
    }
}
