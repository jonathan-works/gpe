package br.com.infox.core.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.util.ObjectUtil;
import br.com.infox.jsf.util.JsfUtil;
import lombok.Getter;

public abstract class AbstractLazyData<T> extends LazyDataModel<T> {

    private static final long serialVersionUID = 1L;
    
    @Getter
    private Map<String, Object> filters = new HashMap<>();
    
    @SuppressWarnings("unchecked")
    public List<T> getResultList() {
        return (List<T>) super.getWrappedData();
    }
    
    public void search() {
        setWrappedData(null);
        getDataTable().reset();
        getDataTable().setRows(20);
        getDataTable().loadLazyData();
    }
    
    public int searchCount() {
        TypedQuery<Long> countQuery = createQueryCount();
        Long rowCount = countQuery.getSingleResult();
        return rowCount.intValue();
    }
    
    public void refresh() {
        setWrappedData(null);
        getDataTable().loadLazyData();
    }
    
    public void clear() {
        clearFilters();
        search();
    }
    
    public void clearFilters() {
        filters.clear();
    }

    public AbstractLazyData<T> addFilter(String key, Object value) {
        filters.put(key, value);
        return this;
    }
    
    public abstract TypedQuery<Long> createQueryCount();
    
    public abstract TypedQuery<T> createQuery(String sortField, SortOrder sortOrder);
    
    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        if (getWrappedData() == null || isPagination()) {
            int rowCount = searchCount();
            setRowCount(rowCount);
            TypedQuery<T> query = createQuery(sortField, sortOrder);
            List<T> resultList = null;
            resultList = query.setMaxResults(pageSize).setFirstResult(first).getResultList();
            return resultList;
        } else {
            return getResultList();
        }
    }
    
    public void setDataTable(DataTable dataTable) {
        JsfUtil.instance().setRequestValue(getClass().getName() + "_binding", dataTable);
    }
    
    public DataTable getDataTable() {
        return JsfUtil.instance().getRequestValue(getClass().getName() + "_binding", DataTable.class);
    }
    
    public boolean isFilterEnabled(String filterName) {
        return filters.containsKey(filterName) && !ObjectUtil.isEmpty(filters.get(filterName));
    }
    
    public boolean hasFilterEnabled() {
        for (String filterName : filters.keySet()) {
            if (isFilterEnabled(filterName)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isPagination() {
        String componentClientId = getDataTable().getClientId();
        JsfUtil jsfUtil = JsfUtil.instance();
        return jsfUtil.getRequestParameter(componentClientId + "_first") != null 
                    || jsfUtil.getRequestParameter(componentClientId + "_rows") != null;
    }
    
    public <C> C getFilter(String name, Class<C> clazz) {
        Object object = filters.get(name);
        return clazz.cast(object);
    }
    
    public void removeFilter(String name) {
        filters.remove(name);
    }
    
    protected EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
}
