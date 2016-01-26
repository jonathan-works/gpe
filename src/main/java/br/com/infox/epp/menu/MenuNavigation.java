package br.com.infox.epp.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.jboss.seam.Component;
import org.jboss.seam.security.Identity;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.component.menu.Menu;
import br.com.infox.epp.access.component.menu.MenuItem;

@Named
@SessionScoped
public class MenuNavigation implements Serializable, MenuHandler {

	private static final long serialVersionUID = 1L;

	private List<MenuItem> navBarMenu;
	private List<MenuItem> actionMenu;
	private MenuItem mainMenuItem;
	private MenuItem navBarItem;

	@PostConstruct
	public void start() {
		navBarMenu = new ArrayList<>();
		actionMenu = new ArrayList<>();
	}
	
	public void refresh(){
		navBarMenu.clear();
		actionMenu.clear();
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
		this.actionMenu.clear();
		this.navBarItem = menu;
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
		navBarMenu.clear();
		this.mainMenuItem = item;
		if (this.mainMenuItem != null) {
			boolean hasActionItem=false;
			for (MenuItem menuItem : this.mainMenuItem.getChildren()) {
				if (menuItem.getUrl() == null || menuItem.getUrl().trim().isEmpty()) {
					navBarMenu.add(menuItem);
				}else{
					hasActionItem = true;
				}
			}
			addMenuUsuario(navBarMenu);
			if (hasActionItem){
				navBarMenu.add(0, mainMenuItem);
			}
			
			if ( mainMenuItem.getUrl() != null && !mainMenuItem.getUrl().trim().isEmpty()) {
				result = mainMenuItem.getUrl();
			} else if (!navBarMenu.isEmpty()) {
				result = selectNavBarMenuItem(navBarMenu.get(0));
			}
		}
		return result;
	}

	@Override
	public List<MenuItem> getNavBarMenu() {
		return navBarMenu;
	}

	@Override
	public List<MenuItem> getActionMenu() {
		return actionMenu;
	}

	@Override
	public List<MenuItem> getMainMenu() {
		List<MenuItem> mainMenu = getMenuItems();
		if (mainMenu!=null){
			addLogoutMenuItem(mainMenu);
			if (!mainMenu.isEmpty() && mainMenuItem == null) {
				selectMainMenuItem(mainMenu.get(0));
			}
		}
		return mainMenu;
	}

	private List<MenuItem> getMenuItems() {
		return ((Menu)Component.getInstance("mainMenu")).getDropMenus();
	}

	private void addMenuUsuario(List<MenuItem> menu) {
		menu.add(new MenuItem(Authenticator.getUsuarioLogado().toString(), "/Painel/list.seam"));
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
