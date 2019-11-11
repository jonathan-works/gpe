package br.com.infox.epp;

public class DynamicFieldAction {
    private final String icon;
    private final String label;
    private final String action;

    public DynamicFieldAction(String icon, String label, String action) {
        this.icon = icon;
        this.label = label;
        this.action = action;
    }

    public String getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }

    public String getAction() {
        return action;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
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
        DynamicFieldAction other = (DynamicFieldAction) obj;
        if (action == null) {
            if (other.action != null)
                return false;
        } else if (!action.equals(other.action))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        return true;
    }

}
