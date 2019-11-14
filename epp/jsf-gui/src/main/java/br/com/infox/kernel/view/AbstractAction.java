package br.com.infox.kernel.view;

import javax.annotation.PostConstruct;

import br.com.infox.kernel.manager.CrudManager;
import br.com.infox.kernel.repository.Repository;
import br.com.infox.kernel.view.compatibility.EppCrudTabController;

public abstract class AbstractAction<T, K> implements CrudTabController, EppCrudTabController {

	private T instance;
	private String activeTab = CrudTabController.SEARCH_TAB;

	@PostConstruct
	protected void init() {
		newInstance();
	}

	public T getInstance() {
		return instance;
	}

	public void setInstance(T instance) {
		this.instance = instance;
	}

	public void setId(K id) {
		if (id == null) {
			instance = null;
		} else {
			instance = getRepository().getById(id);
		}
	}

	public void create() {
		instance = getCrudManager().create(instance);
	}

	public void update() {
		instance = getCrudManager().update(instance);
	}

	public void delete() {
		getCrudManager().delete(instance);
		setInstance(null);
	}

	public void inactive(T entity) {
		getCrudManager().inactive(entity);
	}
	
	@Override
	public String getActiveTab() {
		return this.activeTab;
	}
	
	@Override
	public void setActiveTab(String tab) {
		this.activeTab = tab;
	}
	
	@Override
	public void onClickFormTab() {
	}
	
	@Override
	public void onClickSearchTab() {
		newInstance();
	}
	
	@Override
	public String getTab() {
		return getActiveTab();
	}
	
	@Override
	public void setTab(String tab) {
		setActiveTab(tab);
	}
	
	public abstract void newInstance();
	protected abstract CrudManager<T> getCrudManager();
	protected abstract Repository<T, K> getRepository();
}