package br.com.infox.epp.access.component.tree;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioPerfil;

@Name(PapelTreeHandler.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
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
        UsuarioPerfil usuarioPerfil = (UsuarioPerfil) Contexts.getSessionContext().get(Authenticator.USUARIO_PERFIL_ATUAL);
        if (usuarioPerfil != null) {
            return usuarioPerfil.getPerfilTemplate().getPapel();
        }
        return null;
    }

    @Override
    protected String getEventSelected() {
        return PAPEL_TREE_EVENT;
    }

}
