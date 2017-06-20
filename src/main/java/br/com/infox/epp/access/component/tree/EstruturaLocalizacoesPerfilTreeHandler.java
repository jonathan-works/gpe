package br.com.infox.epp.access.component.tree;

import javax.inject.Named;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.cdi.ViewScoped;

@Named(EstruturaLocalizacoesPerfilTreeHandler.NAME)
@ViewScoped
public class EstruturaLocalizacoesPerfilTreeHandler extends AbstractTreeHandler<Object> {

    private static final long serialVersionUID = 1L;
    protected static final String NAME = "estruturaLocalizacoesPerfilTree";
    
    @Override
    protected String getQueryRoots() {
        return "select n from Estrutura n order by n.nome ";
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
