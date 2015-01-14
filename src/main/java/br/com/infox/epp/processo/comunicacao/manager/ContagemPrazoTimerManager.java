package br.com.infox.epp.processo.comunicacao.manager;

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
@Name(ContagemPrazoTimerManager.NAME)
public class ContagemPrazoTimerManager {

	private static final String DEFAULT_CRON_EXPRESSION = "0 0 0 * * ?";
	private static final LogProvider LOG = Logging.getLogProvider(ContagemPrazoTimerManager.class);
	public static final String NAME = "contagemPrazoTimerManager";
	public static final String ID_TIMER_CONTAGEM_PRAZO_COMUNICACAO = "idTimerContagemPrazoComunicacao";
	private static final Properties QUARTZ_PROPERTIES = ClassLoaderUtil.getProperties(QuartzConstant.QUARTZ_PROPERTIES);
	
	@In
	private ParametroManager parametroManager;

	@Observer(value = QuartzDispatcher.QUARTZ_DISPATCHER_INITIALIZED_EVENT)
	@Transactional
	public void init() {
		if (!Boolean.parseBoolean(QUARTZ_PROPERTIES.getProperty(QuartzConstant.QUARTZ_TIMER_ENABLED))) {
            return;
        }
		initContagemPrazoTimer();
	}
	
	private void initContagemPrazoTimer() {
		try {
			String idBloqueioUsuarioTimer = getParametro(ID_TIMER_CONTAGEM_PRAZO_COMUNICACAO);
			if (idBloqueioUsuarioTimer == null) {
				createTimerInstance(DEFAULT_CRON_EXPRESSION, ID_TIMER_CONTAGEM_PRAZO_COMUNICACAO, "ID do Timer de contagem de prazo da comunicação");
			}
		} catch (SchedulerException | DAOException e) {
			LOG.error(".initContagemPrazoTimer", e);
		}
	}
	
	private void createTimerInstance(String cronExpression, String idQuatzTriggerParamName, String description) throws SchedulerException, DAOException {
		ContagemPrazoProcessor processor = ComponentUtil.getComponent(ContagemPrazoProcessor.NAME);
    	QuartzTriggerHandle handle = processor.processContagemPrazoComunicacao(cronExpression);
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
