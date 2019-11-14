package br.com.infox.epp.fluxo.categoria.api;

import javax.enterprise.context.RequestScoped;

import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.kernel.repository.JPARepository;

@RequestScoped
public class CategoriaJPARepository extends JPARepository<Categoria, Integer> implements CategoriaRepository {

	@Override
	protected Class<Categoria> getEntityClass() {
		return Categoria.class;
	}
}
