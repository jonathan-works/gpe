package br.com.infox.epp.processo.node;

import java.text.MessageFormat;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import br.com.infox.cdi.producer.JbpmContextProducer;
import br.com.infox.hibernate.util.HibernateUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AutomaticNodeService {

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void executeNode(Long idToken) {
        String nodeName = null;
        long processId = 0;
        try {
            Token token = JbpmContextProducer.getJbpmContext().getTokenForUpdate(idToken);
            Node node = (Node) HibernateUtil.removeProxy(token.getNode());
            nodeName = node.getName();
            processId = token.getProcessInstance().getId();
            node.execute(new ExecutionContext(token));
        } catch (Exception e) {
            throw new RuntimeException(MessageFormat.format("Nó: {0}\tProcesso Jbpm: {1}", nodeName, processId, e));
        }
    }
}
