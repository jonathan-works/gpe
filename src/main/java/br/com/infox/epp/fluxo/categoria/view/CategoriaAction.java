package br.com.infox.epp.fluxo.categoria.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.com.infox.epp.fluxo.categoria.api.CategoriaManager;
import br.com.infox.epp.fluxo.categoria.api.CategoriaRepository;
import br.com.infox.epp.fluxo.entity.Categoria;

@ManagedBean
@ViewScoped
public class CategoriaAction {
	@Inject
	private CategoriaRepository categoriaRepository;
	@Inject
	private CategoriaManager categoriaManager;
	
	private Categoria instance;
	
	public Categoria getInstance() {
		return instance;
	}
	
	public void setInstance(Categoria instance) {
		this.instance = instance;
	}
	
	public void setId(Integer id) {
		if (id == null) {
			instance = null;
		} else {
			instance = categoriaRepository.getById(id);
		}
	}
	
	public void create() {
		instance = categoriaManager.create(instance);
	}
	
	public void update() {
		instance = categoriaManager.update(instance);
	}
	
	public void delete() {
		categoriaManager.delete(instance);
		setInstance(null);
	}
	
	public void inactive(Categoria categoria) {
		categoria.setAtivo(false);
		categoriaManager.update(categoria);
	}
	
	public void newInstance() {
		this.instance = new Categoria();
	}
}
