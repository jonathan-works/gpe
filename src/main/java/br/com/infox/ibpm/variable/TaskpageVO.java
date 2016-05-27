package br.com.infox.ibpm.variable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TaskpageVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private List<TaskpageParameterVO> parameters;

    public TaskpageVO(String name, String description) {
        this.name = name;
        this.description = description;
        parameters = new ArrayList<>(1);
    }

    public TaskpageVO(Taskpage taskpage, List<TaskpageParameter> parameters) {
        this(taskpage.name(), taskpage.description());
        List<TaskpageParameterVO> parametersVO = new ArrayList<>(parameters.size());
        for (TaskpageParameter parameter : parameters) {
            parametersVO.add(new TaskpageParameterVO(parameter));
        }
        this.setParameters(parametersVO);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TaskpageParameterVO> getParameters() {
        return parameters;
    }

    public void setParameters(List<TaskpageParameterVO> parameters) {
        this.parameters = parameters;
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
        TaskpageVO other = (TaskpageVO) obj;
        if (getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!getName().equals(other.getName()))
            return false;
        return true;
    }
}
