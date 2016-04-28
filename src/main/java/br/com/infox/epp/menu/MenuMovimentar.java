package br.com.infox.epp.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import br.com.infox.epp.access.component.menu.MenuItem;

@Dependent
public class MenuMovimentar implements Serializable, MenuHandler {

	private static final long serialVersionUID = 1L;

	private List<MenuItem> mainMenu;
	private MenuItem mainMenuItem;
	private MenuItem navBarMenuItem;
	private MenuItem actionMenuItem;

	@PostConstruct
	public void start(){
		this.mainMenu = new ArrayList<>();
//		mainMenu.add(new MenuItem("#{infoxMessages['movimentar.documentos.titleTab']}"));
		mainMenu.add(new MenuItem("Atividade", "fragmentoAtividade.xhtml"));
		mainMenu.add(new MenuItem("#{infoxMessages['variavelProcesso.variaveisProcesso']}", "fragmentoVariaveisProcesso.xhtml"));
//		mainMenu.add(new MenuItem("#{infoxMessages['movimentar.documentos.titleTab']}"));
//		mainMenu.add(new MenuItem("#{infoxMessages['participanteProcesso.participantes']}"));
//		mainMenu.add(new MenuItem("#{infoxMessages['movimentar.anexar.titleTab']}"));
//		mainMenu.add(new MenuItem("Comunicação"));
//		mainMenu.add(new MenuItem("Comunicação Interna"));
//		mainMenu.add(new MenuItem("#{infoxMessages['movimentar.movimentacoes.titleTab']}"));
//		mainMenu.add(new MenuItem("#{infoxMessages['documentoFisico.titleTab']}"));
//		mainMenu.add(new MenuItem("#{infoxMessages['pasta.pastaRestricaoTab']}"));
//		mainMenu.add(new MenuItem("#{infoxMessages['relacionamentoProcesso.titleTab']}"));
//		mainMenu.add(new MenuItem("#{infoxMessages['variavelProcesso.variaveisProcesso']}",""));
		if (mainMenu.size() > 0){
			selectMainMenuItem(mainMenu.iterator().next());
		}
	}
	
	@Override
	public boolean isSelected(MenuItem menuItem) {
		return false;
	}

	@Override
	public String selectNavBarMenuItem(MenuItem menu) {
		return "";
	}

	public MenuItem getNavBarMenuItem() {
		return navBarMenuItem;
	}
	
	public MenuItem getActionMenuItem() {
		return actionMenuItem;
	}
	
	public MenuItem getMainMenuItem(){
		List<MenuItem> mainMenu = getMainMenu();
		if (mainMenuItem == null && mainMenu != null && mainMenu.size() > 1){
			mainMenuItem = mainMenu.iterator().next();
		}
		return mainMenuItem;
	}
	
	@Override
	public String selectMainMenuItem(MenuItem item) {
		this.mainMenuItem = item;
		return "";
	}

	@Override
	public List<MenuItem> getNavBarMenu() {
		return new ArrayList<>();
	}

	@Override
	public List<MenuItem> getActionMenu() {
		return new ArrayList<>();
	}

	@Override
	public List<MenuItem> getMainMenu() {
		if (mainMenu == null){
			start();
		}
		return mainMenu;
	}

	@Override
	public boolean isShowMenu() {
		return true;
	}

	@Override
	public void refresh() {
		
	}

}
