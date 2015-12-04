package br.com.infox.epp.calendario.modification.process;

import java.util.List;

import javax.ejb.Stateless;

import br.com.infox.epp.calendario.CalendarioEventosModification;

@Stateless
public class EppCalendarioEventosModificationProcessor implements CalendarioEventosModificationProcessor {

    @Override
    public void process(List<CalendarioEventosModification> modifications) {
    }

    @Override
    public void afterPersist(CalendarioEventosModification modification) {
    }

}
