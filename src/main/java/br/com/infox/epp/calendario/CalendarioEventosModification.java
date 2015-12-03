package br.com.infox.epp.calendario;

import static br.com.infox.epp.calendario.CalendarioEventosModification.Type.CREATE;
import static br.com.infox.epp.calendario.CalendarioEventosModification.Type.DELETE;
import static br.com.infox.epp.calendario.CalendarioEventosModification.Type.UPDATE;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import br.com.infox.core.exception.EppSystemException;
import br.com.infox.core.exception.StandardErrorCode;
import br.com.infox.epp.cliente.entity.CalendarioEventos;

public class CalendarioEventosModification {

    private CalendarioEventos before;
    private CalendarioEventos after;
    private List<Issue> problems;

    public CalendarioEventosModification(CalendarioEventos before, CalendarioEventos after) {
        setBefore(before);
        setAfter(after);
        problems = new ArrayList<>();
    }

    private CalendarioEventos copyEvent(CalendarioEventos calendarioEventos) {
        if (calendarioEventos == null) {
            return null;
        }
        try {
            CalendarioEventos copy = (CalendarioEventos) BeanUtils.cloneBean(calendarioEventos);
            copy.setSerie(calendarioEventos.getSerie());
            return copy;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new EppSystemException(StandardErrorCode.CLONE).set("entity", calendarioEventos);
        }
    }

    public CalendarioEventos getBefore() {
        return before;
    }

    public void setBefore(CalendarioEventos before) {
        this.before = copyEvent(before);
    }

    public CalendarioEventos getAfter() {
        return after;
    }

    public void setAfter(CalendarioEventos after) {
        this.after = copyEvent(after);
    }

    public List<Issue> getIssues() {
        return problems;
    }

    public Type getType() {
        boolean beforeIsNull = getBefore() == null;
        boolean afterIsNull = getAfter() == null;
        if (beforeIsNull && afterIsNull){
            throw new IllegalStateException("Can't modify from null to null");
        }
        if (beforeIsNull && !afterIsNull) {
            return CREATE;
        }
        if (!beforeIsNull && afterIsNull) {
            return DELETE;
        }
        return UPDATE;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (getType()) {
        case CREATE:
            sb.append("Criar ").append(getAfter());
            break;
        case DELETE:
            sb.append("Remover ").append(getBefore());
            break;
        case UPDATE:
            sb.append("Atualizar de: ").append(getBefore()).append(" para ").append(getAfter());
            break;
        default:
            sb.append("Estado desconhecido");
            break;
        }
        return sb.toString();
    }

    public static enum Type {
        CREATE, UPDATE, DELETE
    }

    public static boolean hasIssues(List<CalendarioEventosModification> modifications) {
        for (CalendarioEventosModification modification : modifications) {
            if (modification.problems != null && modification.problems.size() > 0) {
                return true;
            }
        }
        return false;
    }

    public CalendarioEventosModification addIssues(Collection<? extends Issue> issues){
        getIssues().addAll(issues);
        return this;
    }
    
    public CalendarioEventosModification addIssue(Issue issue) {
        getIssues().add(issue);
        return this;
    }

}
