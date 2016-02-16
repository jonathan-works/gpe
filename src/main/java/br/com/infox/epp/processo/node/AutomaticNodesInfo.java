package br.com.infox.epp.processo.node;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.ibpm.node.InfoxMailNode;
import br.com.infox.ibpm.node.NodeType;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(AutomaticNodesInfo.NAME)
@AutoCreate
@Scope(ScopeType.PAGE)
@Transactional
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
                node.setNumeroProcesso(processoManager.getNumeroProcessoByIdJbpm(token.getProcessInstance().getRoot().getId()));
                node.setTokenId(token.getId());
                node.setNodeType(jbpmNode instanceof InfoxMailNode ? NodeType.M.getLabel() : NodeType.N.getLabel());
                nodes.add(node);
            }
        }
        return nodes;
    }
    
    @Transactional
    public void executeNode(Long tokenId) {
        String nodeName = null;
        long processId = 0;
        try {
            Token token = jbpmContext.getTokenForUpdate(tokenId);
            Node node = (Node) HibernateUtil.removeProxy(token.getNode());
            nodeName = node.getName();
            processId = token.getProcessInstance().getId();
            node.execute(new ExecutionContext(token));
        } catch (Exception e) {
            LOG.error(MessageFormat.format("Nó: {0}\tProcesso Jbpm: {1}", nodeName, processId), e);
            FacesMessages.instance().add("Erro ao executar nó");
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
