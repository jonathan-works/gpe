package br.com.infox.epp.menu;

import java.util.List;

import br.com.infox.epp.access.component.menu.MenuItem;

public interface MenuHandler {

	boolean isSelected(MenuItem menuItem);

	String selectNavBarMenuItem(MenuItem menu);

	String selectMainMenuItem(MenuItem item);

	List<MenuItem> getNavBarMenu();

	List<MenuItem> getActionMenu();

	List<MenuItem> getMainMenu();

	boolean isShowMenu();

}