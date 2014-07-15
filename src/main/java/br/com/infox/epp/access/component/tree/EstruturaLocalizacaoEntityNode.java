package br.com.infox.epp.access.component.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.component.tree.bean.EstruturaLocalizacaoBean;
import br.com.infox.epp.access.component.tree.bean.EstruturaLocalizacaoBean.Tipo;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.seam.util.ComponentUtil;

public class EstruturaLocalizacaoEntityNode extends EntityNode<EstruturaLocalizacaoBean> {
    
    private static final long serialVersionUID = 1L;

    public EstruturaLocalizacaoEntityNode(EntityNode<EstruturaLocalizacaoBean> parent, EstruturaLocalizacaoBean entity, String[] queryChildrenList) {
        super(parent, entity, queryChildrenList);
    }
    
    public EstruturaLocalizacaoEntityNode(String queryChildren) {
        super(queryChildren);
    }
    
    public EstruturaLocalizacaoEntityNode(String[] queryChildrenList) {
        super(queryChildrenList);
    }
    
    @Override
    protected List<EstruturaLocalizacaoBean> getChildrenList(String hql, EstruturaLocalizacaoBean entity) {
        Map<String, Object> parameters = new HashMap<>();
        GenericDAO genericDAO = ComponentUtil.getComponent(GenericDAO.NAME);
        Object parent;
        if (entity.getTipo() == Tipo.L) {
            parent = genericDAO.find(Localizacao.class, entity.getId());
        } else {
            parent = genericDAO.find(Estrutura.class, entity.getId());
        }
        parameters.put(PARENT_NODE, parent);
        return genericDAO.getResultList(hql, parameters);
    }
    
    @Override
    protected EntityNode<EstruturaLocalizacaoBean> createRootNode(EstruturaLocalizacaoBean n) {
        return new EstruturaLocalizacaoEntityNode(null, n, getQueryChildrenList());
    }
    
    @Override
    protected EntityNode<EstruturaLocalizacaoBean> createChildNode(EstruturaLocalizacaoBean n) {
        return new EstruturaLocalizacaoEntityNode(this, n, getQueryChildrenList());
    }
}
