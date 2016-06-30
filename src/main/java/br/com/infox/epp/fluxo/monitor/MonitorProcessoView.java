package br.com.infox.epp.fluxo.monitor;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

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
    private String svg;

    public void selectFluxo(Fluxo f) {
        fluxo = f;
        try {
            svg = monitorProcessoService.createSvgMonitoramentoProcesso(fluxo);
        } catch (Exception e) {
            FacesMessages.instance().add("Não foi possível carregar o arquivo svg.");
            LOG.error("Erro ao tentar carregar SVG do fluxo " + fluxo.getCodFluxo(), e);
        }
    }

    public String getSvg() {
        return svg;
    }
}
