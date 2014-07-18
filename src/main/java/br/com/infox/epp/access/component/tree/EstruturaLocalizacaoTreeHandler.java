package br.com.infox.epp.access.component.tree;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.entity.Estrutura;

@Name(EstruturaLocalizacaoTreeHandler.NAME)
@AutoCreate
public class EstruturaLocalizacaoTreeHandler extends AbstractTreeHandler<Object> {

    public static final String NAME = "estruturaLocalizacaoTree";
    private static final long serialVersionUID = 1L;

    private Estrutura estrutura;
    
    @Override
    protected String getQueryRoots() {
        return "select o from Estrutura o where o.id = " + estrutura.getId(); 
    }

    @Override
    protected String getQueryChildren() {
        return "select o from Localizacao o where o.estruturaPai.id = " + estrutura.getId();
    }

    @Override
    protected EntityNode<Object> createNode() {
        return new EstruturaLocalizacaoEntityNode(getQueryChildrenList());
    }
    
    public Estrutura getEstrutura() {
        return estrutura;
    }
    
    public void setEstrutura(Estrutura estrutura) {
        this.estrutura = estrutura;
    }
}
