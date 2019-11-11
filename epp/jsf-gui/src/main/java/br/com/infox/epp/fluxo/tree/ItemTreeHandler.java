package br.com.infox.epp.fluxo.tree;

import javax.inject.Named;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Item;

@Named(ItemTreeHandler.NAME)
@ViewScoped
public class ItemTreeHandler extends AbstractTreeHandler<Item> {

    protected static final String NAME = "itemTree";
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
