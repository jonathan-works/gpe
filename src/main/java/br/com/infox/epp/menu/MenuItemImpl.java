package br.com.infox.epp.menu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import br.com.infox.epp.menu.api.IconAlignment;
import br.com.infox.epp.menu.api.MenuItem;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="MenuItem")
public class MenuItemImpl implements MenuItem {
    @XmlAttribute
    private String icon;
    @XmlAttribute
    private IconAlignment iconAlignment;
    @XmlAttribute
    private Boolean hideLabel;
    @XmlAttribute
    private String label;
    @XmlAttribute
    private String url;
    @XmlAttribute(name="permission")
    private String permission;
    
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public IconAlignment getIconAlignment() {
        return iconAlignment;
    }
    public void setIconAlignment(IconAlignment iconAlignment) {
        this.iconAlignment = iconAlignment;
    }
    public boolean isHideLabel() {
        return Boolean.TRUE.equals(hideLabel);
    }
    public void setHideLabel(boolean hideLabel) {
        this.hideLabel = hideLabel;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof MenuItemImpl))
            return false;
        MenuItemImpl other = (MenuItemImpl) obj;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }
    @Override
    public String getPermission() {
        return permission;
    }
    @Override
    public void setPermission(String permission) {
        this.permission=permission;
    }
    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<1+depth;i++){
            sb.append("   ");
        }
        sb.append(getLabel()).append(" : ").append(getUrl());
        return sb.toString();
    }
    @Override
    public String toString() {
        return toString(0);
    }
    
}