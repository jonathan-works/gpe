package br.com.infox.epp.menu.api;

import java.util.Collection;
import java.util.List;

public interface Menu {

    List<? extends MenuElement> getItems();

    MenuElement add(MenuElement item);
    
    void addAll(Collection<? extends MenuElement> items);
}
