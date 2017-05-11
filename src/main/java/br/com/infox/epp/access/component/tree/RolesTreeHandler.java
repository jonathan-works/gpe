package br.com.infox.epp.access.component.tree;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.ArrayList;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.Redirect;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.crud.RecursoCrudAction;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.cdi.config.BeanManager;

@Name(RolesTreeHandler.ROLES_TREE)
@Scope(ScopeType.PAGE)
@BypassInterceptors
public class RolesTreeHandler extends AbstractTreeHandler<Papel> {

    public static final String ROLES_TREE = "rolesTree";

    public static final String ROLE_TREE_EVENT = "roleTreeHandlerSelected";

    private static final long serialVersionUID = 1L;

    /** Expressões para montar a árvore invertida (do menor papel para o maior) */
    private static final String QUERY_PAPEIS_INV = "select p from Papel p "
            + "join p.grupos grupo " + "where grupo = :"
            + EntityNode.PARENT_NODE + " order by p.nome";
    private static final String QUERY_ROOT_INV = "select p from Papel p where not exists ("
            + "select p1, grupo from Papel p1 join p1.grupos grupo where "
            + "p = p1 and grupo.identificador not like '/%') "
            + "and p.identificador not like '/%' order by p.nome";

    private static final String QUERY_RECURSOS = "select grupo from Papel p "
            + "join p.grupos grupo " + "where p = :" + EntityNode.PARENT_NODE
            + " and grupo.identificador like '/%' order by grupo.nome";

    private static final String QUERY_ROOT = "select grupo from Papel p "
            + "right join p.grupos grupo where grupo.identificador "
            + "not like '/%' and p is null order by grupo.nome";

    private static final String QUERY_PAPEIS = "select grupo from Papel p "
            + "join p.grupos grupo " + "where p = :" + EntityNode.PARENT_NODE
            + " and grupo.identificador not like '/%' order by grupo.nome";

    private boolean invertida;

    @Override
    protected String getQueryChildren() {
        return null;
    }

    @Override
    // Variável retornada alterada de List<Query> para String[] contendo hql
    protected String[] getQueryChildrenList() {
        String[] hql = new String[] {
            invertida ? QUERY_PAPEIS_INV : QUERY_PAPEIS, QUERY_RECURSOS };

        return hql;
    }

    @Override
    protected String getQueryRoots() {
        return invertida ? QUERY_ROOT_INV : QUERY_ROOT;
    }

    @Override
    protected String getEventSelected() {
        return ROLE_TREE_EVENT;
    }

    public void inverter() {
        invertida = !invertida;
        this.clearTree();
    }

    public boolean getInvertida() {
        return invertida;
    }

    @SuppressWarnings(UNCHECKED)
    @Override
    public void processTreeSelectionChange(TreeSelectionChangeEvent ev) {
        // Considerando single selection
        Object selectionKey = new ArrayList<Object>(ev.getNewSelection()).get(0);
        UITree tree = (UITree) ev.getSource();
        setTreeId(":" + tree.getClientId());

        Object key = tree.getRowKey();
        tree.setRowKey(selectionKey);
        Object node = tree.getRowData();
        if (node instanceof EntityNode) {
            EntityNode<Papel> en = (EntityNode<Papel>) tree.getRowData();
            tree.setRowKey(key);
            setSelected(en.getEntity());
            closeParentPanel(tree);
            raiseEvents(en);
        } else if (node instanceof String) {
            RecursoCrudAction rca = BeanManager.INSTANCE.getReference(RecursoCrudAction.class);
            rca.setRecurso((String) node);
            final Redirect redirect = Redirect.instance();
            redirect.setViewId("/useradmin/recursoListView.xhtml");
            redirect.execute();
        }
    }

}
