package br.com.infox.epp.access.component.tree;

import java.util.List;

import br.com.infox.constants.WarningConstants;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.seam.util.ComponentUtil;

public class LocalizacaoFullEntityNode extends EntityNode<Localizacao> {

    private static final long serialVersionUID = 1L;

    public LocalizacaoFullEntityNode(EntityNode<Localizacao> parent, Localizacao entity, String[] queryChildrenList) {
        super(parent, entity, queryChildrenList);
    }
    
    public LocalizacaoFullEntityNode(String queryChildren) {
        super(queryChildren);
    }
    
    public LocalizacaoFullEntityNode(String[] queryChildrenList) {
        super(queryChildrenList);
    }
    
    @SuppressWarnings(WarningConstants.UNCHECKED)
    @Override
    protected List<Localizacao> getChildrenList(String hql, Localizacao entity) {
        List<Localizacao> children = super.getChildrenList(hql, entity);
        if (entity.getEstruturaFilho() != null) {
            GenericDAO genericDAO = ComponentUtil.getComponent(GenericDAO.NAME);
            String queryRootsOfEstrutura = "select o from Localizacao o where o.estruturaPai.id = " + 
                    entity.getEstruturaFilho().getId() + " and o.localizacaoPai is null and o.ativo = true";
            children.addAll(genericDAO.createQuery(queryRootsOfEstrutura).getResultList());
        }
        return children;
    }
    
    @Override
    protected EntityNode<Localizacao> createChildNode(Localizacao n) {
        return new LocalizacaoFullEntityNode(this, n, getQueryChildrenList());
    }
    
    @Override
    protected EntityNode<Localizacao> createRootNode(Localizacao n) {
        return new LocalizacaoFullEntityNode(null, n, getQueryChildrenList());
    }
}
