package br.com.infox.epp.access.component.tree;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Events;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioPerfil;

@Name(LocalizacaoEstruturaTree.NAME)
public class LocalizacaoEstruturaTree extends AbstractTreeHandler<Localizacao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "localizacaoEstruturaTree";
    public static final String SELECTED_LOCALIZACAO_ESTRUTURA = "selectedLocalizacaoEstrutura";

    @Override
    protected String getQueryRoots() {
        return "select l from Localizacao l where l.idLocalizacao = "
                + getIdLocalizacaoAtual() + " order by localizacao";
    }

    private Integer getIdLocalizacaoAtual() {
        final UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
        final Localizacao estrutura = usuarioPerfil.getPerfil().getPaiDaEstrutura();
        final Localizacao raiz = estrutura != null ? estrutura : usuarioPerfil.getPerfil().getLocalizacao();
        return raiz.getIdLocalizacao();
    }

    @Override
    protected String getQueryChildren() {
        return "select l from Localizacao l where localizacaoPai = :" + EntityNode.PARENT_NODE;
    }

    private Localizacao getParentEstrutura(EntityNode<Localizacao> node) {
        if (node.getEntity().getEstruturaPai() == null) {
            return null;
        } else {
            while (node.getEntity().getEstruturaPai() != null) {
                node = node.getParent();
            }
            return node.getEntity();
        }
    }
    
    @Override
    protected void raiseEvents(final EntityNode<Localizacao> node) {
        Events.instance().raiseEvent(SELECTED_LOCALIZACAO_ESTRUTURA, getSelected(), getParentEstrutura(node));
    }

}
