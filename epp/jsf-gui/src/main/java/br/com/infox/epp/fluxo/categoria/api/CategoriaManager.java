package br.com.infox.epp.fluxo.categoria.api;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.kernel.manager.CrudManager;

@RequestScoped
public class CategoriaManager implements CrudManager<Categoria> {

	@Inject
	private CategoriaRepository categoriaRepository;
	
	@Transactional
	public Categoria create(Categoria categoria) {
		return categoriaRepository.create(categoria);
	}
	
	@Transactional
	public Categoria update(Categoria categoria) {
		return categoriaRepository.update(categoria);
	}
	
	@Transactional
	public Categoria delete(Categoria categoria) {
		return categoriaRepository.delete(categoria);
	}

	@Override
	public void inactive(Categoria entity) {
		entity.setAtivo(false);
		update(entity);
	}
}
