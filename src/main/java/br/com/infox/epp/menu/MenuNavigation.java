package br.com.infox.epp.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.commons.lang3.ObjectUtils;
import org.jboss.seam.Component;
import org.jboss.seam.security.Identity;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.component.menu.Menu;
import br.com.infox.epp.access.component.menu.MenuItem;

@Named
@SessionScoped
public class MenuNavigation implements Serializable, MenuHandler {

	private static final long serialVersionUID = 1L;

	private MenuItem mainMenuItem;
	private MenuItem navBarItem;

	@Override
	public void refresh() {
		mainMenuItem=null;
		navBarItem=null;
	}
	
	private List<MenuItem> getActionMenuItemsFor(MenuItem menu) {
		List<MenuItem> result = new ArrayList<>();
		if (menu != null) {
			if (menu.getUrl() != null && !menu.getUrl().trim().isEmpty()) {
				result.add(menu);
			}
			for (MenuItem menuItem : menu.getChildren()) {
				result.addAll(getActionMenuItemsFor(menuItem));
			}
		}
		return result;
	}

	private boolean isCurrentView(MenuItem menuItem){
		String viewUrl=FacesContext.getCurrentInstance().getViewRoot().getViewId().replace(".xhtml", ".seam");
		return menuItem != null && menuItem.getUrl() != null && menuItem.getUrl().equals(viewUrl);
	}
	
	@Override
	public boolean isSelected(MenuItem menuItem){
		return Objects.equals(menuItem, this.navBarItem) || isCurrentView(menuItem);
	}
	
	@Override
	public String selectNavBarMenuItem(MenuItem menu) {
		String result = "";
		this.navBarItem = menu;
		List<MenuItem> actionMenu = getActionMenu();
		if (navBarItem != null) {
			if (navBarItem.getUrl() != null && !navBarItem.getUrl().trim().isEmpty()) {
				result = navBarItem.getUrl();
			} else if (!actionMenu.isEmpty()) {
				result = actionMenu.get(0).getUrl();
			}
		}
		return result;
	}

	@Override
	public String selectMainMenuItem(MenuItem item) {
		String result = "";
		this.mainMenuItem = item;
		if (this.mainMenuItem != null) {
			if ( mainMenuItem.getUrl() != null && !mainMenuItem.getUrl().trim().isEmpty()) {
				result = mainMenuItem.getUrl();
			} else {
				final List<MenuItem> navBarMenu = getNavBarMenu();
				if (!navBarMenu.isEmpty()) {
					result = selectNavBarMenuItem(navBarMenu.get(0));
				}
			}
		}
		return result;
	}

	@Override
	public List<MenuItem> getNavBarMenu() {
		List<MenuItem> navBarMenu = new ArrayList<>();
		if (mainMenuItem != null) {
			boolean hasActionItem=false;
			for (MenuItem menuItem : mainMenuItem.getChildren()) {
				if (menuItem.getUrl() == null || menuItem.getUrl().trim().isEmpty()) {
					navBarMenu.add(menuItem);
				}else{
					hasActionItem = true;
				}
			}
			if (hasActionItem){
				navBarMenu.add(0, mainMenuItem);
			}
		}
		navBarMenu.add(new MenuItem("#{infoxMessages['painel.titlePage']}", "/Painel/list.seam"));
		return navBarMenu;
	}

	@Override
	public List<MenuItem> getActionMenu() {
		List<MenuItem> actionMenu = new ArrayList<>();
		if (navBarItem != null) {
			if (!Objects.equals(navBarItem, mainMenuItem)){
				for (MenuItem menuItem : navBarItem.getChildren()) {
					actionMenu.addAll(getActionMenuItemsFor(menuItem));
				}
			} else {
				for (MenuItem menuItem : navBarItem.getChildren()) {
					if (menuItem.getUrl() != null && !menuItem.getUrl().isEmpty()){
						actionMenu.add(menuItem);
					}
				}
			}
		}
		return actionMenu;
	}

	@Override
	public List<MenuItem> getMainMenu() {
		List<MenuItem> mainMenu = new ArrayList<>();
		mainMenu.addAll(ObjectUtils.defaultIfNull(getMenuItems(), new ArrayList<MenuItem>()));
		addLogoutMenuItem(mainMenu);
		return mainMenu;
	}

	private List<MenuItem> getMenuItems() {
		return ((Menu)Component.getInstance("mainMenu")).getDropMenus();
	}

	private void addMenuUsuario(List<MenuItem> menu) {
//		UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
//		if (usuarioLogado != null) {
//			menu.add(new MenuItem(usuarioLogado.toString(), "/Painel/list.seam"));
//		}
	}

	private void addLogoutMenuItem(List<MenuItem> menu) {
		final String logoutUrl = "/login.seam";
		if (!menu.get(menu.size()-1).getUrl().equals(logoutUrl)){
			menu.add(new MenuItem("#{infoxMessages['menu.logout']}",logoutUrl));
		}
	}

	@Override
	public boolean isShowMenu() {
		return Identity.instance().isLoggedIn() && Authenticator.getUsuarioLogado().getAtivo()
				&& !Authenticator.getUsuarioLogado().getBloqueio() && !Authenticator.getUsuarioLogado().getProvisorio();
	}

}
