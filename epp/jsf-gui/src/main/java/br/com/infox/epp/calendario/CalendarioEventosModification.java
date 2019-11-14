package br.com.infox.epp.calendario;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import br.com.infox.core.exception.EppSystemException;
import br.com.infox.core.exception.StandardErrorCode;
import br.com.infox.epp.cliente.entity.CalendarioEventos;

public class CalendarioEventosModification {

    private CalendarioEventos evento;
    private List<Issue> problems;
    // Para verificar se não surgiu nenhum problema possível novo nas suspensões de prazo
    private long countPossiveisProblemas = 0;

    public CalendarioEventosModification(CalendarioEventos evento) {
        setEvento(evento);
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

    public CalendarioEventos getEvento() {
        return evento;
    }

    public void setEvento(CalendarioEventos evento) {
        this.evento = copyEvent(evento);
    }

    public List<Issue> getIssues() {
        return problems;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Criar ").append(getEvento());
        return sb.toString();
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

    public long getCountPossiveisProblemas() {
        return countPossiveisProblemas;
    }

    public void setCountPossiveisProblemas(long countPossiveisProblemas) {
        this.countPossiveisProblemas = countPossiveisProblemas;
    }

    public void addCountPossiveisProblemas(long countPossiveisProblemas) {
        this.countPossiveisProblemas += countPossiveisProblemas;
    }

}
