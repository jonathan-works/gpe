package br.com.infox.epp.access.component.tree;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;

@Name(EstruturaLocalizacoesPerfilTreeHandler.NAME)
@AutoCreate
public class EstruturaLocalizacoesPerfilTreeHandler extends AbstractTreeHandler<Object> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "estruturaLocalizacoesPerfilTree";
    
    @Override
    protected String getQueryRoots() {
        return "select n from Estrutura n";
    }

    @Override
    protected String getQueryChildren() {
        return "select n from Localizacao n where n.estruturaPai is not null ";
    }
    
    @Override
    protected EntityNode<Object> createNode() {
        return new EstruturaLocalizacoesPerfilEntityNode(getQueryChildrenList());
    }

}
