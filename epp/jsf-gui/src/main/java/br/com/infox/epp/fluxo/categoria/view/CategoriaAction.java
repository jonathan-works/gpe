package br.com.infox.epp.fluxo.categoria.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.com.infox.epp.fluxo.categoria.api.CategoriaManager;
import br.com.infox.epp.fluxo.categoria.api.CategoriaRepository;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.kernel.manager.CrudManager;
import br.com.infox.kernel.repository.Repository;
import br.com.infox.kernel.view.AbstractAction;

@ManagedBean
@ViewScoped
public class CategoriaAction extends AbstractAction<Categoria, Integer> {
	@Inject
	private CategoriaRepository categoriaRepository;
	@Inject
	private CategoriaManager categoriaManager;
	
	@Override
	public void newInstance() {
		setInstance(new Categoria());
	}

	@Override
	protected CrudManager<Categoria> getCrudManager() {
		return categoriaManager;
	}

	@Override
	protected Repository<Categoria, Integer> getRepository() {
		return categoriaRepository;
	}
}
