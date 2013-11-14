package br.com.infox.access.home;

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
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.security.Role;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.action.RoleAction;

import br.com.infox.access.RolesMap;
import br.com.infox.access.entity.Papel;
import br.com.infox.epp.manager.PapelManager;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(PapelHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class PapelHome extends AbstractHome<Papel> {
	
	private static final String RECURSOS_TAB_ID = "recursosTab";

    private static final String PAPEIS_TAB_ID = "papeisTab";

    private static final String MEMBROS_TAB_ID = "herdeirosTab";

    public static final String NAME = "papelHome";

	private static final long serialVersionUID = 1L;
	
	private Map<Boolean, List<String>> papeisDisponiveis;
	private Map<String, Papel> papelMap;

	private List<String> membros;
	private Map<String, Papel> membrosMap;

	private String identificador;

	private List<String> recursosDisponiveis;

	private List<String> papeis;

	private List<String> recursos;
	
	private String activeInnerTab;
	private boolean acceptChange=false;
	
	@In private PapelManager papelManager;
	
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

	public void setPapelId(Integer id) {
		Object oid = getId();
		if (oid == null || !oid.equals(id)) {
			super.setId(id);
			Conversation.instance().end();
			clear();
			Papel p = getInstance();
			identificador = p.getIdentificador();
			RoleAction action = getRoleaction();
			action.editRole(p.getIdentificador());
		}
	}

	public List<String> getMembros() {
		if (membros == null) {
			membros = new ArrayList<String>();
			membrosMap = new HashMap<String, Papel>();
			final List<Principal> list = new ArrayList<Principal>();
			new RunAsOperation(true) {
				@Override
				public void execute() {
					list.addAll(IdentityManager.instance().listMembers(
							getInstance().getIdentificador()));
				}
			}.run();
			if (list.isEmpty()) {
				return new ArrayList<String>();
			}
			List<String> idPapeis = new ArrayList<String>();
			for (Principal principal : list) {
				idPapeis.add(principal.getName());
			}
			List<Papel> papelList = papelManager.getPapeisByListaDeIdentificadores(idPapeis);
			for (Papel papel : papelList) {
				String id = papel.getIdentificador();
				membros.add(id);
				membrosMap.put(id, papel);
			}
			Collections.sort(membros);
		}
		return membros;
	}
	
	public void setMembros(List<String> membros){
	    if (acceptChange || MEMBROS_TAB_ID.equals(activeInnerTab)) {
	        acceptChange=false;
	        this.membros = membros;
	    }
	}
	
	private RoleAction getRoleaction() {
		return ComponentUtil.getComponent("org.jboss.seam.security.management.roleAction");
	}
	
	@Override
	public void newInstance() {
		super.newInstance();
		clear();
		Contexts.removeFromAllContexts("org.jboss.seam.security.management.roleAction");
	}
	
	@Override
	public String inactive(Papel instance) {
		return remove(instance);
	}
	
	@Override
	public String remove(Papel p) {
		setInstance(p);
		String ret = super.remove();
		newInstance();
		RolesMap.instance().clear();
		return ret;
	}
	
	public String getNome(String identificador) {
		if (papelMap != null && papelMap.containsKey(identificador)) {
			return papelMap.get(identificador).toString();
		}
		return null;
	}
	
	public List<String> getPapeis() {
		if (papeis == null) {
			papeis = getRoleaction().getGroups();
			if (papeis == null) {
				papeis = new ArrayList<String>();
			} else {
				removeRecursos(papeis);
			}
		}
		return papeis;
	}

	public void setPapeis(List<String> papeis) {
	    if (acceptChange || PAPEIS_TAB_ID.equals(activeInnerTab)) {
	        acceptChange=false;
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
	public List<String> getPapeisDisponiveis(boolean removeMembros) {
		if (papeisDisponiveis == null) {
			papeisDisponiveis = new HashMap<Boolean, List<String>>();
		}
		if (!papeisDisponiveis.containsKey(removeMembros)) {
			List<String> assignableRoles = getRoleaction().getAssignableRoles();
			papeisDisponiveis.put(removeMembros, assignableRoles);
			removePapeisImplicitos(assignableRoles, getPapeis());
			removeRecursos(assignableRoles);
			if (isManaged() && removeMembros) {
				removeMembros(instance.getIdentificador(), assignableRoles);
			} else {
				assignableRoles.removeAll(papeis);
			}
			if (papelMap == null) {
				papelMap = new HashMap<String, Papel>();
			}
			List<Papel> papelList = papelManager.getPapeisByListaDeIdentificadores(assignableRoles);
			for (Papel p : papelList) {
				papelMap.put(p.getIdentificador(), p);
			}
			Collections.sort(assignableRoles, new Comparator<String>(){
				@Override
				public int compare(String o1, String o2) {
					String n1 = papelMap.get(o1).toString();
					String n2 = papelMap.get(o2).toString();
					return n1.compareTo(n2);
				}
			});
		}
		return papeisDisponiveis.get(removeMembros);
	}

	public List<String> getRecursos() {
		if (recursos == null) {
			if (IdentityManager.instance().roleExists(getInstance().getIdentificador())) {
				recursos = IdentityManager.instance().getRoleGroups(getInstance().getIdentificador());
				removePapeis(recursos);
			} else {
				recursos = new ArrayList<String>();
			}
		}
		return recursos;
	}
	
	public void setRecursos(List<String> recursos) {
	    if (acceptChange || RECURSOS_TAB_ID.equals(activeInnerTab)) {
	        acceptChange=false;
	        this.recursos = recursos;
	    }
	}

	public String getActiveInnerTab() {
        return activeInnerTab;
    }

    public void setActiveInnerTab(String activeInnerTab) {
        this.acceptChange = true;
        this.activeInnerTab = activeInnerTab;
    }

    public List<String> getRecursosDisponiveis() {
		if (recursosDisponiveis == null) {
			recursosDisponiveis = getRoleaction().getAssignableRoles();
			removePapeisImplicitos(recursosDisponiveis, getPapeis());
			removePapeisImplicitos(recursos, getPapeis());
			removePapeis(recursosDisponiveis);
			if (papelMap == null) {
				papelMap = new HashMap<String, Papel>();
			}
			List<Papel> papelList = papelManager.getPapeisByListaDeIdentificadores(recursosDisponiveis);
			for (Papel p : papelList) {
				papelMap.put(p.getIdentificador(), p);
			}
			Collections.sort(recursosDisponiveis, new Comparator<String>(){
				@Override
				public int compare(String o1, String o2) {
					String n1 = papelMap.get(o1).toString();
					String n2 = papelMap.get(o2).toString();
					return n1.compareTo(n2);
				}
			});
		}
		return recursosDisponiveis;
	}

	private void removeRecursos(List<String> roles) {
		for (Iterator<String> iterator = roles.iterator(); iterator.hasNext();) {
			String papelId = iterator.next();
			if (papelId.startsWith("/")) {
				iterator.remove();
			}
		}
	}
	
	private void removePapeis(List<String> roles) {
		for (Iterator<String> iterator = roles.iterator(); iterator.hasNext();) {
			String papelId = iterator.next();
			if (!papelId.startsWith("/")) {
				iterator.remove();
			}
		}
	}

	private void removePapeisImplicitos(List<String> list, List<String> from) {
		if (from == null) {
			return;
		}
		// ser for o mesmo objeto, clona para evitar ConcurrentModificationException
		if (from.equals(list)) {
			from = new ArrayList<String>(list);
		}
		for (String papel : from) {
			removePapeisImplicitos(papel, list);
		}
	}

	/**
	 * Remove o papel da lista, recursivamente
	 * @param papel
	 */
	private void removePapeisImplicitos(final String papel, List<String> list) {
		for (final String p : IdentityManager.instance().getRoleGroups(papel)) {
			list.remove(p);
			EntityUtil.getEntityManager().flush();
			removePapeisImplicitos(p, list);
		}
	}
	
	private void removeMembros(final String papel, List<String> roles) {
		final List<Principal> listMembers = new ArrayList<Principal>();
		new RunAsOperation(true) {
			@Override
			public void execute() {
				listMembers.addAll(IdentityManager.instance().listMembers(papel));
			}
		}.run();
		for (Principal p : listMembers) {
			if (p instanceof Role) {
				roles.remove(p.getName());
				removeMembros(p.getName(), roles);
			}
		}
	}

	public String save() {
		FacesMessages messages = FacesMessages.instance();
		
		if (IdentityManager.instance().roleExists(instance.getIdentificador()) && !isManaged()) {
			messages.add("#{messages['constraintViolation.registroCadastrado']}");
			return null;
		}

		final StringBuilder ret = new StringBuilder();
		
		new RunAsOperation(true) {
			@Override
			public void execute() {
				ret.append(saveOp());
			}
		}.run();
		RolesMap.instance().clear();
		
		if ("success".equals(ret.toString())) {
		    messages.add("#{messages['entity_updated']}");
		} else {
		    messages.add("#{messages['constraintViolation.registroCadastrado']}");
		}
		
		return ret.toString();
	}
	
	public String saveOp() {
		identificador = getInstance().getIdentificador();
		getRoleaction().setRole(identificador);
		papeis = new ArrayList<String>(getPapeis());
		papeis.addAll(getRecursos());
		removePapeisImplicitos(papeis, papeis);
		getRoleaction().setGroups(papeis);
		String save = getRoleaction().save();
		if (isManaged()) {
			if (membros != null) {
				List<String> incluirMembros = new ArrayList<String>(membros);
				incluirMembros.removeAll(membrosMap.keySet());
				for (String membro : incluirMembros) {
					IdentityManager.instance().addRoleToGroup(membro, identificador);
				}
				List<String> excluirMembros = new ArrayList<String>(membrosMap.keySet());
				excluirMembros.removeAll(membros);
				for (String membro : excluirMembros) {
					IdentityManager.instance().removeRoleFromGroup(membro, identificador);
				}
			}
		} 
		else {
			EntityUtil.flush();
			if (identificador.startsWith("/")) {
				IdentityManager.instance().addRoleToGroup("admin", identificador);
			}
		}
		String nome = instance.getNome();
		instance = papelManager.getPapelByIdentificador(getRoleaction().getRole());
		instance.setNome(nome);
		EntityUtil.flush();
		clear();
		return save;
	}
	
	@Observer("roleTreeHandlerSelected")
	public void treeSelected(Papel papel) {
		setPapelId(papel.getIdPapel());
		setTab("form");
		if (papel.getIdentificador().startsWith("/")) {
			Redirect redirect = Redirect.instance();
			redirect.setViewId("/useradmin/recursoListView.xhtml");
			redirect.execute();
		}
	}
	
	public static PapelHome instance() {
		return ComponentUtil.getComponent("papelHome");
	}
}