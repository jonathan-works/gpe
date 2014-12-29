package br.com.infox.ibpm.node;
/**
 * Discriminadores da coluna class_ da tabela jbpm_node, de acordo com as informações do arquivo Node.hbm.xml 
 * em org/jbpm/graph/def no JAR jbpm-jpdl
 * @author gabriel
 *
 */
public enum NodeType {
    C("Subprocesso"), D("Decisão"), E("Término"), F("Separação"), J("Junção"), K("Tarefa"), M("Email"), N("Sistema"),
    R("Início"), S("State"), U("Super State");
    
    private String label;
    
    private NodeType(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}
