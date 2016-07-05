package br.com.infox.epp.fluxo.monitor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.node.AutomaticNodeService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class MonitorProcessoView implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private AutomaticNodeService automaticNodeService;
    @Inject
    private MonitorProcessoService monitorProcessoService;

    private LogProvider LOG = Logging.getLogProvider(MonitorProcessoView.class);

    private Fluxo fluxo;
    private boolean success;
    private MonitorProcessoDTO monitor;
    private MonitorProcessoDTO filterMonitor;
    private List<MonitorProcessoInstanceDTO> instances;
    private List<MonitorProcessoInstanceDTO> filterInstances;
    private boolean filter;
    private String filterKey;

    public void selectFluxo(Fluxo f) {
        fluxo = f;
        filter = false;
        try {
            monitor = monitorProcessoService.createSvgMonitoramentoProcesso(fluxo, null);
            instances = monitorProcessoService.listInstances(monitor.getProcessDefinition(), null);
            success = true;
        } catch (Exception e) {
            success = false;
            instances = null;
            LOG.error("Erro ao tentar carregar SVG do fluxo " + fluxo.getCodFluxo(), e);
        }
    }

    public void filterElement() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String elementId = params.get("elementId");
        filterKey = elementId;
        filterElement(filterKey);
    }

    private void filterElement(String elementId) {
        try {
            filterMonitor = monitorProcessoService.createSvgMonitoramentoProcesso(fluxo, elementId);
            filterInstances = monitorProcessoService.listInstances(monitor.getProcessDefinition(), elementId);
            filter = true;
        } catch (Exception e) {
            success = false;
            instances = null;
            FacesMessages.instance().add("Não foi possível filtrar o fluxo pela tarefa selecionada. Favor tentar novamente");
            LOG.error("Erro ao tentar filtrar SVG do fluxo " + fluxo.getCodFluxo(), e);
        }
    }

    public void resetFilter() {
        filter = false;
        filterMonitor = null;
        filterInstances = null;
    }

    public void executeNode(MonitorProcessoInstanceDTO row) {
        try {
            automaticNodeService.executeNode(row.getTokenId());
        } catch (Exception e) {
            FacesMessages.instance().add("Erro ao tentar executar o nó" + e.getMessage() );
        }
        if (filter) {
            filterElement(filterKey);
        } else {
            selectFluxo(fluxo);
        }
    }

    public String getSvg() {
        return !filter ? monitor.getSvg() : filterMonitor.getSvg();
    }

    public String getFluxoNome() {
        return fluxo.getFluxo();
    }

    public String getFluxoDescricao() {
        return monitor.getProcessDefinition().getDescription();
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFilter() {
        return filter;
    }

    public List<MonitorProcessoInstanceDTO> getInstances() {
        return !filter ? instances : filterInstances;
    }
}
