package br.com.infox.epp.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import br.com.infox.epp.menu.api.IconAlignment;
import br.com.infox.epp.menu.api.MenuElement;
import br.com.infox.epp.menu.api.Submenu;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(value={MenuItemImpl.class})
@XmlType(name="Submenu")
public class SubmenuImpl implements Submenu{
    @XmlElement(name="item")
    private List<MenuElement> items;
    @XmlAttribute
    private String icon;
    @XmlAttribute
    private IconAlignment iconAlignment;
    @XmlAttribute
    private Boolean hideLabel;
    @XmlAttribute
    private String label;
    @XmlAttribute
    private String permission;

    public SubmenuImpl() {
        items = new ArrayList<>();
    }
    
    @Override
    public String getIcon() {
        return this.icon;
    }

    @Override
    public void setIcon(String icon) {
        this.icon=icon;
    }

    @Override
    public IconAlignment getIconAlignment() {
        return iconAlignment;
    }

    @Override
    public void setIconAlignment(IconAlignment iconAlignment) {
        this.iconAlignment=iconAlignment;
    }

    @Override
    public boolean isHideLabel() {
        return Boolean.TRUE.equals(hideLabel);
    }

    @Override
    public void setHideLabel(boolean hideLabel) {
        this.hideLabel=hideLabel;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label=label;
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

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLabel() == null) ? 0 : getLabel().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MenuElement other = (MenuElement) obj;
        if (getLabel() == null) {
            if (other.getLabel() != null)
                return false;
        } else if (!getLabel().equals(other.getLabel()))
            return false;
        return true;
    }
    
    private String toString(int depth){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<1+depth;i++){
            sb.append("   ");
        }
        sb.append(getLabel()).append(" { ").append(System.lineSeparator());
        for (Iterator<? extends MenuElement> iterator = items.iterator(); iterator.hasNext();) {
            MenuElement menuElement = iterator.next();
            if (menuElement instanceof SubmenuImpl){
                sb.append(((SubmenuImpl)menuElement).toString(1+depth));
            } else if (menuElement instanceof MenuItemImpl){
                sb.append(((MenuItemImpl)menuElement).toString(1+depth));
            } else {
                sb.append(menuElement.toString());
            }
            sb.append(System.lineSeparator());
        }
        for(int i=0;i<1+depth;i++){
            sb.append("   ");
        }
        sb.append("}").append(System.lineSeparator());
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return toString(0);
    }
}