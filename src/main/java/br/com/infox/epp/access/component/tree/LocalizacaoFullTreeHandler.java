package br.com.infox.epp.access.component.tree;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Events;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioPerfil;

/**
 * Tree que traz as localizações fora de estruturas e as localizações dentro das estruturas filhas das 
 * localizações superiores, ou seja, a árvore completa de localizações incluindo a subárvore das estruturas filhas
 * @author gabriel
 *
 */

@Name(LocalizacaoFullTreeHandler.NAME)
@AutoCreate
public class LocalizacaoFullTreeHandler extends AbstractTreeHandler<Localizacao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "localizacaoFullTree";
    public static final String SELECTED_LOCALIZACAO_ESTRUTURA = "selectedLocalizacaoEstrutura";

    @Override
    protected String getQueryRoots() {
        return "select l from Localizacao l where l.idLocalizacao = " + getIdLocalizacaoAtual()
                + " and l.ativo = true"
                + " order by localizacao";
    }

    private Integer getIdLocalizacaoAtual() {
        final UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
        final Localizacao localizacaoPaiEstrutura = usuarioPerfil.getPerfil().getPaiDaEstrutura();
        Localizacao raiz;
        if (localizacaoPaiEstrutura != null) {
            raiz = localizacaoPaiEstrutura;
        } else {
            raiz = usuarioPerfil.getPerfil().getLocalizacao();
        }
        return raiz.getIdLocalizacao();
    }

    @Override
    protected String getQueryChildren() {
        StringBuilder sb = new StringBuilder("select l from Localizacao l where "
                + "localizacaoPai = :" + EntityNode.PARENT_NODE
                + " and l.ativo = true");
        UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
        Estrutura estruturaPai = usuarioPerfil.getPerfil().getLocalizacao().getEstruturaPai();
        if (estruturaPai != null) {
            sb.append(" and estruturaPai.id = ");
            sb.append(estruturaPai.getId());
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
