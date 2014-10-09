package br.com.infox.epp.processo.home;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jbpm.graph.exe.Token;

import br.com.infox.ibpm.util.JbpmUtil;

@Name(SystemNodeRetryProcessor.NAME)
@AutoCreate
public class SystemNodeRetryProcessor {
    public static final String NAME = "systemNodeRetryProcessor";
    
    @Asynchronous
    @Transactional
    public QuartzTriggerHandle retrySystemNodes(@IntervalCron String cron) {
        List<Token> tokens = JbpmUtil.getTokensOfSystemNodesNotEnded();
        for (Token token : tokens) {
            token.signal();
        }
        return null;
    }
}
