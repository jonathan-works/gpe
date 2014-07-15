package br.com.infox.epp.access.component.tree;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.entity.Localizacao;

@Name(EstruturaTreeHandler.NAME)
@BypassInterceptors
@Deprecated
public class EstruturaTreeHandler extends AbstractTreeHandler<Localizacao> {

    public static final String NAME = "estruturaTree";
    private static final long serialVersionUID = 1L;

    @Override
    protected String getQueryRoots() {
        return "select n from Localizacao n "
                + "where localizacaoPai is null and estrutura = true "
                + "order by localizacao";
    }

    @Override
    protected String getQueryChildren() {
        return "select n from Localizacao n where localizacaoPai = :"
                + EntityNode.PARENT_NODE;
    }

    @Override
    protected EntityNode<Localizacao> createNode() {
        return new EstruturaNode(getQueryChildrenList());
    }

}
