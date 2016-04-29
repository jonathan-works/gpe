package br.com.infox.epp.access.component.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.core.Interpolator;

public class MenuItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int PRIME = 31;

    private String label;

    private String url;

    private List<MenuItem> items = new ArrayList<MenuItem>();

    public MenuItem(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public MenuItem(String label) {
        this.label = label;
    }

    public String getLabel() {
        return Interpolator.instance().interpolate(label);
    }

    public String getLabelExpression() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String url() {
        return url;
    }

    @Override
    public String toString() {
        return label + ":" + getUrl() + " " + items;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public MenuItem add(MenuItem item) {
        MenuItem auxiliarItem = item;
        int i = items.indexOf(auxiliarItem);
        if (i != -1) {
            auxiliarItem = items.get(i);
        } else {
            items.add(auxiliarItem);
        }
        return auxiliarItem;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = PRIME * result + ((label == null) ? 0 : label.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MenuItem other = (MenuItem) obj;
        if (label == null) {
            if (other.label != null) {
                return false;
            }
        } else if (!label.equals(other.label)) {
            return false;
        }
        return true;
    }

}
