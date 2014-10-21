package br.com.infox.epp.processo.home;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.ibpm.node.InfoxMailNode;
import br.com.infox.ibpm.util.JbpmUtil;

@Name(AutomaticNodeRetryProcessor.NAME)
@AutoCreate
public class AutomaticNodeRetryProcessor {
    public static final String NAME = "automaticNodeRetryProcessor";
    
    @Asynchronous
    @Transactional
    public QuartzTriggerHandle retryAutomaticNodes(@IntervalCron String cron) {
        List<Token> tokens = JbpmUtil.getTokensOfAutomaticNodesNotEnded();
        for (Token token : tokens) {
            Node node = (Node) HibernateUtil.removeProxy(token.getNode());
            if (node instanceof InfoxMailNode) {
                ExecutionContext executionContext = new ExecutionContext(token);
                node.execute(executionContext);
            } else {
                token.signal(); // Sistema
            }
        }
        return null;
    }
}
