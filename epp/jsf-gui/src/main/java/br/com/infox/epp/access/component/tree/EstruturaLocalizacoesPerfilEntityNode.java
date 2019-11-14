package br.com.infox.epp.access.component.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.seam.util.ComponentUtil;

public class EstruturaLocalizacoesPerfilEntityNode extends EntityNode<Object> {

    private static final long serialVersionUID = 1L;
    
    private String queryChildrenOfLocalizacaoList;
    private String queryRootsOfEstruturaList;

    public EstruturaLocalizacoesPerfilEntityNode(EntityNode<Object> parent,
            Object entity, String[] queryChildrenList) {
        super(parent, entity, queryChildrenList);
        queryChildrenOfLocalizacaoList = queryChildrenList[0] + " and n.localizacaoPai = :" + EntityNode.PARENT_NODE + " order by n.caminhoCompleto";
        queryRootsOfEstruturaList = queryChildrenList[0] + " and n.localizacaoPai is null and n.estruturaPai = :"+ EntityNode.PARENT_NODE + " order by n.caminhoCompleto ";
    }

    public EstruturaLocalizacoesPerfilEntityNode(String queryChildren) {
        super(queryChildren);
        queryChildrenOfLocalizacaoList = queryChildren + " and n.localizacaoPai = :" + EntityNode.PARENT_NODE;
        queryRootsOfEstruturaList = queryChildren + " and n.localizacaoPai is null and n.estruturaPai = :"+ EntityNode.PARENT_NODE;
    }
    
    public EstruturaLocalizacoesPerfilEntityNode(String[] queryChildrenList) {
        super(queryChildrenList);
        queryChildrenOfLocalizacaoList = queryChildrenList[0] + " and n.localizacaoPai = :" + EntityNode.PARENT_NODE + 
                " order by n.caminhoCompleto";
        queryRootsOfEstruturaList = queryChildrenList[0] + " and n.localizacaoPai is null and n.estruturaPai = :"+ EntityNode.PARENT_NODE +" order by n.caminhoCompleto";
    }
    
    @Override
    protected List<Object> getChildrenList(String hql, Object entity) {
        Map<String, Object> parameters = new HashMap<>();
        String query;
        parameters.put(PARENT_NODE, entity);
        if (entity instanceof Localizacao) { // O pai é uma localização, trago suas localizações filhas
            query = queryChildrenOfLocalizacaoList;
        } else {
            query = queryRootsOfEstruturaList; // O pai é uma estrutura, trago as localizações raízes da estrutura
        }
        GenericDAO genericDAO = ComponentUtil.getComponent(GenericDAO.NAME);
        return genericDAO.getResultList(query, parameters);
    }
    
    @Override
    protected EntityNode<Object> createRootNode(Object n) {
        return new EstruturaLocalizacoesPerfilEntityNode(null, n, getQueryChildrenList());
    }
    
    @Override
    protected EntityNode<Object> createChildNode(Object n) {
        return new EstruturaLocalizacoesPerfilEntityNode(this, n, getQueryChildrenList());
    }

}
