package br.com.infox.epp.cliente.crud;

import static java.text.MessageFormat.format;

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
import org.jboss.seam.international.StatusMessages;

import br.com.infox.componentes.tabs.TabPanel;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.calendario.CalendarioEventosModification;
import br.com.infox.epp.calendario.CalendarioEventosService;
import br.com.infox.epp.calendario.TipoEvento;
import br.com.infox.epp.calendario.TipoSerie;
import br.com.infox.epp.calendario.entity.SerieEventos;
import br.com.infox.epp.calendario.modification.process.CalendarioEventosModificationProcessor;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.cliente.entity.CalendarioEventos;
import br.com.infox.epp.cliente.list.CalendarioEventosList;
import br.com.infox.epp.cliente.manager.CalendarioEventosSearch;

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
    @Inject
    private CalendarioEventosSearch calendarioEventosSearch;
    @Inject
    private LocalizacaoManager localizacaoManager;

    private Integer id;
    private String descricao;
    private Date dataInicio;
    private Date dataFim;
    private SerieEventos serie;
    private TipoEvento tipoEvento;

    private List<CalendarioEventosModification> calendarioEventosModifications;

    public boolean isPersisted() {
        return getId() != null;
    }

    public void clickSearchTab() {
        newInstance();
        setCalendarioEventosModifications(null);
        calendarioEventosList.refresh();
    }

    public void newInstance() {
        setId(null);
        setDescricao(null);
        setDataInicio(null);
        setDataFim(null);
        setTipoEvento(null);
        this.serie = null;
    }

    public void clickFormTab() {
        if (getId() == null) {
            newInstance();
        }
    }

    public void cancelar(){
        setCalendarioEventosModifications(null);
    }

    @ExceptionHandled
    public void aplicarModificacoes() {
        calendarioEventosService.persistir(getCalendarioEventosModifications());
        if (getCalendarioEventosModifications() != null && !getCalendarioEventosModifications().isEmpty()) {
            setId(getCalendarioEventosModifications().get(0).getEvento().getIdCalendarioEvento());
        }
        StatusMessages.instance().add(Severity.INFO, "#{infoxMessages['entity_created']}");
        setCalendarioEventosModifications(null);
        calendarioEventosList.refresh();
    }

    public void persist() {
        if (getDataFim() == null) {
            setDataFim(getDataInicio());
        }
        if (TipoEvento.S.equals(getTipoEvento())
                && calendarioEventosSearch.existeColisaoDeSuspensaoDePrazo(getDataInicio(), getDataFim())) {
            FacesMessages.instance().add(Severity.ERROR,
                    "Não é possível cadastrar uma suspensão de prazo num dia ou período onde já existe uma suspensão cadastrada.");
        } else {
            CalendarioEventos eventoCriar = new CalendarioEventos();
            eventoCriar.setLocalizacao(localizacaoManager.getLocalizacaoByCodigo("LOC1"));
            eventoCriar.setDescricaoEvento(getDescricao());
            eventoCriar.setDataInicio(getDataInicio());
            eventoCriar.setDataFim(getDataFim());
            eventoCriar.setTipoEvento(getTipoEvento());
            eventoCriar.setSerie(serie);
            setCalendarioEventosModifications(calendarioEventosService.criar(eventoCriar));
            modificationProcessor.process(getCalendarioEventosModifications());
        }
    }

    @ExceptionHandled(MethodType.REMOVE)
    public void remover(CalendarioEventos calendarioEventos) {
        calendarioEventosService.remover(calendarioEventos, false);
        calendarioEventosList.refresh();
        newInstance();
    }

    @ExceptionHandled(MethodType.REMOVE)
    public void removerEvento() {
        CalendarioEventos calendarioEventos = calendarioEventosSearch.find(getId());
        remover(calendarioEventos);
    }

    @ExceptionHandled(MethodType.REMOVE)
    public void removerSerie() {
        CalendarioEventos calendarioEventos = calendarioEventosSearch.find(getId());
        calendarioEventosService.remover(calendarioEventos, true);
        calendarioEventosList.refresh();
        newInstance();
    }

    @ExceptionHandled(MethodType.UPDATE)
    public void update() {
        CalendarioEventos eventoAlterar = calendarioEventosSearch.find(getId());
        eventoAlterar.setDescricaoEvento(getDescricao());
        calendarioEventosService.atualizar(eventoAlterar);
        calendarioEventosList.refresh();
    }

    public void edit(CalendarioEventos calendarioEventos) {
        setId(calendarioEventos.getIdCalendarioEvento());
        setDescricao(calendarioEventos.getDescricaoEvento());
        setDataInicio(calendarioEventos.getDataInicio());
        setDataFim(calendarioEventos.getDataFim());
        setTipoEvento(calendarioEventos.getTipoEvento());
        this.serie = calendarioEventos.getSerie();
        ((TabPanel) FacesContext.getCurrentInstance().getViewRoot().findComponent("defaultTabPanel"))
                .setActiveTab("form");
    }

    public void setCalendarioToRemove(CalendarioEventos calendarioEventos) {
        setId(calendarioEventos.getIdCalendarioEvento());
    }

    public boolean isParteSerie() {
        return this.serie != null;
    }

    public TipoEvento[] getTiposEvento() {
        return TipoEvento.values();
    }

    public boolean getAnual() {
        return this.serie != null && TipoSerie.A.equals(this.serie.getTipo());
    }

    public void setAnual(boolean anual) {
        if (anual) {
            this.serie = new SerieEventos();
            this.serie.setTipo(TipoSerie.A);
        } else if (!anual) {
            this.serie = null;
        }
    }

    public void validarDatas(final ComponentSystemEvent event) {
        final UIComponent panel = event.getComponent();
		final String datePattern = "{0}Decoration:{0}";
		final ValueHolder dataInicioComponent = (ValueHolder) panel.findComponent(format(datePattern, "dataInicio"));
		final ValueHolder dataFimComponent = (ValueHolder) panel.findComponent(format(datePattern, "dataFim"));
        Date dataInicio = (Date) dataInicioComponent.getLocalValue();
        if (dataInicio != null) {
            Date dataFim = (Date) dataFimComponent.getLocalValue();
            dataFim = dataFim == null ? new Date(dataInicio.getTime()) : dataFim;
            if (dataInicio.after(dataFim)) {
                FacesMessages.instance().add(Severity.ERROR, "A data final deve ser igual ou superior à data inicial.");
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

}
