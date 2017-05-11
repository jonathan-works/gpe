package br.com.infox.epp.access.crud;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.security.Role;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.action.RoleAction;
import org.jboss.seam.security.permission.PermissionManager;

import br.com.infox.constants.WarningConstants;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.api.RolesMap;
import br.com.infox.epp.access.component.tree.RolesTreeHandler;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.Permissao;
import br.com.infox.epp.access.entity.Recurso;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.RecursoManager;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.seam.security.operation.PopulateRoleMembersListOperation;
import br.com.infox.seam.security.operation.UpdateRolesOperation;
import br.com.infox.seam.util.ComponentUtil;

@Name(PapelCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class PapelCrudAction extends AbstractCrudAction<Papel, PapelManager> {
    private static final long serialVersionUID = 1L;
    private static final String ROLE_ACTION = "org.jboss.seam.security.management.roleAction";
    private static final String CONSTRAINT_VIOLATION_UNIQUE_VIOLATION = "#{infoxMessages['constraintViolation.uniqueViolation']}";
    private static final String RECURSOS_TAB_ID = "recursosTab";
    private static final String PAPEIS_TAB_ID = "papeisTab";
    private static final String MEMBROS_TAB_ID = "herdeirosTab";

    public static final String NAME = "papelCrudAction";

    private Map<Boolean, List<String>> papeisDisponiveis;
    private Map<String, Papel> papelMap;
    private Map<String, Recurso> recursoMap;

    private List<String> membros;
    private Map<String, Papel> membrosMap;

    private List<String> recursosDisponiveis;
    private List<String> papeis;
    private List<String> recursos;

    private String activeInnerTab;
    private boolean acceptChange = Boolean.FALSE;

    private RecursoManager recursoManager = BeanManager.INSTANCE.getReference(RecursoManager.class);
    
    @In IdentityManager identityManager;

    private final Comparator<String> papelComparator = new Comparator<String>() {
        @Override
        public int compare(final String o1, final String o2) {
            final String n1 = papelMap.get(o1).toString();
            final String n2 = papelMap.get(o2).toString();
            return n1.compareTo(n2);
        }
    };
    
    @Override
    public void onClickFormTab() {
    	clear();
    }
    
    public void onClickRecursosTab(){
    	recursos = null;
    	recursosDisponiveis = null;
    }
    
    public void onClickHerdeirosOrPapelTab(){
    	membros = null;
    	membrosMap = null;
    	papeis = null;
    	papeisDisponiveis = null;
    }

    public Integer getPapelId() {
        return (Integer) getId();
    }

    private void clear() {
        papeis = null;
        recursos = null;
        recursosDisponiveis = null;
        papeisDisponiveis = null;
        membros = null;
    }

    public void setPapelId(final Integer id) {
        final Object oid = getId();
        if (oid == null || !oid.equals(id)) {
            super.setId(id);
            Conversation.instance().end();
            clear();

            getRoleaction().editRole(getInstance().getIdentificador());
        }
    }

    public List<String> getMembros() {
        if (membros == null) {
            membros = new ArrayList<>();
            membrosMap = new HashMap<>();
            final List<Principal> list = new ArrayList<>();
            new PopulateRoleMembersListOperation(getInstance()
                    .getIdentificador(), list).run();
            if (list.isEmpty()) {
                return new ArrayList<>();
            }
            final List<String> idPapeis = new ArrayList<String>();
            for (final Principal principal : list) {
                idPapeis.add(principal.getName());
            }
            final List<Papel> papelList = getManager()
                    .getPapeisByListaDeIdentificadores(idPapeis);
            for (final Papel papel : papelList) {
                final String id = papel.getIdentificador();
                membros.add(id);
                membrosMap.put(id, papel);
            }
            Collections.sort(membros);
        }
        return membros;
    }

    public void setMembros(final List<String> membros) {
        if (acceptChange || MEMBROS_TAB_ID.equals(activeInnerTab)) {
            acceptChange = Boolean.FALSE;
            this.membros = membros;
        }
    }

    private RoleAction getRoleaction() {
        return ComponentUtil.getComponent(ROLE_ACTION);
    }

    @Override
    public void newInstance() {
        super.newInstance();
        clear();
        Contexts.removeFromAllContexts(ROLE_ACTION);
    }

    @Override
    public String remove(final Papel p) {
        setInstance(p);
        final String ret = super.remove();
        newInstance();
        RolesMap.instance().clear();
        if (REMOVED.equals(ret)) {
            getMessagesHandler().add(MSG_REGISTRO_REMOVIDO);
        }
        return ret;
    }

    public String getNome(final String identificador) {
        if (papelMap != null && papelMap.containsKey(identificador)) {
            return papelMap.get(identificador).toString();
        }
        return null;
    }

    public String getNomeRecurso(final String identificador) {
        if (recursoMap != null && recursoMap.containsKey(identificador)) {
            return recursoMap.get(identificador).getNome();
        }
        return null;
    }

    public List<String> getPapeis() {
        if (papeis == null) {
            getRoleaction().setRole(getInstance().getIdentificador());
            papeis = identityManager.getRoleGroups(getInstance().getIdentificador());
            if (papeis == null) {
                papeis = new ArrayList<>();
            }
        }
        return papeis;
    }

    public void setPapeis(final List<String> papeis) {
        if (acceptChange || PAPEIS_TAB_ID.equals(activeInnerTab)) {
            acceptChange = Boolean.FALSE;
            this.papeis = papeis;
        }
    }

    /**
     * Busca os papeis que podem ser atribuidos ao papel atual, removendo
     * aqueles que são implícitos, isso é, atribuidos por herança de papel
     * 
     * @return
     */
    public List<String> getPapeisDisponiveis(final boolean removeMembros) {
        if (papeisDisponiveis == null) {
            papeisDisponiveis = new HashMap<>();
        }
        if (!papeisDisponiveis.containsKey(removeMembros)) {
            final List<String> assignableRoles = getRoleaction()
                    .getAssignableRoles();
            papeisDisponiveis.put(removeMembros, assignableRoles);
            final List<String> papeisImplicitos = getPapeis();
            removePapeisImplicitos(assignableRoles, papeisImplicitos);
            assignableRoles.remove(getInstance().getIdentificador());
            removeRecursos(assignableRoles);
            if (isManaged() && removeMembros) {
                removeMembros(getInstance().getIdentificador(), assignableRoles);
            } else {
                assignableRoles.removeAll(papeis);
            }
            if (papelMap == null) {
                papelMap = new HashMap<>();
            }
            final List<Papel> papelList = getManager()
                    .getPapeisByListaDeIdentificadores(assignableRoles);
            for (final Papel p : papelList) {
                papelMap.put(p.getIdentificador(), p);
            }
            Collections.sort(assignableRoles, papelComparator);
        }
        return papeisDisponiveis.get(removeMembros);
    }

    @SuppressWarnings(WarningConstants.UNCHECKED)
    public List<String> getRecursos() {
        if (recursos == null) {
            final String identificador_ = getInstance().getIdentificador();
            if (IdentityManager.instance().roleExists(identificador_)) {
                final Role role = new Role(identificador_);
                final List<Permissao> permissoes = (List<Permissao>) PermissionManager
                        .instance().getPermissoesFromRole(role);
                recursos = recursoManager
                        .getIdentificadorRecursosFromPermissoes(permissoes);
            } else {
                recursos = new ArrayList<String>();
            }
        }
        return recursos;
    }

    public void setRecursos(final List<String> recursos) {
        if (acceptChange || RECURSOS_TAB_ID.equals(activeInnerTab)) {
            acceptChange = Boolean.FALSE;
            this.recursos = recursos;
        }
    }

    public String getActiveInnerTab() {
        return activeInnerTab;
    }

    public void setActiveInnerTab(final String activeInnerTab) {
        this.acceptChange = Boolean.TRUE;
        this.activeInnerTab = activeInnerTab;
    }

    public List<String> getRecursosDisponiveis() {
        if (recursosDisponiveis == null) {
            recursosDisponiveis = new ArrayList<>();
            if (IdentityManager.instance().roleExists(
                    getInstance().getIdentificador())) {
                final List<Recurso> listaRecursos = recursoManager.findAll();
                recursoMap = new HashMap<>();
                for (final Recurso recurso : listaRecursos) {
                    final String identificadorRecurso = recurso
                            .getIdentificador();
                    recursosDisponiveis.add(identificadorRecurso);
                    recursoMap.put(identificadorRecurso, recurso);
                }
            }
        }
        return recursosDisponiveis;
    }

    private void removeRecursos(final List<String> roles) {
        for (final Iterator<String> iterator = roles.iterator(); iterator
                .hasNext();) {
            final String papelId = iterator.next();
            if (papelId.startsWith("/")) {
                iterator.remove();
            }
        }
    }

    private void removePapeisImplicitos(final List<String> list,
            List<String> from) {
        if (from == null) {
            return;
        }
        // ser for o mesmo objeto, clona para evitar
        // ConcurrentModificationException
        if (from.equals(list)) {
            from = new ArrayList<String>(list);
        }
        for (final String papel : from) {
            removePapeisImplicitos(papel, list);
        }
    }

    /**
     * Remove o papel da lista, recursivamente
     * 
     * @param papel
     */
    private void removePapeisImplicitos(final String papel,
            final List<String> list) {
        for (final String p : IdentityManager.instance().getRoleGroups(papel)) {
            list.remove(p);
            getManager().flush();
            removePapeisImplicitos(p, list);
        }
    }

    private void removeMembros(final String papel, final List<String> roles) {
        final List<Principal> listMembers = new ArrayList<>();
        new PopulateRoleMembersListOperation(papel, listMembers).run();
        for (final Principal p : listMembers) {
            if (p instanceof Role) {
                roles.remove(p.getName());
                removeMembros(p.getName(), roles);
            }
        }
    }

    @Override
    protected boolean isInstanceValid() {
        boolean result = Boolean.TRUE;
        if (IdentityManager.instance().roleExists(
                getInstance().getIdentificador())
                && !isManaged()) {
            getMessagesHandler().clear();
            getMessagesHandler().add(CONSTRAINT_VIOLATION_UNIQUE_VIOLATION);
            result = Boolean.FALSE;
        }
        return result;
    }

    @Override
    protected void afterSave(final String ret) {
        if (UPDATED.equals(ret)) {
            final String role = getInstance().getIdentificador();
            final List<String> roleGroup = getPapeis();
            final Collection<String> rolesToInclude = getMembros();
            final Collection<String> rolesToExclude = membrosMap.keySet();
            final Collection<String> availableResourcesList = getRecursosDisponiveis();
            final Collection<String> selectedResourcesList = getRecursos();
            final UpdateRolesOperation operation = new UpdateRolesOperation(
                    roleGroup, role, rolesToInclude, rolesToExclude,
                    availableResourcesList, selectedResourcesList);
            operation.run();
            getManager().flush();
            clear();
        }
    }

    @Observer(RolesTreeHandler.ROLE_TREE_EVENT)
    public void treeSelected(final Papel papel) {
        setPapelId(papel.getIdPapel());
        setTab("form");
    }

    public static PapelCrudAction instance() {
        return ComponentUtil.getComponent(NAME);
    }

}
