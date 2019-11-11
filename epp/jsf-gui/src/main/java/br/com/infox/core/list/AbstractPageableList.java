package br.com.infox.core.list;

import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.jboss.seam.Component;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.ApplicationException;

/**
 * 
 * @author Erik Liberal
 * 
 */
public abstract class AbstractPageableList<E> implements PageableList<E>, Serializable {

	private static final long serialVersionUID = 1L;

	private static final LogProvider LOG = Logging.getLogProvider(AbstractPageableList.class);
    
    @EJB
    private InfoxMessages infoxMessages;
    
    private final class HashMapExtension<K, V> extends HashMap<K, V> {
        private static final long serialVersionUID = 932116907388087006L;

        private boolean isDirty = false;

        @Override
        public V put(K key, V value) {
            final V storedValue = super.get(key);
            if (storedValue == null || !storedValue.equals(value)) {
                this.isDirty = true;
                return super.put(key, value);
            } else {
                return storedValue;
            }
        }

        @Override
        public void clear() {
            this.isDirty = true;
            super.clear();
        }
        
        @Override
        public V remove(Object key) {
            this.isDirty = true;
            return super.remove(key);
        }
    }

    private static final int DEFAULT_MAX_AMMOUNT = 15;

    private Integer maxAmmount;
    private Integer page = 1;
    private Integer pageCount;
    private List<E> resultList;
    private HashMapExtension<String, Object> parameters;
    private HashMap<String, String> searchCriteria;
    private HashMap<String, Object> params;
    private GenericManager genericManager;
    private String orderedColumn;

    @Override
    public List<E> list() {
        return list(DEFAULT_MAX_AMMOUNT);
    }

    protected void beforeInitList() {
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

    /**
     * @throws SecurityException
     * @throws IllegalArgumentException
     * */
    private String resolveParameters() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        params = new HashMap<>();
        int i = 0;
        StringBuilder sb = new StringBuilder(getQuery());
        for (String key : searchCriteria.keySet()) {
            final String[] split = key.split("\\.");

            if (parameters.containsKey(split[0])) {
                Object val = null;
                int j;
                for (j = 0; j < split.length; j++) {
                    if (j == 0) {
                        val = parameters.get(split[j]);
                    } else if (val != null) {
                        val = val.getClass().getMethod(format("get{0}", capitalizeFirstLetter(split[j]))).invoke(val);
                    }
                }
                if (val != null) {
                    if (i++ == 0) {
                        sb.append(" where ");
                    } else {
                        sb.append(" and ");
                    }
                    sb.append(searchCriteria.get(key));
                    params.put(split[j - 1], val);
                }
            }
        }
        sb.append(" ").append(getGroupBy());
        sb.append(" ").append(getOrderBy());
        return sb.toString();
    }

    protected String getGroupBy() {
        return "";
    }
    
    protected String getOrderBy() {
        if (orderedColumn == null || orderedColumn.isEmpty()) {
            return "";
        }
        return "order by " + orderedColumn;
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
                this.pageCount = null;
            }
            truncList = truncList();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            LOG.error("AbstractPageableList.list(int)", e);
            FacesMessages.instance().add(Severity.ERROR, infoxMessages.get("list.resolveFilter.error"));
        } catch (Exception e) {
            throw new ApplicationException("list.error", e);
        }
        return truncList;
    }

    private List<E> truncList() {
        final int fromIndex = (page - 1) * maxAmmount;
        final int toIndex = maxAmmount * page;
        final int listSize = this.resultList.size();
        return resultList.subList(fromIndex, toIndex > listSize ? listSize : toIndex);
    }

    protected boolean areEqual(final Object obj1, final Object obj2) {
        return obj1 == obj2 || obj1 != null && obj1.equals(obj2);
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
            if (count == 0) {
                page = 1;
            } else {
                page = count;
            }
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
                pageCount = estimatedPageCount + 1;
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

    @PostConstruct
    public void init() {
        this.genericManager = (GenericManager) Component.getInstance(GenericManager.NAME);
        this.parameters = new HashMapExtension<>();
        this.searchCriteria = new HashMap<>();
        initCriteria();
    }

    protected void addParameter(String key, Object value) {
        this.parameters.put(key, value);
    }
    
    protected void removeParameter(String key) {
        this.parameters.remove(key);
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
    
    protected void removeSearchCriteria(String field) {
        this.searchCriteria.remove(field);
    }
    
    @Override
    public String getOrderedColumn() {
        return orderedColumn;
    }
    
    @Override
    public void setOrderedColumn(String orderedColumn) {
        this.orderedColumn = orderedColumn;
    }
}
