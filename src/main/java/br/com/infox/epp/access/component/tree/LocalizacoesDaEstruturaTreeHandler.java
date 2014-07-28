package br.com.infox.epp.access.component.tree;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.crud.EstruturaCrudAction;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Localizacao;

/**
 * Localizações dentro de uma estrutura. Utilizada pelo {@link EstruturaCrudAction}
 * @author gabriel
 *
 */

@Name(LocalizacoesDaEstruturaTreeHandler.NAME)
@AutoCreate
public class LocalizacoesDaEstruturaTreeHandler extends AbstractTreeHandler<Localizacao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "localizacoesDaEstruturaTree";

    private Estrutura estruturaPai = new Estrutura();
    
    @In
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
