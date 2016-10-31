package br.com.infox.core.list;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.infox.epp.cdi.config.BeanManager;

public abstract class JpaQuery<E> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Pattern patternEl = Pattern.compile("#\\{.*?\\}");
	private Integer firstResult = 0;
	private Integer maxResults;
	private Map<RestrictionField, Object> restrictionsParams = new HashMap<RestrictionField, Object>();
	private String parsedEjbql;
	private String order;
	protected List<E> resultList;
	private Long resultCount;
	private transient EntityManager entityManager;
	
	@PostConstruct
	private void init() {
		validate();
	}
	
	protected abstract String getDefaultEjbql();
	
	protected abstract Map<RestrictionField, Object> getRestrictionParams();
	
	protected abstract void addAdditionalClauses(StringBuilder sb);
	
	protected String getDefaultWhere(){ return null; }
	
	
	
	private void validate() {
		if ( getDefaultEjbql() == null ) {
			throw new IllegalStateException("ejbql is null");
		}
		if ( getEntityManager() == null ) {
			throw new IllegalStateException("entityManager is null");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<E> getResultList()	{
		if (isAnyParameterDirty()) refresh();
		if (resultList == null) {
			Query query = createQuery();
			resultList = query == null ? null : (List<E>) query.getResultList();
		}
		return truncResultList(resultList);
	}
	
	protected List<E> truncResultList(List<E> results) {
		Integer mr = getMaxResults();
		if ( mr!=null && results.size() > mr ) {
			return results.subList(0, mr);
		} else {
			return results;
		}
	}
  
   	public boolean isNextExists() {
    	return resultList != null && getMaxResults() != null && resultList.size() > getMaxResults();
   	}
    
    public boolean isPreviousExists() {
       return getFirstResult() != null && getFirstResult() != 0;
    }
    
    public Long getResultCount() {
    	if (isAnyParameterDirty()) refresh();
    	if (resultCount == null) {
    		Query query = createCountQuery();
    		resultCount = query == null ? null : (Long) query.getSingleResult();
    	}
    	return resultCount;
    }
    
    public Integer getPageCount() {
    	if ( getMaxResults()==null ) {
    	   return null;
    	} else {
    	   int rc = getResultCount().intValue();
    	   int mr = getMaxResults().intValue();
    	   int pages = rc / mr;
    	   return rc % mr == 0 ? pages : pages+1;
    	}
    }
    
    public void newInstance(){
    	refresh();
    }
    
    public void refresh() {
    	resultCount = null;
    	resultList = null;
    	parsedEjbql = null;
    }
    
    private void clearResultlist(){
    	resultList = null;
    }
	
	private Query createQuery() {
		
		parseEjbql(getDefaultEjbql());      
		Query query = getEntityManager().createQuery( getParsedEjbqlOrdered() );
		setParameters( query );
		if ( getFirstResult()!=null) query.setFirstResult( getFirstResult() );
		if ( getMaxResults()!=null) query.setMaxResults( getMaxResults()+1 );
		return query;
	}
	   
	protected Query createCountQuery() {
	   
		parseEjbql(getCountEjbql());      
		Query query = getEntityManager().createQuery( parsedEjbql );
		setParameters( query );
		return query;
	}

	private void setParameters(Query query) {
		for (RestrictionField restriction : restrictionsParams.keySet()) {
			query.setParameter(getParameterName(restriction.getName()), restrictionsParams.get(restriction));
		}
	}
	
	private String getParsedEjbqlOrdered() {
		if ( getOrder() != null ) {
			return parsedEjbql + " order by " + getOrder();
		} else {
			return parsedEjbql;
		}
	}
	
	private String getParameterName(String value) {
		String parameterName = getFieldName(value);
		if(parameterName.contains("[")) {
			parameterName = parameterName.replaceAll("[\\[\\]\"']", "");
		}
		return parameterName;
	}
	
	private void parseEjbql(String queryString) {
		boolean first = true;
		boolean hasWhereClause = getDefaultWhere() != null;
		StringBuilder sb = new StringBuilder(queryString);
		if (hasWhereClause) sb.append(" ").append(getDefaultWhere());
		for (RestrictionField fieldFilter : restrictionsParams.keySet()){
			sb.append(hasWhereClause ? " and " : (first ? " where " : " and "));
			String parameterName = getParameterName(fieldFilter.getName());
			sb.append(fieldFilter.getExpression().replace(getEl(fieldFilter.getExpression()), ":"+ parameterName));
			first = false;
		}
		addAdditionalClauses(sb);
		this.parsedEjbql = sb.toString();
	}


	private String getCountEjbql() {		
		String countQuery = getDefaultEjbql();
		if (countQuery.startsWith("select distinct")) {			
			countQuery = countQuery.replaceFirst("select.*from", "select count(distinct o) from");
		} else {
			countQuery = countQuery.replaceFirst("select.*from", "select count(*) from");
		}		
		countQuery = countQuery.replaceAll("join\\s+fetch", "join");
		return countQuery;
	}
	
	protected String getEl(String restriction) {
		Matcher matcher = patternEl.matcher(restriction);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
	
	protected EntityManager getEntityManager(){
		if (entityManager == null) {
			entityManager = BeanManager.INSTANCE.getReference(EntityManager.class);
		}
		return entityManager;
	}

	public Integer getFirstResult() {
		return firstResult;
	}
	
	public void setFirstResult(Integer firstResult) {
		if (this.firstResult != null && !this.firstResult.equals(firstResult)) 
			clearResultlist();
		this.firstResult = firstResult;		
	}
	
	public Integer getMaxResults() {
		return maxResults;	
	}
	
	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}
	
	public String getOrder() {
		return order;
	}
	
	public void setOrder(String order) {
		if (this.order != null &&  !this.order.equals(order)) clearResultlist();
		this.order = order;
	}
	
	protected void addRestrictionsParams(RestrictionField restrictionField, Object value) {
		if (restrictionField != null && value != null)
			restrictionsParams.put(restrictionField, value);
	}
	
	protected String getFieldName(String field){
		if ( !field.contains(".") ) {
			return field;		
		}
		return field.split("\\.")[0];
	}
	
	private boolean isAnyParameterDirty() {
		Map<RestrictionField, Object> newFields = getRestrictionParams();
		boolean mudou = false;
		if (newFields.size() != restrictionsParams.size()){
			mudou = true;
		} else {
			for (RestrictionField rf : newFields.keySet()){
				if (!newFields.get(rf).equals(restrictionsParams.get(rf))){
					mudou = true;
					break;
				}
			}
		}
		if (mudou){
			restrictionsParams.clear();
			restrictionsParams.putAll(newFields);
		}		
		return mudou;
	}
}
