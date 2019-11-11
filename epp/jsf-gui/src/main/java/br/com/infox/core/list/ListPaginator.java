package br.com.infox.core.list;

import java.util.List;

public class ListPaginator<T> implements Pageable {

    private Integer page = 1;
    private Integer pageElements;
    private List<T> resultList;

    public ListPaginator(List<T> list, int pageElements) {
        this.resultList = list;
        this.pageElements = pageElements;
    }

    @Override
    public Integer getPage() {
        return page;
    }

    @Override
    public void setPage(Integer page) {
        this.page = Math.min(Math.max(1, page), getPageCount());
    }

    @Override
    public Integer getPageCount() {
        return list() != null ? (int) Math.ceil((float) list().size()
                / pageElements) : 1;
    }

    @Override
    public boolean isPreviousExists() {
        return page > 1;
    }

    @Override
    public boolean isNextExists() {
        return page < getPageCount();
    }

    public List<T> list() {
        return resultList.subList((page - 1) * pageElements, Math.min(page
                * pageElements, resultList.size()));
    }

    public int getResultCount() {
        return resultList.size();
    }

}
