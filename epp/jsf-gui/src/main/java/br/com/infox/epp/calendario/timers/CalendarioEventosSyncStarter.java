package br.com.infox.epp.calendario.timers;

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
import br.com.infox.seam.util.ComponentUtil;

@AutoCreate
@Scope(ScopeType.STATELESS)
@Name(CalendarioEventosSyncStarter.NAME)
public class CalendarioEventosSyncStarter {
    private static final String DEFAULT_CRON_EXPRESSION = "0 0 0 * * ?";
    private static final LogProvider LOG = Logging.getLogProvider(CalendarioEventosSyncStarter.class);
    public static final String NAME = "CalendarioEventosSyncStarter";
    public static final String ID_TIMER_ATUALIZAR_CALENDARIO_FUTURO = "idTimerAtualizarCalendarioFuturo";
    private static final Properties QUARTZ_PROPERTIES = ClassLoaderUtil.getProperties(QuartzConstant.QUARTZ_PROPERTIES);
    @In
    private ParametroManager parametroManager;
    @Observer(value = QuartzDispatcher.QUARTZ_DISPATCHER_INITIALIZED_EVENT)
    @Transactional
    public void init() {
        if (!Boolean.parseBoolean(QUARTZ_PROPERTIES.getProperty(QuartzConstant.QUARTZ_TIMER_ENABLED))) {
            return;
        }
        initTimer();
    }

    private void initTimer() {
        try {
            String idBloqueioUsuarioTimer = getParametro(ID_TIMER_ATUALIZAR_CALENDARIO_FUTURO);
            if (idBloqueioUsuarioTimer == null) {
                createTimerInstance(DEFAULT_CRON_EXPRESSION, ID_TIMER_ATUALIZAR_CALENDARIO_FUTURO,
                        "ID do Timer que cria datas de eventos futuros para calendários periódicos");
            }
        } catch (SchedulerException | DAOException e) {
            LOG.error(".initTimer", e);
        }
    }

    private void createTimerInstance(String cronExpression, String idQuatzTriggerParamName, String description)
            throws SchedulerException, DAOException {
        CalendarioEventosSyncProcessor processor = ComponentUtil.getComponent(CalendarioEventosSyncProcessor.NAME);
        QuartzTriggerHandle handle = processor.processUpdateCalendarioSync(cronExpression);
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
