package br.com.infox.core.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jboss.seam.Component;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.web.ServletContexts;

import br.com.infox.core.util.ReflectionsUtil;

public abstract class DataList<E> extends JpaQuery<E> {
	
	private static final long serialVersionUID = 1L;	
	private static final int DEFAULT_MAX_RESULT = 20;
	private List<RestrictionField> restrictionFieldList = new ArrayList<RestrictionField>();
	private Map<String, String> customColumnsOrder;
	private Integer page = 1;
	private String orderedColumn;
	
	@PostConstruct
	public void init() {
		addRestrictionFields();
		customColumnsOrder = getCustomColumnsOrder();
		setOrder(getDefaultOrder());
	}
	
	@Override
	public void newInstance() {
		if (restrictionFieldList.size() != 0) clearSearchFields();
		this.orderedColumn = null;
		super.newInstance();
	}
	
	protected void addRestrictionFields(){}
	
	protected abstract String getDefaultOrder();
	
	protected Map<String, String> getCustomColumnsOrder(){ return null; }

	protected List<RestrictionField> getRestrictionFieldList() {
		return restrictionFieldList;
	}
	
	protected void addRestrictionField(String fieldName, String compareField, RestrictionType restriction) {
		addRestrictionField(new RestrictionField(getEntityListName(), fieldName, compareField, restriction));
	}
	
	protected void addRestrictionField(String fieldName, RestrictionType restriction) {
		addRestrictionField(new RestrictionField(getEntityListName(), fieldName, restriction));
	}

	protected void addRestrictionField(String fieldName, String expression) {
		addRestrictionField(new RestrictionField(getEntityListName(), fieldName, expression));
	}

	private void addRestrictionField(RestrictionField restrictionField) {
		restrictionFieldList.add(restrictionField);
	}
	
	@Override
	public List<E> getResultList() {
		Integer pageCount = getPageCount() != null ? getPageCount() : 1;
		if (page > pageCount) {
			setPage(pageCount);
		}
		Object search = ServletContexts.getInstance().getRequest().getParameterMap().get("search");
		if (search != null) refresh();
		return super.getResultList();
	}
	
	public List<E> list() {
		setMaxResults(null);
		return getResultList();
	}

	public List<E> list(int maxResult) {			
		setMaxResults(maxResult > 0 ? maxResult : DEFAULT_MAX_RESULT);
		List<E> list = getResultList();
		return list;
	}

	public void setPage(Integer page) {
		if (this.page.equals(page)) return;	
		this.page = page;
		int firstResult = (page - 1) * (getMaxResults() != null ? getMaxResults() : 0);
		if (firstResult < 0) firstResult = 0;
		super.setFirstResult(firstResult);
	}
	
	public Integer getPage() {
		return page;
	}
	
	public void setOrderedColumn(String order) {
		if(!order.endsWith("asc") && !order.endsWith("desc")) {
			order = order.trim().concat(" asc");
		}
		this.orderedColumn = order;
		String[] fields = order.split(" ");
		order = customColumnsOrder != null ? customColumnsOrder.get(fields[0])
				.concat(" ").concat(fields[1]) : getDefaultOrder();
		setOrder(order);
	}
	
	public String getOrderedColumn() {
		return orderedColumn;
	}
	
	public String getEntityListName() {
    	String componentName = ReflectionsUtil.getCdiComponentName(getClass());
    	if (componentName == null) {
    		return Component.getComponentName(this.getClass());
    	}
    	return componentName;
    }
	
	public boolean isAnyRestrictionPopulated() {
		for (RestrictionField searchField : getRestrictionFieldList()) {			
			if ( getValueExpression(getEl(searchField.getExpression())) != null) {
				return true;
			}
		}
		return false;
	}
	
	public Map<RestrictionField, Object> getRestrictionParams(){
		Map<RestrictionField, Object> map = new HashMap<RestrictionField, Object>();
		for (RestrictionField restrictionField : getRestrictionFieldList()){			
			Object value = getValueExpression(getEl(restrictionField.getExpression()));
			if (value!=null) map.put(restrictionField, value);
		}
		return map;
	}
	
	private void clearSearchFields() {
		for (RestrictionField field : getRestrictionFieldList()){
			String expression = "#{"+getEntityListName()+"."+getFieldName(field.getName())+"}";
			Expressions.instance().createValueExpression(expression).setValue(null);
		}
	}
	
	private Object getValueExpression(String expression){
		return Expressions.instance().createValueExpression(expression).getValue();
	}
	
}
