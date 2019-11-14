package br.com.infox.core.list;

import java.util.List;

public interface PageableList<E> extends Pageable {

    List<E> list();

    List<E> list(int maxAmmount);

    void newInstance();

    void setPage(Integer page);

    boolean isPreviousExists();

    boolean isNextExists();

    Integer getPage();

    Integer getPageCount();

    Integer getResultCount();
    
    String getOrderedColumn();
    
    void setOrderedColumn(String orderedColumn);

}
