package br.com.infox.epp.calendario.modification.process;

import java.util.List;

import br.com.infox.epp.calendario.CalendarioEventosModification;

public interface CalendarioEventosModificationProcessor {
    void process(List<CalendarioEventosModification> modifications);
    void afterPersist(CalendarioEventosModification modification);
}
