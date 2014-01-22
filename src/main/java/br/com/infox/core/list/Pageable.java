package br.com.infox.core.list;

public interface Pageable {

    Integer getPage();

    void setPage(Integer page);

    Integer getPageCount();

    boolean isPreviousExists();

    boolean isNextExists();

}
