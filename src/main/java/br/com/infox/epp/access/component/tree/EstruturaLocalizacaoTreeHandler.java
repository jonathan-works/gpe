package br.com.infox.epp.access.component.tree;

import javax.inject.Named;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.cdi.ViewScoped;

/**
 * Árvore cuja raiz é uma estrutura e o restante dos nós são as localizações dentro dessa estrutura.
 * Utilizada pela aba de Visão Hierárquica do CRUD de Estrutura
 * @author gabriel
 *
 */

@Named(EstruturaLocalizacaoTreeHandler.NAME)
@ViewScoped
public class EstruturaLocalizacaoTreeHandler extends AbstractTreeHandler<Object> {

    protected static final String NAME = "estruturaLocalizacaoTree";
    private static final long serialVersionUID = 1L;

    private Estrutura estrutura;

    @Override
    protected String getQueryRoots() {
        if (estrutura != null && estrutura.getId() != null) {
            return "select o from Estrutura o where o.id = " + estrutura.getId();
        }
        return "select o from Estrutura o where o.id is null"; // Quando a tree é chamada sozinha pelo seam e pelas injeções em um momento inoportuno
    }

    @Override
    protected String getQueryChildren() {
        if (estrutura != null && estrutura.getId() != null) {
            return "select o from Localizacao o where o.estruturaPai.id = " + estrutura.getId();
        }
        return "select o from Estrutura o where o.id is null"; // Quando a tree é chamada sozinha pelo seam e pelas injeções em um momento inoportuno
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
