package br.com.infox.epp.processo.node;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import br.com.infox.cdi.producer.JbpmContextProducer;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class AutomaticNodesInfo implements Serializable {
	
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(AutomaticNodesInfo.class);

    private List<NodeBean> nodes;
    
    public List<NodeBean> getNodesNotEnded() {
        if (nodes == null) {
            nodes = JbpmUtil.getNodeBeansOfAutomaticNodesNotEnded();
        }
        return nodes;
    }
    
    @Transactional
    public void executeNode(Long tokenId) {
        String nodeName = null;
        long processId = 0;
        try {
            Token token = JbpmContextProducer.getJbpmContext().getTokenForUpdate(tokenId);
            Node node = (Node) HibernateUtil.removeProxy(token.getNode());
            nodeName = node.getName();
            processId = token.getProcessInstance().getId();
            node.execute(new ExecutionContext(token));
        } catch (Exception e) {
            LOG.error(MessageFormat.format("Nó: {0}\tProcesso Jbpm: {1}", nodeName, processId), e);
            FacesMessages.instance().add("Erro ao executar nó: \n " + e.getMessage() );
        }
        this.nodes = null;
    }
}
