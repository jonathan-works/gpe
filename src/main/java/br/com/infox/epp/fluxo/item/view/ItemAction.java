package br.com.infox.epp.fluxo.item.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.item.api.ItemManager;
import br.com.infox.epp.fluxo.item.api.ItemRepository;

@ManagedBean
@ViewScoped
public class ItemAction {
	@Inject
	private ItemManager itemManager;
	@Inject
	private ItemRepository itemRepository;
	
	private Item instance;
	
	public Item getInstance() {
		return instance;
	}
	
	public void setInstance(Item instance) {
		this.instance = instance;
	}
	
	public void setId(Integer id) {
		if (id == null) {
			instance = null;
		} else {
			instance = itemRepository.getById(id);
		}
	}
	
	public void create() {
		instance = itemManager.create(instance);
	}
	
	public void update() {
		instance = itemManager.update(instance);
	}
	
	public void delete() {
		itemManager.delete(instance);
		setInstance(null);
	}
	
	public void inactive(Item item) {
		item.setAtivo(false);
		itemManager.update(item);
	}
	
	public void newInstance() {
		this.instance = new Item();
	}
}
