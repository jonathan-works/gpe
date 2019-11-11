package br.com.infox.epp.fluxo.item.api;

import java.util.List;
import java.util.Set;

import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.kernel.repository.Repository;

public interface ItemRepository extends Repository<Item, Integer> {
	Set<Item> getFolhas(Item pai);
	List<Item> getItens(int lowerBound, int maxResults);
}
