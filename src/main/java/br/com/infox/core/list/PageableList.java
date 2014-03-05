package br.com.infox.core.list;

import java.util.List;

public interface PageableList<E> extends Pageable {

    public abstract List<E> list();

    public abstract List<E> list(int maxAmmount);

    public abstract void newInstance();

    public abstract void setPage(Integer page);

    public abstract boolean isPreviousExists();

    public abstract boolean isNextExists();

    public abstract Integer getPage();

    public abstract Integer getPageCount();

    public abstract Integer getResultCount();

}