package br.com.infox.epp.access.component.tree;

import javax.inject.Named;

import org.jboss.seam.core.Events;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.cdi.ViewScoped;

/**
 * Tree que traz as localizações fora de estruturas e as localizações dentro das estruturas filhas das 
 * localizações superiores, ou seja, a árvore completa de localizações incluindo a subárvore das estruturas filhas
 * @author gabriel
 *
 */

@Named(LocalizacaoFullTreeHandler.NAME)
@ViewScoped
public class LocalizacaoFullTreeHandler extends AbstractTreeHandler<Localizacao> {

    private static final long serialVersionUID = 1L;
    protected static final String NAME = "localizacaoFullTree";
    public static final String SELECTED_LOCALIZACAO_ESTRUTURA = "selectedLocalizacaoEstrutura";

    @Override
    protected String getQueryRoots() {
        return "select l from Localizacao l where l.idLocalizacao = " + getIdLocalizacaoAtual()
                + " and l.ativo = true"
                + " order by localizacao";
    }

    private Integer getIdLocalizacaoAtual() {
        final UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
        final Localizacao localizacaoPaiEstrutura = usuarioPerfil.getLocalizacao();
        Localizacao raiz;
        if (localizacaoPaiEstrutura != null) {
            raiz = localizacaoPaiEstrutura;
        } else {
            raiz = usuarioPerfil.getPerfilTemplate().getLocalizacao();
        }
        return raiz.getIdLocalizacao();
    }

    @Override
    protected String getQueryChildren() {
        StringBuilder sb = new StringBuilder("select l from Localizacao l where "
                + "localizacaoPai = :" + EntityNode.PARENT_NODE
                + " and l.ativo = true");
        UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
        Localizacao localizacao = usuarioPerfil.getPerfilTemplate().getLocalizacao();
        if (usuarioPerfil.getPerfilTemplate().getLocalizacao() != null) {
            sb.append(" and estruturaPai.id = ");
            sb.append(localizacao.getEstruturaPai().getId());
        }
        return sb.toString();
    }

    private Localizacao getLocalizacaoPaiEstrutura(EntityNode<Localizacao> node) {
        if (node.getEntity().getEstruturaPai() == null) {
            return null;
        }
        while (node.getEntity().getEstruturaPai() != null) {
            node = node.getParent();
        }
        return node.getEntity();
    }
    
    @Override
    protected void raiseEvents(final EntityNode<Localizacao> node) {
        Events.instance().raiseEvent(SELECTED_LOCALIZACAO_ESTRUTURA, getSelected(), getLocalizacaoPaiEstrutura(node));
    }
    
    @Override
    protected EntityNode<Localizacao> createNode() {
        return new LocalizacaoFullEntityNode(getQueryChildrenList());
    }
}
