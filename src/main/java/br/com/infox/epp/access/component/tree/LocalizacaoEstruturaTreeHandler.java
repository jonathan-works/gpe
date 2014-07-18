package br.com.infox.epp.access.component.tree;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Events;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioPerfil;

@Name(LocalizacaoEstruturaTreeHandler.NAME)
@BypassInterceptors
@Deprecated
public class LocalizacaoEstruturaTreeHandler extends AbstractTreeHandler<Localizacao> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "localizacaoEstruturaTreeHandler";
    public static final String EVENT_SELECT_LOC_ESTRUTURA = "evtSelectLocalizacaoEstrutura";

    private UsuarioPerfil usuarioPerfilAtual;

    @Override
    protected String getQueryRoots() {
        final StringBuilder sb = new StringBuilder("select n from Localizacao n ");
        sb.append("where ");
        sb.append(" n.idLocalizacao = " + getIdLocalizacao());
        sb.append(" order by localizacao");
        return sb.toString();
    }

    private Integer getIdLocalizacao() {
        usuarioPerfilAtual = Authenticator.getUsuarioPerfilAtual();
        final UsuarioPerfil usuarioPerfil = getUsuarioPerfilAtual();
        final Localizacao estrutura = usuarioPerfil.getPerfil().getPaiDaEstrutura();
        final Localizacao loc = estrutura != null ? estrutura : usuarioPerfil.getPerfil().getLocalizacao();
        return loc.getIdLocalizacao();
    }

    @Override
    protected String getQueryChildren() {
        return "select n from Localizacao n where localizacaoPai = :"
                + EntityNode.PARENT_NODE;
    }

    @Override
    protected String getEventSelected() {
        return null;
    }

    @Override
    protected Localizacao getEntityToIgnore() {
        return getSelected();
    }

    @Override
    protected EntityNode<Localizacao> createNode() {
        return new LocalizacaoNode(getQueryChildrenList());
    }

    @Override
    protected void raiseEvents(final EntityNode<Localizacao> en) {
        Events.instance().raiseEvent(EVENT_SELECT_LOC_ESTRUTURA, getSelected(), getEstrutura(en));
    }

    private Localizacao getEstrutura(final EntityNode<Localizacao> en) {
        EntityNode<Localizacao> parent = en.getParent();
        while (parent != null) {
            final Localizacao parentEntity = parent.getEntity();
            if (parentEntity.getEstruturaFilho() != null) {
                return parentEntity;
            }
            parent = parent.getParent();
        }
        return null;
    }

    public void setUsuarioPerfilAtual(
            final UsuarioPerfil usuarioLocalizacaoAtual) {
        this.usuarioPerfilAtual = usuarioLocalizacaoAtual;
    }

    public UsuarioPerfil getUsuarioPerfilAtual() {
        if (usuarioPerfilAtual == null) {
            usuarioPerfilAtual = Authenticator.getUsuarioPerfilAtual();
        }
        return usuarioPerfilAtual;
    }

    @Override
    public void clearTree() {
        super.clearTree();
        usuarioPerfilAtual = null;
    }

}
