package br.com.infox.core.list;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;

@Scope(ScopeType.CONVERSATION)
public abstract class AbstractPageableList<E> implements PageableList<E>, Serializable {

    private static final int DEFAULT_MAX_AMMOUNT = 15;
    
    private Integer maxAmmount;
    private Integer page;
    private Integer pageCount;
    private List<E> resultList;
    private HashMap<String, Object> parameters;
    private GenericManager genericManager;
    private boolean isDirty;
    
    @Override
    public List<E> list() {
        return list(DEFAULT_MAX_AMMOUNT);
    }
    
    protected void beforeInitList(){
    }
    
    protected abstract String getQuery();
    
    @Override
    public List<E> list(final int maxAmmount) {
        this.maxAmmount = maxAmmount;
        if (this.resultList == null || isDirty()) {
            beforeInitList();
            this.resultList = genericManager.getResultList(getQuery(), parameters);
            this.isDirty = false;
        }
        return truncList();
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
        return this.isDirty;
    }

    @Create
    public void init() {
        this.genericManager = (GenericManager) Component.getInstance(GenericManager.NAME);
        this.parameters = new HashMap<>();
        this.isDirty = false;
    }

    protected void addParameter(String key, Object value) {
        this.parameters.put(key, value);
        this.isDirty = true;
    }
    
    protected void clearParameters() {
        this.parameters.clear();
    }
    
    public HashMap<String, Object> getParameters() {
        return parameters;
    }
}