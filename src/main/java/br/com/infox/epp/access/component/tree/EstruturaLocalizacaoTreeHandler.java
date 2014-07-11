package br.com.infox.epp.access.component.tree;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.component.tree.bean.EstruturaLocalizacaoBean;

@Name(EstruturaLocalizacaoTreeHandler.NAME)
@AutoCreate
public class EstruturaLocalizacaoTreeHandler extends AbstractTreeHandler<EstruturaLocalizacaoBean> {

    public static final String NAME = "estruturaLocalizacaoTree";
    private static final long serialVersionUID = 1L;

    @Override
    protected String getQueryRoots() {
        return "select new br.com.infox.epp.access.component.tree.bean.EstruturaLocalizacaoBean"
                + "(o.id, o.nome, 'E') from Estrutura o";
    }

    @Override
    protected String getQueryChildren() {
        return "select new br.com.infox.epp.access.component.tree.bean.EstruturaLocalizacaoBean"
                + "(o.idLocalizacao, o.localizacao, 'L') from Localizacao o "
                + "where o.estruturaPai = :" + EntityNode.PARENT_NODE;
    }

    @Override
    protected EntityNode<EstruturaLocalizacaoBean> createNode() {
        return new EstruturaLocalizacaoEntityNode(getQueryChildrenList());
    }
}
