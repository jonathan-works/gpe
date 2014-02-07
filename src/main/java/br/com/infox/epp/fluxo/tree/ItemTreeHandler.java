package br.com.infox.epp.fluxo.tree;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.fluxo.entity.Item;

@Name(ItemTreeHandler.NAME)
public class ItemTreeHandler extends AbstractTreeHandler<Item> {

    public static final String NAME = "itemTree";
    private static final long serialVersionUID = 1L;

    @Override
    protected String getQueryRoots() {
        return "select n from Item n " + "where itemPai is null "
                + "order by descricaoItem";
    }

    @Override
    protected String getQueryChildren() {
        return "select n from Item n where itemPai = :"
                + EntityNode.PARENT_NODE;
    }

}
