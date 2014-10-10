package br.com.infox.epp.processo.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.DelegationException;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.ibpm.node.InfoxMailNode;
import br.com.infox.ibpm.node.NodeType;
import br.com.infox.ibpm.util.JbpmUtil;

@Name(AutomaticNodesInfo.NAME)
@AutoCreate
@Scope(ScopeType.PAGE)
public class AutomaticNodesInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(AutomaticNodesInfo.class);

    public static final String NAME = "automaticNodesInfo";
    
    @In("org.jboss.seam.bpm.jbpmContext")
    private JbpmContext jbpmContext;
    
    @In
    private ProcessoManager processoManager;
    
    private List<NodeBean> nodes;
    
    public List<NodeBean> getNodesNotEnded() {
        if (nodes == null) {
            nodes = new ArrayList<>();
            List<Token> tokens = JbpmUtil.getTokensOfAutomaticNodesNotEnded();
            for (Token token : tokens) {
                NodeBean node = new NodeBean();
                Node jbpmNode = (Node) HibernateUtil.removeProxy(token.getNode());
                node.setNodeName(jbpmNode.getName());
                node.setNumeroProcesso(processoManager.getNumeroProcessoByIdJbpm(token.getProcessInstance().getId()));
                node.setTokenId(token.getId());
                node.setNodeType(jbpmNode instanceof InfoxMailNode ? NodeType.M.getLabel() : NodeType.N.getLabel());
                nodes.add(node);
            }
        }
        return nodes;
    }
    
    @Transactional
    public void executeNode(Long tokenId) {
        try {
            Token token = jbpmContext.getTokenForUpdate(tokenId);
            Node node = (Node) HibernateUtil.removeProxy(token.getNode());
            if (node instanceof InfoxMailNode) {
                ExecutionContext context = new ExecutionContext(token);
                node.execute(context);
            } else {
                token.signal(); // Sistema
            }
        } catch (Exception e) {
            LOG.error("", e);
            String mensagem = e instanceof DelegationException ? e.getCause().getMessage() : e.getMessage();
            FacesMessages.instance().add("Erro ao executar nó: " + mensagem);
        }
        this.nodes = null;
    }
    
    public static final class NodeBean {
        private String nodeName;
        private String numeroProcesso;
        private Long tokenId;
        private String nodeType;
        
        public String getNodeName() {
            return nodeName;
        }
        
        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }
        
        public String getNumeroProcesso() {
            return numeroProcesso;
        }
        
        public void setNumeroProcesso(String numeroProcesso) {
            this.numeroProcesso = numeroProcesso;
        }
        
        public Long getTokenId() {
            return tokenId;
        }
        
        public void setTokenId(Long tokenId) {
            this.tokenId = tokenId;
        }
        
        public String getNodeType() {
            return nodeType;
        }
        
        public void setNodeType(String nodeType) {
            this.nodeType = nodeType;
        }
    }
}
