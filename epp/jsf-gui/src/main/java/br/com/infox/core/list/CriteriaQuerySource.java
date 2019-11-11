package br.com.infox.core.list;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

public interface CriteriaQuerySource<T> {

	public CriteriaQuery<T> createQuery(CriteriaBuilder cb);

    public static interface FilterBuilder extends FiltroOperadores, NotNullBuilder, BuilderAtalhos {
    	//FilterBuilder addIfNotNull(Object atributoFiltro, Predicate restriction);
    	public List<Predicate> getRestrictions();
    }
    
    public static interface NotNullBuilder extends FiltroOperadores {
    	FiltroOperadores ifNotNull(Object atributo);
    }
    
    public static interface BuilderAtalhos {
    	FilterBuilder equalIfNotNull(Expression<?> expression, Object atributoFiltro);
    }
    
    public static interface FiltroOperadores {
    	FilterBuilder add(Predicate restriction);
    	FilterBuilder equal(Expression<?> expression, Object atributoFiltro);
    }
    
    public static class FiltroOperadoresConditional implements FiltroOperadores {
    	private FilterBuilder filterBuilder;
    	private Object condition;
    	
    	public FiltroOperadoresConditional(FilterBuilder filterBuilder, Object condition) {
    		this.filterBuilder = filterBuilder;
    		this.condition = condition;
		}

		public FilterBuilder add(Predicate restriction) {
			if(condition != null) {
				filterBuilder.add(restriction);
			}
			return filterBuilder;
		}

		public FilterBuilder equal(Expression<?> expression, Object atributoFiltro) {
			if(condition != null) {
				filterBuilder.equal(expression, atributoFiltro);
			}
			return filterBuilder;
		}
    }
    
    public static class BuilderImpl implements FilterBuilder {

    	private CriteriaBuilder cb;
    	private List<Predicate> restrictions = new ArrayList<>();
    	
    	public BuilderImpl(CriteriaBuilder cb) {
    		this.cb = cb;
    	}
    	
		@Override
		public List<Predicate> getRestrictions() {
			return restrictions;
		}

		@Override
		public FilterBuilder add(Predicate restriction) {
			restrictions.add(restriction);
			return this;
		}

		@Override
		public FilterBuilder equal(Expression<?> expression, Object atributoFiltro) {
			restrictions.add(cb.equal(expression, atributoFiltro));
			return this;
		}

		@Override
		public FiltroOperadores ifNotNull(Object atributo) {
			FiltroOperadores retorno = new FiltroOperadoresConditional(this, atributo);
			return retorno;
		}

		@Override
		public FilterBuilder equalIfNotNull(Expression<?> expression, Object atributoFiltro) {
			return ifNotNull(atributoFiltro).equal(expression, atributoFiltro);
		}

    }
	
	
}
