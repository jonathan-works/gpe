package br.com.infox.epp.entrega.modelo.quartz;

import java.util.Date;
import java.util.Properties;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.async.QuartzDispatcher;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jbpm.util.ClassLoaderUtil;
import org.quartz.SchedulerException;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.quartz.QuartzConstant;

@Name(AgendaEntregaTimerStarter.NAME)
@Scope(ScopeType.STATELESS)
@AutoCreate
public class AgendaEntregaTimerStarter {
    private static final String CRON_EXPRESSION = "0 0 18 1/1 * ? *";
    private static final LogProvider LOG = Logging.getLogProvider(AgendaEntregaTimerStarter.class);
    public static final String NAME = "agendaEntregaTimerStarter";
    private static final Properties QUARTZ_PROPERTIES = ClassLoaderUtil.getProperties(QuartzConstant.QUARTZ_PROPERTIES);

    @In 
    private ParametroManager parametroManager;
    @In 
    private AgendaEntregaTimerProcessor agendaEntregaTimerProcessor;

    @Observer(value = QuartzDispatcher.QUARTZ_DISPATCHER_INITIALIZED_EVENT)
    @Transactional
    public void create() {
        if (!Boolean.parseBoolean(QUARTZ_PROPERTIES.getProperty(QuartzConstant.QUARTZ_TIMER_ENABLED))) {
            return;
        }
        initTimer();
    }

    private void initTimer() {
        try {
            String idAgendaEntregaTimer = getParametro("idTimerAgendaEntrega");
            if (idAgendaEntregaTimer == null) {
                createTimerInstance(CRON_EXPRESSION, "idTimerAgendaEntrega",
                        "ID do Timer que lança eventos de agenda de entrega");
            }
        } catch (SchedulerException | DAOException e) {
            LOG.error(".initTimer", e);
        }
    }

    private void createTimerInstance(String cronExpression, String idQuatzTriggerParamName, String description)
            throws SchedulerException, DAOException {
        QuartzTriggerHandle handle = agendaEntregaTimerProcessor.processAgendaEntregaSync(cronExpression);
        String triggerName = handle.getTrigger().getKey().getName();
        createAndSaveParameter(idQuatzTriggerParamName, triggerName, description);
    }

    private void createAndSaveParameter(String nome, String valor, String descricao) throws DAOException {
        Parametro parametro = new Parametro();
        parametro.setNomeVariavel(nome);
        parametro.setValorVariavel(valor);
        parametro.setDescricaoVariavel(descricao);
        parametro.setDataAtualizacao(new Date());
        parametro.setSistema(true);
        parametro.setAtivo(true);
        parametroManager.persist(parametro);
    }

    public String getParametro(String nome) {
        Parametro parametro = parametroManager.getParametro(nome);
        return parametro != null ? parametro.getValorVariavel() : null;
    }

}
