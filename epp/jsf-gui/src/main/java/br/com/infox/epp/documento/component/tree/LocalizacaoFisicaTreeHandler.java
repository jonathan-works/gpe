package br.com.infox.epp.documento.component.tree;

import javax.inject.Named;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.LocalizacaoFisica;

@Named(LocalizacaoFisicaTreeHandler.NAME)
@ViewScoped
public class LocalizacaoFisicaTreeHandler extends AbstractTreeHandler<LocalizacaoFisica> {

    protected static final String NAME = "localizacaoFisicaTree";
    private static final long serialVersionUID = 1L;

    @Override
    protected String getQueryRoots() {
        return "select o from LocalizacaoFisica o "
                + "where localizacaoFisicaPai is null " + "order by descricao";
    }

    @Override
    protected String getQueryChildren() {
        return "select n from LocalizacaoFisica n where localizacaoFisicaPai = :"
                + EntityNode.PARENT_NODE;
    }

}
