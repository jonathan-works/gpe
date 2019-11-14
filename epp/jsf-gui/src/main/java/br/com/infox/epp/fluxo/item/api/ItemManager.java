package br.com.infox.epp.fluxo.item.api;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.kernel.manager.CrudManager;

@RequestScoped
public class ItemManager implements CrudManager<Item> {
	@Inject
	private ItemRepository itemRepository;

	@Override
	@Transactional
	public Item create(Item item) {
		return itemRepository.create(item);
	}
	
	@Override
	@Transactional
	public Item update(Item item) {
		return itemRepository.update(item);
	}
	
	@Override
	@Transactional
	public Item delete(Item item) {
		return itemRepository.delete(item);
	}

	@Override
	public void inactive(Item entity) {
		entity.setAtivo(false);
		update(entity);
	}
}