package br.com.infox.core.persistence;

import java.util.List;

public interface Recursive<E extends Recursive<E>> {
    E getParent();

    void setParent(E parent);

    String getHierarchicalPath();

    void setHierarchicalPath(String path);

    String getPathDescriptor();

    void setPathDescriptor(String pathDescriptor);

    List<E> getChildList();

    void setChildList(List<E> childList);
}
