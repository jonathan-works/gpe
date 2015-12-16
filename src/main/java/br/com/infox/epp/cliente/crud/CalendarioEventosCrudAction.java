package br.com.infox.epp.cliente.crud;

import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.util.Collection;
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
import org.richfaces.component.UITree;

import br.com.infox.componentes.tabs.TabPanel;
import br.com.infox.epp.calendario.CalendarioEventosModification;
import br.com.infox.epp.calendario.CalendarioEventosService;
import br.com.infox.epp.calendario.TipoEvento;
import br.com.infox.epp.calendario.TipoSerie;
import br.com.infox.epp.calendario.entity.SerieEventos;
import br.com.infox.epp.calendario.modification.process.CalendarioEventosModificationProcessor;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cliente.entity.CalendarioEventos;
import br.com.infox.epp.cliente.list.CalendarioEventosList;
import br.com.infox.epp.cliente.manager.CalendarioEventosManager;

@Named(CalendarioEventosCrudAction.NAME)
@ViewScoped
public class CalendarioEventosCrudAction implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "calendarioEventosCrudAction";

    private static final String ID_FORM_TREE = "defaultTabPanel"+ ":"+ "formTabForm"+ ":"+ "localizacaoTreeDecoration"
			+ ":"+ "localizacaoTree";
    private static final String ID_SEARCH_TREE = "defaultTabPanel"+ ":"+ "pesquisarCalendarioEventosForm"
			+ ":"+ "pesquisarCalendarioEventosFormlocalizacaoTreeDecoration"
			+ ":"+ "pesquisarCalendarioEventosFormlocalizacaoTree";
    
    @Inject
    private CalendarioEventosList calendarioEventosList;
    @Inject
    private CalendarioEventosService calendarioEventosService;
    @Inject
    private CalendarioEventosModificationProcessor modificationProcessor;

    private CalendarioEventos calendarioEventos;

    private List<CalendarioEventosModification> calendarioEventosModifications;

    private boolean persisted=false;
    
    private void resetTree(String treeId){
    	((UITree)FacesContext.getCurrentInstance().getViewRoot().findComponent(treeId)).setSelection(null);
    }
    
    public boolean isPersisted(){
    	return persisted;
    }
    
    public void clickSearchTab() {
    	newInstance();
        calendarioEventosList.refresh();
    }

    public void newInstance(){
    	persisted = false;
    	resetTree(ID_FORM_TREE);
    	setCalendarioEventos(new CalendarioEventos());
    }
    
    public void clickFormTab() {
        if (getCalendarioEventos() == null || getCalendarioEventos().getIdCalendarioEvento() == null) {
        	newInstance();
        }
    }

    public void cancelar(){
        setCalendarioEventosModifications(null);
    }
    
    private void resolveMessage(Collection<CalendarioEventosModification> modifications){
    	for (CalendarioEventosModification modification : calendarioEventosModifications) {
			resolveMessage(modification);
			break;
		}
    }
    
    private void resolveMessage(CalendarioEventosModification modification){
    	switch (modification.getType()) {
		case CREATE:
			StatusMessages.instance().add(Severity.INFO, "#{infoxMessages['entity_created']}");
			if (modification.getAfter().getIdCalendarioEvento() != null){
				CalendarioEventosManager calendarioEventosManager = BeanManager.INSTANCE.getReference(CalendarioEventosManager.class);
				setCalendarioEventos(calendarioEventosManager.find(modification.getAfter().getIdCalendarioEvento()));
				persisted = true;
			}
			break;
		case UPDATE:
			if (modification.getAfter().getIdCalendarioEvento() != null){
				CalendarioEventosManager calendarioEventosManager = BeanManager.INSTANCE.getReference(CalendarioEventosManager.class);
				setCalendarioEventos(calendarioEventosManager.find(modification.getAfter().getIdCalendarioEvento()));
			}
			StatusMessages.instance().add(Severity.INFO, "#{infoxMessages['entity_updated']}");
			break;
		case DELETE:
			StatusMessages.instance().add(Severity.INFO, "#{infoxMessages['entity_deleted']}");
			break;
		default:
			break;
		}
    }
    
    @ExceptionHandled
    public void aplicarModificacoes() {
        calendarioEventosService.persistir(getCalendarioEventosModifications());
        resolveMessage(getCalendarioEventosModifications());
        setCalendarioEventosModifications(null);
        calendarioEventosList.refresh();
    }

    @ExceptionHandled
    public void aplicarModificacao(CalendarioEventosModification calendarioEventosModification) {
        calendarioEventosService.persistir(calendarioEventosModification);
        resolveMessage(calendarioEventosModification);
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
        setCalendarioEventos(calendarioEventos);
        persisted = true;
        ((TabPanel) FacesContext.getCurrentInstance().getViewRoot().findComponent("defaultTabPanel"))
                .setActiveTab("form");
    }

    public CalendarioEventos getCalendarioEventos() {
        return calendarioEventos;
    }

    public void setCalendarioEventos(CalendarioEventos calendarioEventos) {
        this.calendarioEventos = calendarioEventos;
        if (this.calendarioEventos.getDataFim() == null){
        	this.calendarioEventos.setDataFim(this.calendarioEventos.getDataInicio());
        }
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
		final String datePattern = "{0}Decoration:{0}";
		final ValueHolder dataInicioComponent = (ValueHolder) panel.findComponent(format(datePattern, "dataInicio"));
		final ValueHolder dataFimComponent = (ValueHolder) panel.findComponent(format(datePattern, "dataFim"));
        Date dataInicio = (Date) dataInicioComponent.getLocalValue();
        if (dataInicio != null) {
            Date dataFim = (Date) dataFimComponent.getLocalValue();
            dataFim = dataFim == null ? new Date(dataInicio.getTime()) : dataFim;
            if (dataInicio.after(dataFim)) {
                FacesMessages.instance().add(Severity.ERROR, "A data de fim deve ser igual ou superior à data de início");
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
