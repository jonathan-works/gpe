package br.com.infox.epp.processo.home;

import java.util.Date;

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
import br.com.infox.quartz.QuartzConstant;

@Name(AutomaticNodeRetryProcessorStarter.NAME)
@Scope(ScopeType.STATELESS)
@AutoCreate
public class AutomaticNodeRetryProcessorStarter {
    public static final String NAME = "automaticNodeRetryProcessorStarter";
    
    public static final String CRON = "0 0/30 * * * ?";
    public static final String AUTOMATIC_NODE_RETRY_PROCESSOR_PARAMETER = "idAutomaticNodeRetryProcessorTimer";
    
    @In
    private ParametroManager parametroManager;
    
    @In
    private AutomaticNodeRetryProcessor automaticNodeRetryProcessor;
    
    @Observer(value = QuartzDispatcher.QUARTZ_DISPATCHER_INITIALIZED_EVENT)
    @Transactional
    public void createProcessor() throws SchedulerException, DAOException {
        if (!Boolean.parseBoolean(ClassLoaderUtil.getProperties(QuartzConstant.QUARTZ_PROPERTIES).getProperty(QuartzConstant.QUARTZ_TIMER_ENABLED))) {
            return;
        }
        
        if (!parametroManager.existeParametro(AUTOMATIC_NODE_RETRY_PROCESSOR_PARAMETER)) {
            Parametro parametro = new Parametro();
            parametro.setAtivo(true);
            parametro.setDataAtualizacao(new Date());
            parametro.setDescricaoVariavel("ID do timer do processor que executa novamente os nós automáticos parados");
            parametro.setSistema(true);
            parametro.setNomeVariavel(AUTOMATIC_NODE_RETRY_PROCESSOR_PARAMETER);
            
            QuartzTriggerHandle handle = automaticNodeRetryProcessor.retryAutomaticNodes(CRON);
            
            parametro.setValorVariavel(handle.getTrigger().getKey().getName());
            
            parametroManager.persist(parametro);
        }
    }
}
