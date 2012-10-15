package br.com.infox.access;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.international.Messages;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.JpaIdentityStore;
import org.richfaces.event.DropEvent;

import br.com.infox.access.entity.Papel;
import br.com.itx.util.EntityUtil;

/**
 * Monta o menu do usuário baseado nas permissões de acesso às páginas
 * @author luizruiz
 *
 */
public class Menu implements Serializable {

	private static final long serialVersionUID = 1L;

	protected List<MenuItem> bookmark = new ArrayList<MenuItem>();

	protected List<MenuItem> dropMenus;
	
	/**
	 * Flag para indicar que as páginas já foram verificadas
	 * Isso ocorre apenas uma vez (static)
	 */
	private static boolean pagesChecked;
	
	public void drop(DropEvent o) {
		bookmark.add((MenuItem) o.getDragValue());
	}
 
	/**
	 * Cria o menu, verificando para cada ítem se o usuário é admin ou 
	 * tem um role no formato SecurityUtil.PAGES_PREFIX 
	 * seguido da url da página a ser acessada
	 * 
	 * @param items
	 */
	public void setItems(List<String> items) {
		dropMenus = new ArrayList<MenuItem>();
		boolean ok = true;
		for (String key : items) {
			try {
				String[] split = key.split(":");
				key = split[0];				
				String url = null;
				if (split.length > 1) {
					url = split[1];
				}
				String pageRole = SecurityUtil.PAGES_PREFIX + url;
				if (!pagesChecked && Identity.instance().isLoggedIn()) {
					checkPage(pageRole, key);
				}
				if (Identity.instance().hasRole(pageRole)) {
					buildItem(key, url);
				}
			} catch (Exception e) {
				e.printStackTrace();
				ok = false;
				break;
			}
		}
		pagesChecked = ok;
	}

	/**
	 * Verifica se existe o role para a página, se não existir
	 * registra o role e atribui permissão ao role admin
	 * 
	 * @param pageRole
	 */
	private void checkPage(final String pageRole, String name) {
		if (! IdentityManager.instance().roleExists(pageRole)) {
			new RunAsOperation(true) {
				@Override
				public void execute() {
					IdentityManager.instance().createRole(pageRole);
					EntityUtil.getEntityManager().flush();
				}
			}.run();
			JpaIdentityStore store = (JpaIdentityStore) 
					IdentityManager.instance().getRoleIdentityStore();
			Papel role = (Papel) store.lookupRole(pageRole);
			String[] parts = name.split("/");
			StringBuilder sb = new StringBuilder();
			for (String s : parts) {
				if (!sb.toString().isEmpty()) {
					sb.append("/");
				}
				sb.append(Messages.instance().get(s));
			}
			role.setNome("Página " + sb.toString());
			new RunAsOperation(true) {
				@Override
				public void execute() {
					IdentityManager.instance().addRoleToGroup("admin", pageRole);
				}
			}.run();
			if (Identity.instance().hasRole("admin")) {
				Identity.instance().addRole(pageRole);
			}
		}
	}

	protected void buildItem(String key, String url) throws Exception {
		if (key.startsWith("/")) {
			key = key.substring(1);
		}
		String[] groups = key.split("/");
		MenuItem parent = new MenuItem(null);
		for (int i = 0; i < groups.length; i++) {
			String label = groups[i];
			if (!label.startsWith("#{messages['")) {
				label = "#{messages['" + label + "']}";
			}
			MenuItem item = new MenuItem(label);
			if (i == 0) {
				int j = dropMenus.indexOf(item);
				if (j != -1) {
					parent = dropMenus.get(j);
				}
				else {
					parent = item;
					if (groups.length == 1) {
						parent.setUrl(url);
					}
					dropMenus.add(parent);
				}
			}
			else if (i < (groups.length - 1)) {
				parent = parent.add(item);
			}
			else {
				item.setUrl(url);
				parent.getChildren().add(item);
			}
		}
	}
	
	public List<MenuItem> getBookmark() {
		return bookmark;
	}

	public List<MenuItem> getDropMenus() {
		return dropMenus;
	}

	public void clearMenu() {
		Contexts.removeFromAllContexts("mainMenu");
	}
	
	public static void main(String[] args) {
		List<String> items = new ArrayList<String>();
		items.add("Empresa 1:url.empresa1");
		items.add("/Cadastro/1/Empresa 2:url.empresa2");
		items.add("/Cadastro/2/Empresa 3:url.empresa3");
		items.add("/Cadastro/2/Empresa 4:url.empresa4");
		Menu m = new Menu();
		m.setItems(items);
		System.out.println(m.dropMenus);
	}

}