package br.com.infox.epp.access.component.tree;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.crud.LocalizacaoCrudAction;
import br.com.infox.epp.access.entity.Localizacao;

@Name(LocalizacaoTreeHandler.NAME)
@AutoCreate
public class LocalizacaoTreeHandler extends AbstractTreeHandler<Localizacao> {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "localizacaoTree";

    @Override
    protected String getQueryRoots() {
        return "select n from Localizacao n " + "where localizacaoPai is null "
                + "order by localizacao";
    }

    @Override
    protected String getQueryChildren() {
        return "select n from Localizacao n where localizacaoPai = :"
                + EntityNode.PARENT_NODE;
    }

    @Override
    protected String getEventSelected() {
        return "evtSelectLocalizacao";
    }

    @Override
    protected Localizacao getEntityToIgnore() {
        return ((LocalizacaoCrudAction) Component.getInstance(LocalizacaoCrudAction.NAME)).getInstance();
    }
}
