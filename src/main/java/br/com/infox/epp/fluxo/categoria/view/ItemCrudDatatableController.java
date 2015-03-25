package br.com.infox.epp.fluxo.categoria.view;

import java.util.List;

import javax.inject.Inject;

import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.item.api.ItemJPARepository;
import br.com.infox.kernel.view.DatatableController;

public class ItemCrudDatatableController extends DatatableController<Item> {
	@Inject
	private ItemJPARepository itemJPARepository;

	private List<Item> results;
	
	@Override
	public List<Item> getResults() {
		if (results == null) {
			results = itemJPARepository.getItens(pageCalculator.getLowerBound(getPageSize(), getPage()), getPageSize());
		}
		return results;
	}
}
