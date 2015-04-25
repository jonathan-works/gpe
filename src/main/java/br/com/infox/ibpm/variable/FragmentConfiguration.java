package br.com.infox.ibpm.variable;

import java.io.Serializable;

import org.jbpm.taskmgmt.exe.TaskInstance;

public abstract class FragmentConfiguration {

    private final String path;
    private final String code;
    private final String label;

    public FragmentConfiguration(String label, String path, String code) {
        this.label = label;
        this.path = path;
        this.code = code;
    }

    public String getPath() {
        return this.path;
    }

    public abstract void init(TaskInstance taskInstance, String variableName);

    public abstract Serializable init(TaskInstance taskInstance);

    public String getCode() {
        return this.code;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
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
        if (!(obj instanceof FragmentConfiguration)) {
            return false;
        }
        FragmentConfiguration other = (FragmentConfiguration) obj;
        if (code == null) {
            if (other.code != null) {
                return false;
            }
        } else if (!code.equals(other.code)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getLabel();
    }

}
