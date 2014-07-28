package br.com.infox.epp.access.component.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.seam.util.ComponentUtil;

/**
 * EntityNode utilizado pela {@link EstruturaLocalizacaoTreeHandler}
 * @author gabriel
 *
 */
public class EstruturaLocalizacaoEntityNode extends EntityNode<Object> {
    
    private static final long serialVersionUID = 1L;

    private String queryChildrenOfLocalizacaoList;
    private String queryRootsOfEstruturaList;
    
    public EstruturaLocalizacaoEntityNode(EntityNode<Object> parent, Object entity, String[] queryChildrenList) {
        super(parent, entity, queryChildrenList);
        queryChildrenOfLocalizacaoList = queryChildrenList[0] + " and o.localizacaoPai = :" + EntityNode.PARENT_NODE;
        queryRootsOfEstruturaList = queryChildrenList[0] + " and o.localizacaoPai is null";
    }
    
    public EstruturaLocalizacaoEntityNode(String queryChildren) {
        super(queryChildren);
        queryChildrenOfLocalizacaoList = queryChildren + " and o.localizacaoPai = :" + EntityNode.PARENT_NODE;
        queryRootsOfEstruturaList = queryChildren + " and o.localizacaoPai is null";
    }
    
    public EstruturaLocalizacaoEntityNode(String[] queryChildrenList) {
        super(queryChildrenList);
        queryChildrenOfLocalizacaoList = queryChildrenList[0] + " and o.localizacaoPai = :" + EntityNode.PARENT_NODE + 
                " order by o.caminhoCompleto";
        queryRootsOfEstruturaList = queryChildrenList[0] + " and o.localizacaoPai is null order by o.caminhoCompleto";
    }
    
    @Override
    protected List<Object> getChildrenList(String hql, Object entity) {
        Map<String, Object> parameters = new HashMap<>();
        String query;
        if (entity instanceof Localizacao) { // O pai é uma localização, trago suas localizações filhas
            parameters.put(PARENT_NODE, entity);
            query = queryChildrenOfLocalizacaoList;
        } else {
            query = queryRootsOfEstruturaList; // O pai é uma estrutura, trago as localizações raízes da estrutura
        }
        GenericDAO genericDAO = ComponentUtil.getComponent(GenericDAO.NAME);
        return genericDAO.getResultList(query, parameters);
    }
    
    @Override
    protected EntityNode<Object> createRootNode(Object n) {
        return new EstruturaLocalizacaoEntityNode(null, n, getQueryChildrenList());
    }
    
    @Override
    protected EntityNode<Object> createChildNode(Object n) {
        return new EstruturaLocalizacaoEntityNode(this, n, getQueryChildrenList());
    }
}
