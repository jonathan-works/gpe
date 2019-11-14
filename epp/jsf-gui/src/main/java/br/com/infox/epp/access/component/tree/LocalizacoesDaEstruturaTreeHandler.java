package br.com.infox.epp.access.component.tree;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.crud.EstruturaCrudAction;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.cdi.ViewScoped;

/**
 * Localizações dentro de uma estrutura. Utilizada pelo {@link EstruturaCrudAction}
 * @author gabriel
 *
 */

@Named(LocalizacoesDaEstruturaTreeHandler.NAME)
@ViewScoped
public class LocalizacoesDaEstruturaTreeHandler extends AbstractTreeHandler<Localizacao> {

    private static final long serialVersionUID = 1L;
    protected static final String NAME = "localizacoesDaEstruturaTree";

    private Estrutura estruturaPai = new Estrutura();
    
    @Inject
    private EstruturaCrudAction estruturaCrudAction;
    
    @Override
    protected String getQueryRoots() {
        return "select o from Localizacao o where o.ativo = true and "
                + "o.localizacaoPai is null and o.estruturaPai.id = " + estruturaPai.getId() + 
                " order by o.caminhoCompleto"; 
    }

    @Override
    protected String getQueryChildren() {
        return "select o from Localizacao o where o.ativo = true and o.localizacaoPai = :" + EntityNode.PARENT_NODE + 
                " order by o.caminhoCompleto";
    }
    
    public Estrutura getEstruturaPai() {
        return estruturaPai;
    }
    
    public void setEstruturaPai(Estrutura estruturaPai) {
        this.estruturaPai = estruturaPai;
    }

    @Override
    protected Localizacao getEntityToIgnore() {
        return estruturaCrudAction.getLocalizacaoFilho();
    }
}
