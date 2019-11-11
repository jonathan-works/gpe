package br.com.infox.epp.processo.node;

import br.com.infox.ibpm.node.NodeType;

public class NodeBean {

    private String nodeName;
    private String numeroProcesso;
    private Long tokenId;
    private String nodeType;
    private String processName;

    public NodeBean(Long tokenId, String nodeName, char nodeClass, String numeroProcesso, String processName) {
        this.tokenId = tokenId;
        this.nodeName = nodeName;
        this.nodeType = NodeType.valueOf(Character.toString(nodeClass)).getLabel();
        this.numeroProcesso = numeroProcesso;
        this.processName = processName;
    }

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

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}
