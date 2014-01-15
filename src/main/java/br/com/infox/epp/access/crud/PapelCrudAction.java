package br.com.infox.epp.access.crud;

import java.security.Principal;
import java.util.ArrayList;
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
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.Role;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.action.RoleAction;
import org.jboss.seam.security.permission.Permission;
import org.jboss.seam.security.permission.PermissionManager;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.api.RolesMap;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.Permissao;
import br.com.infox.epp.access.entity.Recurso;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.RecursoManager;
import br.com.itx.util.ComponentUtil;

@Name(PapelCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class PapelCrudAction extends AbstractCrudAction<Papel> {
	
	private static final String ROLE_ACTION = "org.jboss.seam.security.management.roleAction";
    private static final String ACCESS = "access";
    private static final String CONSTRAINT_VIOLATION_UNIQUE_VIOLATION = "#{messages['constraintViolation.uniqueViolation']}";
    private static final String RECURSOS_TAB_ID = "recursosTab";
    private static final String PAPEIS_TAB_ID = "papeisTab";
    private static final String MEMBROS_TAB_ID = "herdeirosTab";

    public static final String NAME = "papelCrudAction";

	private Map<Boolean, List<String>> papeisDisponiveis;
	private Map<String, Papel> papelMap;
	private Map<String, Recurso> recursoMap;

	private List<String> membros;
	private Map<String, Papel> membrosMap;

	private String identificador;

	private List<String> recursosDisponiveis;
	private List<String> papeis;
	private List<String> recursos;
	
	private String activeInnerTab;
	private boolean acceptChange=Boolean.FALSE;
	
	@In private PapelManager papelManager;
	@In private RecursoManager recursoManager;
    
	private final Comparator<String> papelComparator = new Comparator<String>(){
        @Override
        public int compare(final String o1, final String o2) {
            final String n1 = papelMap.get(o1).toString();
            final String n2 = papelMap.get(o2).toString();
            return n1.compareTo(n2);
        }
    };
    
	public Integer getPapelId() {
		return (Integer) getId();
	}
	
	private void clear() {
		papeis = null;
		recursos = null;
		recursosDisponiveis = null;
		papeisDisponiveis = null;
		membros = null;
		identificador = null;
	}

	public void setPapelId(final Integer id) {
		final Object oid = getId();
		if (oid == null || !oid.equals(id)) {
			super.setId(id);
			Conversation.instance().end();
			clear();
			
			this.identificador = getInstance().getIdentificador();
			getRoleaction().editRole(this.identificador);
		}
	}

	public List<String> getMembros() {
		if (membros == null) {
			membros = new ArrayList<String>();
			membrosMap = new HashMap<String, Papel>();
			final List<Principal> list = new ArrayList<Principal>();
			
			new RunAsOperation(Boolean.TRUE) {
				@Override
				public void execute() {
					list.addAll(IdentityManager.instance().listMembers(
							getInstance().getIdentificador()));
				}
			}.run();
			if (list.isEmpty()) {
				return new ArrayList<String>();
			}
			final List<String> idPapeis = new ArrayList<String>();
			for (final Principal principal : list) {
				idPapeis.add(principal.getName());
			}
			final List<Papel> papelList = papelManager.getPapeisByListaDeIdentificadores(idPapeis);
			for (final Papel papel : papelList) {
				final String id = papel.getIdentificador();
				membros.add(id);
				membrosMap.put(id, papel);
			}
			Collections.sort(membros);
		}
		return membros;
	}
	
	public void setMembros(final List<String> membros){
	    if (acceptChange || MEMBROS_TAB_ID.equals(activeInnerTab)) {
	        acceptChange=Boolean.FALSE;
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
	
	public String getNomeRecurso(final String identificador){
	    if (recursoMap != null && recursoMap.containsKey(identificador)){
	        return recursoMap.get(identificador).getNome();
	    }
	    return null;
	}
	
	public List<String> getPapeis() {
		if (papeis == null) {
			papeis = getRoleaction().getGroups();
			if (papeis == null) {
				papeis = new ArrayList<String>();
			}
		}
		return papeis;
	}

	public void setPapeis(final List<String> papeis) {
	    if (acceptChange || PAPEIS_TAB_ID.equals(activeInnerTab)) {
	        acceptChange=Boolean.FALSE;
	        this.papeis = papeis;
	    }
	}
	
	/**
	 * Busca os papeis que podem ser atribuidos ao papel atual,
	 * removendo aqueles que são implícitos, isso é, atribuidos 
	 * por herança de papel
	 * 
	 * @return
	 */
	public List<String> getPapeisDisponiveis(final boolean removeMembros) {
		if (papeisDisponiveis == null) {
			papeisDisponiveis = new HashMap<Boolean, List<String>>();
		}
		if (!papeisDisponiveis.containsKey(removeMembros)) {
			final List<String> assignableRoles = getRoleaction().getAssignableRoles();
			papeisDisponiveis.put(removeMembros, assignableRoles);
			removePapeisImplicitos(assignableRoles, getPapeis());
			removeRecursos(assignableRoles);
			if (isManaged() && removeMembros) {
				removeMembros(getInstance().getIdentificador(), assignableRoles);
			} else {
				assignableRoles.removeAll(papeis);
			}
			if (papelMap == null) {
				papelMap = new HashMap<String, Papel>();
			}
			final List<Papel> papelList = papelManager.getPapeisByListaDeIdentificadores(assignableRoles);
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
                final List<Permissao> permissoes = (List<Permissao>) PermissionManager.instance().getPermissoesFromRole(role);
				recursos = recursoManager.getIdentificadorRecursosFromPermissoes(permissoes);
			} else {
				recursos = new ArrayList<String>();
			}
		}
		return recursos;
	}
	
	public void setRecursos(final List<String> recursos) {
	    if (acceptChange || RECURSOS_TAB_ID.equals(activeInnerTab)) {
	        acceptChange=Boolean.FALSE;
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
            if (IdentityManager.instance().roleExists(getInstance().getIdentificador())) {
                final List<Recurso> listaRecursos = recursoManager.findAll(Recurso.class);
                recursoMap = new HashMap<>();
                for (final Recurso recurso : listaRecursos){
                    final String identificadorRecurso = recurso.getIdentificador();
                    recursosDisponiveis.add(identificadorRecurso);
                    recursoMap.put(identificadorRecurso, recurso);
                }
            } 
        }
		return recursosDisponiveis;
	}

	private void removeRecursos(final List<String> roles) {
		for (final Iterator<String> iterator = roles.iterator(); iterator.hasNext();) {
			final String papelId = iterator.next();
			if (papelId.startsWith("/")) {
				iterator.remove();
			}
		}
	}
	
	private void removePapeisImplicitos(final List<String> list, List<String> from) {
		if (from == null) {
			return;
		}
		// ser for o mesmo objeto, clona para evitar ConcurrentModificationException
		if (from.equals(list)) {
			from = new ArrayList<String>(list);
		}
		for (final String papel : from) {
			removePapeisImplicitos(papel, list);
		}
	}

	/**
	 * Remove o papel da lista, recursivamente
	 * @param papel
	 */
	private void removePapeisImplicitos(final String papel, final List<String> list) {
		for (final String p : IdentityManager.instance().getRoleGroups(papel)) {
			list.remove(p);
			getGenericManager().flush();
			removePapeisImplicitos(p, list);
		}
	}
	
	private void removeMembros(final String papel, final List<String> roles) {
		final List<Principal> listMembers = new ArrayList<Principal>();
		new RunAsOperation(Boolean.TRUE) {
			@Override
			public void execute() {
				listMembers.addAll(IdentityManager.instance().listMembers(papel));
			}
		}.run();
		for (final Principal p : listMembers) {
			if (p instanceof Role) {
				final String roleName = p.getName();
                roles.remove(roleName);
				removeMembros(roleName, roles);
			}
		}
	}

	public String save() {
		final StatusMessages messages = getMessagesHandler();
        
		final boolean managed = isManaged();
        if (IdentityManager.instance().roleExists(getInstance().getIdentificador()) && !managed) {
			messages.add(CONSTRAINT_VIOLATION_UNIQUE_VIOLATION);
			return null;
		}

		final StringBuilder ret = new StringBuilder();
		new RunAsOperation(Boolean.TRUE) {
			@Override
			public void execute() {
				ret.append(saveOp());
			}
		}.run();
		RolesMap.instance().clear();
		
		if ("success".equals(ret.toString())) {
			if (managed) {
				messages.add(MSG_REGISTRO_ALTERADO);
			} else {
				messages.add(MSG_REGISTRO_CRIADO);
			}
		} else {
		    messages.add(CONSTRAINT_VIOLATION_UNIQUE_VIOLATION);
		}
		
		return ret.toString();
	}
	
	public String saveOp() {
		identificador = getInstance().getIdentificador();
		getRoleaction().setRole(identificador);
		papeis = new ArrayList<String>(getPapeis());
		removePapeisImplicitos(papeis, papeis);
		getRoleaction().setGroups(papeis);
		final String save = getRoleaction().save();
		if (isManaged()) {
			if (membros != null) {
				final List<String> incluirMembros = new ArrayList<String>(membros);
				incluirMembros.removeAll(membrosMap.keySet());
				for (final String membro : incluirMembros) {
					IdentityManager.instance().addRoleToGroup(membro, identificador);
				}
				final List<String> excluirMembros = new ArrayList<String>(membrosMap.keySet());
				excluirMembros.removeAll(membros);
				for (final String membro : excluirMembros) {
					IdentityManager.instance().removeRoleFromGroup(membro, identificador);
				}
			}
		} 
		else {
			getGenericManager().flush();
			if (identificador.startsWith("/")) {
				IdentityManager.instance().addRoleToGroup("admin", identificador);
			}
		}
		final String nome = getInstance().getNome();
		setInstance(papelManager.getPapelByIdentificador(getRoleaction().getRole()));
		getInstance().setNome(nome);
		updatePermissions();
		getGenericManager().flush();
		clear();
		return save;
	}
	
	private void updatePermissions(){
		if (recursosDisponiveis == null) {
	    	return;
	    }
	    final String identificador_ = getInstance().getIdentificador();
	    final PermissionManager permissionManager = PermissionManager.instance();
        permissionManager.revokePermissions(addPermissions(identificador_, recursosDisponiveis));
        permissionManager.grantPermissions(addPermissions(identificador_, recursos));
	}

    private List<Permission> addPermissions(final String identificador, final List<String> permissionsToAdd) {
        final List<Permission> result = new ArrayList<>();
        for (final String recurso : permissionsToAdd) {
            result.add(new Permission(recurso, ACCESS, new Role(identificador)));
        }
        return result;
    }
	
	@Observer("roleTreeHandlerSelected")
	public void treeSelected(final Papel papel) {
		setPapelId(papel.getIdPapel());
		setTab("form");
		if (papel.getIdentificador().startsWith("/")) {
			final Redirect redirect = Redirect.instance();
			redirect.setViewId("/useradmin/recursoListView.xhtml");
			redirect.execute();
		}
	}
	
	public static PapelCrudAction instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
}