package br.com.infox.epp.fluxo.monitor;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.controller.Controller;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Named
@ViewScoped
public class MonitorProcessoView implements Controller {
    private static final long serialVersionUID = 1L;

    @Inject
    private MonitorProcessoSearch monitorProcessoSearch;
    @Inject
    private MonitorProcessoService monitorProcessoService;

    // view controll
    private String tab;
    private Fluxo fluxo;
    private List<Fluxo> fluxoList;

    // svg controll
    private String svg;

    @PostConstruct
    private void init() {
        fluxoList = monitorProcessoSearch.getFluxoList();
        // TODO remover a linha abaixo
        selectFluxo(EntityManagerProducer.getEntityManager().find(Fluxo.class, 2));
    }

    public void selectFluxo(Fluxo f) {
        fluxo = f;
        try {
            svg = monitorProcessoService.createSvgMonitoramentoProcesso(fluxo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Fluxo> getFluxoList() {
        return fluxoList;
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
        // TODO falta implementar
        svg = null;
        fluxo = null;
    }

    @Override
    public void onClickFormTab() {
        // TODO acredito que será inútil
    }

    @Override
    public Object getId() {
        // TODO provavelmente inútil
        return null;
    }
    
    @Override
    public void setId(Object id) {
        // TODO provavelmente inútil
    }
}
