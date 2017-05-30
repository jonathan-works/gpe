package br.com.infox.epp.tarefa.component.tree;

import java.util.ArrayList;
import java.util.List;

import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.painel.CaixaDefinitionBean;
import br.com.infox.epp.painel.PanelDefinition;
import br.com.infox.epp.painel.TaskDefinitionBean;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.painel.caixa.CaixaDAO;

public class PainelEntityNode extends EntityNode<PanelDefinition> {
	
    private static final long serialVersionUID = 1L;
    public static final String TASK_TYPE = "Task";
    public static final String CAIXA_TYPE = "Caixa";
    
    private String type;
    private boolean expanded = true;
    private List<PainelEntityNode> caixasEntityNode;
    
    public PainelEntityNode(PainelEntityNode parent, PanelDefinition panelDefinition, String type) {
        super(parent, panelDefinition, null);
        this.type = type;
    }
    
    public List<PainelEntityNode> getCaixas() {
        if (!isLeaf() && caixasEntityNode == null) {
            caixasEntityNode = new ArrayList<>();
            List<Caixa> caixas = getCaixaDAO().getCaixasByTaskKey(getEntity().getId().toString());
            TaskDefinitionBean taskDefinitionBean = (TaskDefinitionBean) getEntity();
            for (Caixa caixa : caixas) {
                CaixaDefinitionBean caixaDefinitionBean = taskDefinitionBean.getCaixaDefinitionBean(caixa.getIdCaixa());
                if (caixaDefinitionBean == null) {
                    caixaDefinitionBean = new CaixaDefinitionBean(caixa.getIdCaixa(), caixa.getNomeCaixa(), taskDefinitionBean);
                }
                PainelEntityNode painelEntityNode = new PainelEntityNode(this, caixaDefinitionBean, CAIXA_TYPE);
                caixasEntityNode.add(painelEntityNode);
            }
        }
        return caixasEntityNode;
    }
    
    @Override
    public String getType() {
        return type;
    }
    
	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	
	private CaixaDAO getCaixaDAO() {
	    return Beans.getReference(CaixaDAO.class);
	}
	
}
