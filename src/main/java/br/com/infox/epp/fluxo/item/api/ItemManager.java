package br.com.infox.epp.fluxo.item.api;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.infox.cdi.annotations.Transactional;
import br.com.infox.epp.fluxo.entity.Item;

@RequestScoped
public class ItemManager {
	@Inject
	private ItemRepository itemRepository;

	@Transactional
	public Item create(Item item) {
		return itemRepository.create(item);
	}
	
	@Transactional
	public Item update(Item item) {
		return itemRepository.update(item);
	}
	
	@Transactional
	public Item delete(Item item) {
		return itemRepository.delete(item);
	}
}