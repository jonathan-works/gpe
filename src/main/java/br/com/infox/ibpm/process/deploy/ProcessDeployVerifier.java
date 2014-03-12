package br.com.infox.ibpm.process.deploy;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.transaction.UserTransaction;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.xml.sax.InputSource;

import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.seam.util.ComponentUtil;

/**
 * Componente responsavel por verificar se os fluxos da aplicação já foram
 * publicados no jBPM
 * 
 * @author luizruiz
 * 
 */

@Name(ProcessDeployVerifier.NAME)
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Install(dependencies = "org.jboss.seam.bpm.jbpm")
@Startup(depends = "org.jboss.seam.bpm.jbpm")
public class ProcessDeployVerifier {

    public static final String NAME = "processDeployVerifier";
    private static final LogProvider LOG = Logging.getLogProvider(ProcessDeployVerifier.class);

    @SuppressWarnings(UNCHECKED)
    @Create
    public void init() {
        long time = new Date().getTime();
        UserTransaction transaction = Transaction.instance();
        try {
            if (!transaction.isActive()) {
                transaction.begin();
            } else {
                transaction = null;
            }
        } catch (Exception e) {
            LOG.error(".init()", e);
        }
        GraphSession graphSession = JbpmUtil.getGraphSession();
        List<ProcessDefinition> definitions = graphSession.findLatestProcessDefinitions();
        List<String> processNames = new ArrayList<String>();
        for (ProcessDefinition p : definitions) {
            processNames.add(p.getName());
        }
        FluxoManager fluxoManager = ComponentUtil.getComponent(FluxoManager.NAME);
        List<Fluxo> list = fluxoManager.findAll();
        for (Fluxo f : list) {
            if (verify(processNames, f)) {
                ProcessDefinition instance = parseInstance(f.getXml());
                instance.setName(f.getFluxo());
                graphSession.deployProcessDefinition(instance);
                JbpmUtil.getJbpmSession().flush();
                Events.instance().raiseEvent(ProcessBuilder.POST_DEPLOY_EVENT, instance);
                LOG.info(MessageFormat.format("Publicando fluxo {0}", f.getFluxo()));
            }
        }
        if (transaction != null) {
            try {
                transaction.commit();
            } catch (Exception e) {
                LOG.error(".init()", e);
            }
        }
        LOG.info(MessageFormat.format("Tempo de publicacao: {0}", new Date().getTime()
                - time));
    }

    private boolean verify(List<String> processNames, Fluxo f) {
        return estaDisponivel(f) && possuiXmlValido(f)
                && (!processNames.contains(f.getFluxo()));
    }

    private boolean estaDisponivel(Fluxo f) {
        return f.getAtivo() && f.getPublicado();
    }

    private boolean possuiXmlValido(Fluxo f) {
        return f.getXml() != null && !"".equals(f.getXml());
    }

    private ProcessDefinition parseInstance(String xml) {
        StringReader stringReader = new StringReader(xml);
        InfoxJpdlXmlReader jpdlReader = new InfoxJpdlXmlReader(new InputSource(stringReader));
        return jpdlReader.readProcessDefinition();
    }
}
