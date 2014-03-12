package br.com.infox.epp.access.component.tree;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;

@Name(PapelTreeHandler.NAME)
@Scope(ScopeType.PAGE)
@BypassInterceptors
public class PapelTreeHandler extends AbstractTreeHandler<Papel> {
    public static final String NAME = "papelTree";
    public static final String PAPEL_TREE_EVENT = "papelTreeHandlerSelected";

    private static final long serialVersionUID = 1L;

    private static final String QUERY_PAPEIS = "select grupo from Papel p "
            + "join p.grupos grupo " + "where p = :" + EntityNode.PARENT_NODE
            + " and grupo.identificador not like '/%' order by grupo.nome";

    @Override
    protected String getQueryChildren() {
        return QUERY_PAPEIS;
    }

    @Override
    protected String getQueryRoots() {
        String hql = "select grupo from Papel p "
                + "right join p.grupos grupo where grupo.identificador "
                + "not like '/%' and p is null order by grupo.nome";
        return hql;
    }

    protected Papel getPapelAtual() {
        UsuarioLocalizacao usuarioLocalizacao = (UsuarioLocalizacao) Contexts.getSessionContext().get("usuarioLogadoLocalizacaoAtual");
        if (usuarioLocalizacao != null) {
            return usuarioLocalizacao.getPapel();
        }
        return null;
    }

    @Override
    protected String getEventSelected() {
        return PAPEL_TREE_EVENT;
    }

}
