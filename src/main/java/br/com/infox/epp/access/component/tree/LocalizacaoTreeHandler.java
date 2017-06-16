package br.com.infox.epp.access.component.tree;

import javax.inject.Named;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.crud.LocalizacaoCrudAction;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.util.Beans;

@Named(LocalizacaoTreeHandler.NAME)
@ViewScoped
public class LocalizacaoTreeHandler extends AbstractTreeHandler<Localizacao> {

    private static final long serialVersionUID = 1L;

    protected static final String NAME = "localizacaoTree";
    public static final String EVENT_SELECTED = "evtSelectLocalizacao";

    @Override
    protected String getQueryRoots() {
        return "select n from Localizacao n where n=#{authenticator.getLocalizacaoAtual()} order by n.localizacao";
    }

    @Override
    protected String getQueryChildren() {
        return "select n from Localizacao n where localizacaoPai = :"
                + EntityNode.PARENT_NODE;
    }

    @Override
    protected String getEventSelected() {
        return EVENT_SELECTED;
    }

    @Override
    protected Localizacao getEntityToIgnore() {
        return Beans.getReference(LocalizacaoCrudAction.class).getInstance();
    }
}
