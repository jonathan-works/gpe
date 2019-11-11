package br.com.infox.epp.fluxo.item.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.Item_;
import br.com.infox.kernel.repository.JPARepository;

@RequestScoped
public class ItemJPARepository extends JPARepository<Item, Integer> implements ItemRepository {

	@Override
	public Set<Item> getFolhas(Item pai) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Item> query = cb.createQuery(Item.class);
		Root<Item> root = query.from(Item.class);
		Predicate predicate = cb.like(root.get(Item_.caminhoCompleto), pai.getCaminhoCompleto() + "%");
		
		Subquery<Integer> subquery = query.subquery(Integer.class);
		Root<Item> fromSubQuery = subquery.distinct(true).from(Item.class);
		subquery.where(cb.isNotNull(fromSubQuery.get(Item_.itemPai)));
		subquery.select(fromSubQuery.get(Item_.idItem));
		
		predicate = cb.and(predicate, root.get(Item_.idItem).in(subquery).not());
		query.where(predicate);
		
		return new HashSet<Item>(entityManager.createQuery(query).getResultList());
	}

	@Override
	protected Class<Item> getEntityClass() {
		return Item.class;
	}

	@Override
	public List<Item> getItens(int lowerBound, int maxResults) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Item> query = cb.createQuery(Item.class);
		Root<Item> from = query.from(Item.class);
		query.orderBy(cb.asc(from.get(Item_.caminhoCompleto)));
		return entityManager.createQuery(query).setFirstResult(lowerBound).setMaxResults(maxResults).getResultList();
	}
}
