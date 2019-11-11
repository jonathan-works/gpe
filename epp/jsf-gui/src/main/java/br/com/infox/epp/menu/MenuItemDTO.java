package br.com.infox.epp.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import br.com.infox.epp.menu.api.IconAlignment;
import br.com.infox.epp.menu.api.Menu;
import br.com.infox.epp.menu.api.MenuElement;
import br.com.infox.epp.menu.api.MenuItem;
import br.com.infox.epp.menu.api.Submenu;

public class MenuItemDTO {
    private String label;
    private boolean hideLabel;
    private String url;
    private String icon;
    private IconAlignment iconAlign;
    private Boolean showFilter;
    private List<MenuItemDTO> items;
    private String permission;

    public static List<MenuItemDTO> convert(Menu menu) {
        List<MenuItemDTO> items = new ArrayList<>();
        for (Iterator<? extends MenuElement> iterator = menu.getItems().iterator(); iterator.hasNext();) {
            MenuElement menuElement = iterator.next();
            if (menuElement instanceof MenuItem){
                items.add(new MenuItemDTO((MenuItem)menuElement));
            } else if (menuElement instanceof Submenu){
                items.add(new MenuItemDTO((Submenu)menuElement));
            }
        }
        return items;
    }
    
    public MenuItemDTO(MenuItem item){
        this(item.getLabel(), item.isHideLabel(), item.getUrl(), item.getIcon(), item.getIconAlignment(), false, item.getPermission());
    }
    
    public MenuItemDTO(Submenu item){
        this(item.getLabel(), item.isHideLabel(), null, item.getIcon(), item.getIconAlignment(), false, item.getPermission());
        for (Iterator<? extends MenuElement> iterator = item.getItems().iterator(); iterator.hasNext();) {
            MenuElement menuElement = iterator.next();
            if (menuElement instanceof MenuItem){
                items.add(new MenuItemDTO((MenuItem)menuElement));
            } else if (menuElement instanceof Submenu){
                items.add(new MenuItemDTO((Submenu)menuElement));
            }
        }
    }
    
    public MenuItemDTO(String label, boolean hideLabel, String url, String icon, IconAlignment iconAlign,
            Boolean showFilter, String permission) {
        this.label = label;
        this.hideLabel = hideLabel;
        this.url = url;
        this.icon = icon;
        this.iconAlign = iconAlign;
        this.showFilter = showFilter;
        this.permission = permission;
        this.items = new ArrayList<>();
    }

    public MenuItemDTO(String label, String url) {
        this(label, false, url, null, null, null, null);
    }

    public MenuItemDTO(String label) {
        this(label, false, null, null, null, null, null);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isHideLabel() {
        return hideLabel;
    }

    public void setHideLabel(boolean hideLabel) {
        this.hideLabel = hideLabel;
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public IconAlignment getIconAlign() {
        return iconAlign;
    }

    public void setIconAlign(IconAlignment iconAlign) {
        this.iconAlign = iconAlign;
    }

    public boolean isShowFilter() {
        return showFilter;
    }

    public void setShowFilter(boolean showFilter) {
        this.showFilter = showFilter;
    }

    public List<MenuItemDTO> getItems() {
        return items;
    }

    public void setItems(List<MenuItemDTO> items) {
        this.items = items;
    }

    public MenuItemDTO add(MenuItemDTO item) {
        MenuItemDTO auxiliarItem = item;
        setItems(ObjectUtils.defaultIfNull(getItems(), new ArrayList<MenuItemDTO>()));
        int i = items.indexOf(auxiliarItem);
        if (i != -1) {
            auxiliarItem = items.get(i);
        } else {
            items.add(auxiliarItem);
        }
        return auxiliarItem;
    }

    public void setPermission(String permission) {
        this.permission=permission;
    }
    public String getPermission() {
        return permission;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
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
        MenuItemDTO other = (MenuItemDTO) obj;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        return true;
    }
}
