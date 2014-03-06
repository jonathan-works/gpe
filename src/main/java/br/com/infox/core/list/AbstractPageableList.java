package br.com.infox.core.list;

import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.exception.ApplicationException;
import br.com.infox.core.manager.GenericManager;

/**
 * 
 * @author Erik Liberal
 *
 */
@Scope(ScopeType.CONVERSATION)
public abstract class AbstractPageableList<E> implements PageableList<E>, Serializable {

    private final class HashMapExtension<K, V> extends HashMap<K, V> {
        private static final long serialVersionUID = 932116907388087006L;

        private boolean isDirty=false;
        
        @Override
        public V put(K key, V value) {
            this.isDirty = true;
            return super.put(key, value);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            this.isDirty = true;
            super.putAll(m);
        }
    }

    private static final int DEFAULT_MAX_AMMOUNT = 15;
    
    private Integer maxAmmount;
    private Integer page;
    private Integer pageCount;
    private List<E> resultList;
    private HashMapExtension<String, Object> parameters;
    private HashMap<String, String> searchCriteria;
    private HashMap<String, Object> params;
    private GenericManager genericManager;
    
    @Override
    public List<E> list() {
        return list(DEFAULT_MAX_AMMOUNT);
    }
    
    protected void beforeInitList(){
    }
    
    protected abstract String getQuery();

    private String capitalizeFirstLetter(String value) {
        String string;
        if ("".equals(value)) {
            string = "";
        } else {
            final StringBuilder sb = new StringBuilder();
            sb.append(value.substring(0, 1).toUpperCase());
            sb.append(value.substring(1));
            string = sb.toString();   
        }
        return string;
    }
    
    private String resolveParameters() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        params = new HashMap<>();
        int i=0;
        StringBuilder sb = new StringBuilder(getQuery());
        for (String key : searchCriteria.keySet()) {
            final String[] split = key.split("\\.");
            
            if (parameters.containsKey(split[0])) {
                Object val = null;
                int j;
                for (j = 0; j < split.length; j++) {
                    if (j==0) {
                        val = parameters.get(split[j]);
                    } else if (val != null ){
                        val = val.getClass().getMethod(format("get{0}", capitalizeFirstLetter(split[j]))).invoke(val);
                    }
                }
                if (val != null) {
                    if (i++==0) {
                        sb.append(" where ");
                    } else {
                        sb.append(" and ");
                    }
                    sb.append(searchCriteria.get(key));
                    params.put(split[j-1], val);
                }
            }
        }
        sb.append(" ").append(getGroupBy());
        return sb.toString();
    }
    
    protected String getGroupBy() {
        return "";
    }
    
    protected abstract void initCriteria();
    
    @Override
    public List<E> list(final int maxAmmount) {
        List<E> truncList = null;
        try {
            this.maxAmmount = maxAmmount;
            if (this.resultList == null || isDirty()) {
                beforeInitList();
                final String resolveParameters = resolveParameters();
                this.resultList = genericManager.getResultList(resolveParameters, params);
                this.parameters.isDirty = false;
            }
            truncList = truncList();
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage(), e);
        }
        return truncList;
    }
    
    private List<E> truncList() {
        final int fromIndex = (page-1) * maxAmmount;
        final int toIndex = maxAmmount * page;
        final int listSize = this.resultList.size();
        return resultList.subList(fromIndex, toIndex > listSize ? listSize : toIndex);
    }

    protected boolean areEqual(final Object obj1, final Object obj2) {
        return obj1==obj2 || obj1 != null && obj1.equals(obj2);
    }
    
    @Override
    public void newInstance() {
        clearParameters();
    }

    @Override
    public void setPage(final Integer page) {
        this.page = page;
    }

    @Override
    public boolean isPreviousExists() {
        return getPage() > 1;
    }

    @Override
    public boolean isNextExists() {
        return getPage() < getPageCount();
    }

    @Override
    public Integer getPage() {
        if (page == null || page < 0) {
            page = 1;
        }
        final Integer count = getPageCount();
        if (page > count) {
            page = count;
        }
        return page;
    }

    @Override
    public Integer getPageCount() {
        if (pageCount == null || isDirty()) {
            final int size = resultList.size();
            final int estimatedPageCount = size / maxAmmount;
            if (size % maxAmmount == 0) {
                pageCount = estimatedPageCount;
            } else {
                pageCount = estimatedPageCount+1;
            }
        }
        return pageCount;
    }
    
    @Override
    public Integer getResultCount() {
        if (resultList == null) {
            list();
        }
        return resultList.size();
    }

    protected boolean isDirty() {
        return this.parameters.isDirty;
    }

    @Create
    public void init() {
        this.genericManager = (GenericManager) Component.getInstance(GenericManager.NAME);
        this.parameters = new HashMapExtension<>();
        this.searchCriteria = new HashMap<>();
        initCriteria();
    }

    protected void addParameter(String key, Object value) {
        this.parameters.put(key, value);
    }
    
    protected void clearParameters() {
        this.parameters.clear();
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }

    protected void addSearchCriteria(String field, String expression) {
        this.searchCriteria.put(field, expression);
    }
    
}