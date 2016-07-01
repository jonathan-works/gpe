package br.com.infox.epp.fluxo.monitor;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class MonitorProcessoView implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private MonitorProcessoService monitorProcessoService;

    private LogProvider LOG = Logging.getLogProvider(MonitorProcessoView.class);

    private Fluxo fluxo;
    private boolean success;
    private MonitorProcessoDTO monitor;
    private List<MonitorProcessoInstanceDTO> instances;

    public void selectFluxo(Fluxo f) {
        fluxo = f;
        try {
            monitor = monitorProcessoService.createSvgMonitoramentoProcesso(fluxo);
            instances = monitorProcessoService.listInstances(monitor.getProcessDefinition());
            success = true;
        } catch (Exception e) {
            success = false;
            instances = null;
            LOG.error("Erro ao tentar carregar SVG do fluxo " + fluxo.getCodFluxo(), e);
        }
    }

    public String getSvg() {
        return monitor.getSvg();
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

    public List<MonitorProcessoInstanceDTO> getInstances() {
        return instances;
    }
}
