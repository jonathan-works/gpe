package br.com.infox.epp.cliente.crud;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.joda.time.LocalDate;

import br.com.infox.componentes.tabs.TabPanel;
import br.com.infox.epp.calendario.CalendarioEventosModification;
import br.com.infox.epp.calendario.CalendarioEventosService;
import br.com.infox.epp.calendario.TipoEvento;
import br.com.infox.epp.calendario.TipoSerie;
import br.com.infox.epp.calendario.entity.SerieEventos;
import br.com.infox.epp.calendario.modification.process.CalendarioEventosModificationProcessor;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cliente.entity.CalendarioEventos;
import br.com.infox.epp.cliente.list.CalendarioEventosList;

@Named(CalendarioEventosCrudAction.NAME)
@ViewScoped
public class CalendarioEventosCrudAction implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "calendarioEventosCrudAction";

    @Inject
    private CalendarioEventosList calendarioEventosList;
    @Inject
    private CalendarioEventosService calendarioEventosService;
    @Inject
    private CalendarioEventosModificationProcessor modificationProcessor;

    private CalendarioEventos calendarioEventos;

    private List<CalendarioEventosModification> calendarioEventosModifications;

    public void clickSearchTab() {
        setCalendarioEventos(null);
        calendarioEventosList.refresh();
    }

    public void clickFormTab() {
        if (getCalendarioEventos() == null || getCalendarioEventos().getIdCalendarioEvento() == null) {
            setCalendarioEventos(new CalendarioEventos());
        }
    }

    public void cancelar(){
        setCalendarioEventosModifications(null);
    }
    
    @ExceptionHandled
    public void aplicarModificacoes() {
        calendarioEventosService.persistir(getCalendarioEventosModifications());
        setCalendarioEventosModifications(null);
        calendarioEventosList.refresh();
    }

    @ExceptionHandled
    public void aplicarModificacao(CalendarioEventosModification calendarioEventosModification) {
        calendarioEventosService.persistir(calendarioEventosModification);
        getCalendarioEventosModifications().remove(calendarioEventosModification);
        if (getCalendarioEventosModifications().isEmpty()){
            setCalendarioEventosModifications(null);
        }
        calendarioEventosList.refresh();
    }

    private void processarModificacoes() {
        modificationProcessor.process(getCalendarioEventosModifications());
    }

    public void persist() {
        setCalendarioEventosModifications(calendarioEventosService.criar(getCalendarioEventos()));
        processarModificacoes();
    }

    public void remover(CalendarioEventos calendarioEventos) {
        setCalendarioEventosModifications(calendarioEventosService.remover(calendarioEventos));
        processarModificacoes();
    }

    public void update() {
        setCalendarioEventosModifications(calendarioEventosService.atualizar(getCalendarioEventos()));
        processarModificacoes();
    }

    public void edit(CalendarioEventos calendarioEventos) {
        this.calendarioEventos = calendarioEventos;
        ((TabPanel) FacesContext.getCurrentInstance().getViewRoot().findComponent("defaultTabPanel"))
                .setActiveTab("form");
    }

    public CalendarioEventos getCalendarioEventos() {
        return calendarioEventos;
    }

    public void setCalendarioEventos(CalendarioEventos calendarioEventos) {
        this.calendarioEventos = calendarioEventos;
    }

    public TipoEvento[] getTiposEvento() {
        return TipoEvento.values();
    }

    public boolean getAnual() {
        return getCalendarioEventos() != null && getCalendarioEventos().getSerie() != null
                && TipoSerie.A.equals(getCalendarioEventos().getSerie().getTipo());
    }

    public void setAnual(boolean anual) {
        if (anual && getCalendarioEventos().getSerie() == null) {
            getCalendarioEventos().setSerie(new SerieEventos());
            getCalendarioEventos().getSerie().setTipo(TipoSerie.A);
        } else if (!anual && getCalendarioEventos().getSerie() != null) {
            getCalendarioEventos().setSerie(null);
        }
    }

    public void validarDatas(final ComponentSystemEvent event) {
        final UIComponent panel = event.getComponent();
        final ValueHolder dataInicioComponent = (ValueHolder) panel.findComponent("dataInicio" + "Decoration:"+ "dataInicio");
        final ValueHolder dataFimComponent = (ValueHolder) panel.findComponent("dataFim" + "Decoration:" + "dataFim");
        Date dtInicio = (Date) dataInicioComponent.getLocalValue();
        if (dtInicio != null) {
            Date dtFim = (Date) dataFimComponent.getLocalValue();
            final LocalDate dataInicio = LocalDate.fromDateFields(dtInicio);
            final LocalDate dataFim = LocalDate.fromDateFields(dtFim == null ? dtInicio : dtFim);
            if (dataInicio.isAfter(dataFim)) {
                FacesMessages.instance().add(Severity.ERROR, "A data de fim deve ser igual ou superior à data de fim");
                FacesContext.getCurrentInstance().renderResponse();
            }
        }
    }

    public List<CalendarioEventosModification> getCalendarioEventosModifications() {
        return calendarioEventosModifications;
    }

    private void setCalendarioEventosModifications(List<CalendarioEventosModification> calendarioEventosModifications) {
        this.calendarioEventosModifications = calendarioEventosModifications;
    }

}
