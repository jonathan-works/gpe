package br.com.infox.epp.access.component.tree;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.system.util.ParametroUtil;

@Name(LocalizacaoSemEstruturaTreeHandler.NAME)
@AutoCreate
public class LocalizacaoSemEstruturaTreeHandler extends AbstractTreeHandler<Localizacao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "localizacaoSemEstruturaTree";

    @Override
    protected String getQueryRoots() {
        return "select o from Localizacao o where o.localizacaoPai is null and o.estruturaPai is null "
                + "and o.estruturaFilho is null and o.localizacao != '" + ParametroUtil.LOCALIZACAO_EXTERNA + "'";
    }

    @Override
    protected String getQueryChildren() {
        return "select o from Localizacao o where o.localizacaoPai = :" + EntityNode.PARENT_NODE + " "
                + "and o.estruturaPai is null and o.estruturaFilho is null and "
                + "o.localizacao != '" + ParametroUtil.LOCALIZACAO_EXTERNA + "'";
    }

}
