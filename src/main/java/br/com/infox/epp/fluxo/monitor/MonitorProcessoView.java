package br.com.infox.epp.fluxo.monitor;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.controller.Controller;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class MonitorProcessoView implements Controller {
    private static final long serialVersionUID = 1L;

    private static final String MONITOR_TAB = "monitorTab";

    @Inject
    private MonitorProcessoService monitorProcessoService;

    private LogProvider LOG = Logging.getLogProvider(MonitorProcessoView.class);

    private Fluxo fluxo;
    private String svg;
    private String tab;

    public void selectFluxo(Fluxo f) {
        fluxo = f;
        try {
            svg = monitorProcessoService.createSvgMonitoramentoProcesso(fluxo);
            tab = MONITOR_TAB;
        } catch (Exception e) {
            FacesMessages.instance().add("Não foi possível carregar o arquivo svg.");
            LOG.error("Erro ao tentar carregar SVG do fluxo " + fluxo.getCodFluxo(), e);
        }
    }

    public Fluxo getFluxo() {
        return fluxo;
    }

    public String getSvg() {
        return svg;
    }

    @Override
    public String getTab() {
        return tab;
    }

    @Override
    public void setTab(String tab) {
        this.tab = tab;
    }

    @Override
    public void onClickSearchTab() {
        svg = null;
        fluxo = null;
    }

    @Override
    public void onClickFormTab() {}

    @Override
    public Object getId() {return null;}
    
    @Override
    public void setId(Object id) {}
}
