package br.com.infox.ibpm.variable;

import java.io.Serializable;

public class TaskpageParameterVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String type;
    private String tooltip;

    public TaskpageParameterVO(String name, String type, String tooltip) {
        super();
        this.name = name;
        this.type = type;
        this.tooltip = tooltip;
    }

    public TaskpageParameterVO(TaskpageParameter parameter) {
        this(parameter.name(), parameter.type(), parameter.tooltip());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
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
        TaskpageParameterVO other = (TaskpageParameterVO) obj;
        if (getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!getName().equals(other.getName()))
            return false;
        return true;
    }
}