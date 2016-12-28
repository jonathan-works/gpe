package br.com.infox.epp.processo.node;

import br.com.infox.ibpm.node.NodeType;

public class NodeBean {

    private String nodeName;
    private String numeroProcesso;
    private Long tokenId;
    private String nodeType;

    public NodeBean(Long tokenId, String nodeName, char nodeClass, String numeroProcesso) {
        this.tokenId = tokenId;
        this.nodeName = nodeName;
        this.nodeType = NodeType.valueOf(Character.toString(nodeClass)).getLabel();
        this.numeroProcesso = numeroProcesso;
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
}
