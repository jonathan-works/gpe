package br.com.infox.epp.entrega.modelo.rest;

import javax.inject.Inject;

import br.com.infox.epp.entrega.ModeloEntregaService;

public class AgendaEntregaTimerResourceImpl implements AgendaEntregaTimerResource {

    @Inject 
    private ModeloEntregaService modeloEntregaService;

    @Override
    public void processAgendaEntrega() {
        modeloEntregaService.sinalizarAgendasVencidas();
    }

}
