package br.com.infox.core.list;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.infox.cdi.producer.EntityManagerProducer;

public class CriteriaLazyDataModel<T> extends LazyDataModel<T> {
	private static final long serialVersionUID = 1L;
	
	private int aliasCount = 0;
	private CriteriaQuerySource<T> criteriaQuerySource;
	
    private LoadListener<T> loadListener;
    
    public static interface LoadListener<T> {
    	public void load(List<T> entities);
    }
    
    public CriteriaLazyDataModel(CriteriaQuerySource<T> criteriaQuerySource) {
		super();
		this.criteriaQuerySource = criteriaQuerySource;
	}

	@SuppressWarnings("unchecked")
	public static  <T> Root<T> findRoot(CriteriaQuery<?> query, Class<T> entityClass) {
		for (Root<?> root : query.getRoots()) {
			if (entityClass.equals(root.getJavaType())) {
				return (Root<T>) root.as(entityClass);
			}
		}
		return (Root<T>) query.getRoots().iterator().next();
	}
	
	public String getOrCreateAlias(Selection<?> selection) {
		String alias = selection.getAlias();
		if (alias == null) {
			alias = "_" + selection.getJavaType().getSimpleName() + "_" + aliasCount++;
			selection.alias(alias);
		}
		return alias;
		
	}
	
	private <V> CriteriaQuery<V> cloneQueryWithoutSelectAndOrder(CriteriaBuilder cb, CriteriaQuery<?> src, Class<V> cloneClass, boolean excludeFetches) {
		aliasCount = 0;			
		
		CriteriaQuery<V> clone = cb.createQuery(cloneClass);
		
		for(Root<?> root : src.getRoots()) {
			Root<?> rootClone = clone.from(root.getJavaType());
			rootClone.alias(getOrCreateAlias(root));
			copyJoins(root, rootClone);
			if(!excludeFetches) {
				copyFetches(root, rootClone);
			}
		}
		
		clone.distinct(src.isDistinct());
		
		clone.groupBy(src.getGroupList());
		if(src.getGroupRestriction() != null) {
			clone.having(src.getGroupRestriction());
		}
		
		Predicate where = src.getRestriction();
		if(where != null) {
			clone.where(where);
		}
		
		return clone;
	}
	
	private void copyJoins(From<?,?> src, From<?, ?> dest) {
		for(Join<?, ?> srcJoin : src.getJoins()) {
			Join<?, ?> destJoin = dest.join(srcJoin.getAttribute().getName(), srcJoin.getJoinType());
			destJoin.alias(getOrCreateAlias(src));
			copyJoins(srcJoin, destJoin);
		}
	}
	
	private void copyFetches(FetchParent<?,?> src, FetchParent<?, ?> dest) {
		for(Fetch<?, ?> srcFetch : src.getFetches()) {
			Fetch<?, ?> destFetch = dest.fetch(srcFetch.getAttribute().getName(), srcFetch.getJoinType());
			copyFetches(srcFetch, destFetch);
		}
	}
	
	private CriteriaQuery<Long> createCountQuery(CriteriaBuilder cb) {
		CriteriaQuery<?> cq = criteriaQuerySource.createQuery(cb);
		
		CriteriaQuery<Long> countCq = cloneQueryWithoutSelectAndOrder(cb, cq, Long.class, true);
		
		Root<?> root = findRoot(countCq, cq.getResultType());
		Expression<Long> select = cq.isDistinct() ? cb.countDistinct(root) : cb.count(root);
		
		countCq.select(select);
		
		return countCq;
	}
	
	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		
		CriteriaQuery<Long> countCq = createCountQuery(cb);
		TypedQuery<Long> countTq = getEntityManager().createQuery(countCq);
		Long count = countTq.getSingleResult();
		setRowCount(count.intValue());

		CriteriaQuery<T> cq = criteriaQuerySource.createQuery(cb);
		TypedQuery<T> query = getEntityManager().createQuery(cq);
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		
		List<T> retorno = query.getResultList();
		
		if(loadListener != null) {
			loadListener.load(retorno);
		}
		
		return retorno;
	}

	public EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

	public void setLoadListener(LoadListener<T> loadListener) {
		this.loadListener = loadListener;
	}
	
}
