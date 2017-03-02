package br.com.infox.epp.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import br.com.infox.epp.menu.api.Menu;
import br.com.infox.epp.menu.api.MenuElement;
import br.com.infox.epp.menu.api.Submenu;

@XmlRootElement(name="menu")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(value={SubmenuImpl.class, MenuItemImpl.class})
@XmlType(name="Menu")
public class MenuImpl implements Menu {
    @XmlElement(name="item")
    private List<MenuElement> items;

    public MenuImpl() {
        this.items = new ArrayList<>();
    }
    
    @Override
    public List<? extends MenuElement> getItems() {
        return items;
    }

    @Override
    public MenuElement add(MenuElement item) {
        MenuElement auxiliarItem = item;
        int i = items.indexOf(auxiliarItem);
        if (i != -1) {
            auxiliarItem = items.get(i);
            if (auxiliarItem instanceof Submenu && item instanceof Submenu)
                ((Submenu) auxiliarItem).addAll(((Submenu)item).getItems());
        } else {
            items.add(auxiliarItem);
        }
        return auxiliarItem;
    }
    @Override
    public void addAll(Collection<? extends MenuElement> items) {
        for (Iterator<? extends MenuElement> iterator = items.iterator(); iterator.hasNext();) {
            add(iterator.next());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Menu { ").append(System.lineSeparator());
        for (Iterator<? extends MenuElement> iterator = items.iterator(); iterator.hasNext();) {
            MenuElement menuElement = iterator.next();
            sb.append(menuElement.toString());
        }
        sb.append("}").append(System.lineSeparator());
        return sb.toString();
    }
    
}