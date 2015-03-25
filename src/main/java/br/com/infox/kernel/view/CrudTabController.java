package br.com.infox.kernel.view;

public interface CrudTabController extends TabController {
	String SEARCH_TAB = "search";
	String FORM_TAB = "form";
	
	void onClickSearchTab();
	void onClickFormTab();
}
