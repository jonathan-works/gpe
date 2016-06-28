package br.com.infox.epp.fluxo.merger.view;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.merger.model.MergePointsBundle;
import br.com.infox.epp.fluxo.merger.service.FluxoMergeService;

@Named
@ViewScoped
public class FluxoMergeView implements Serializable {

    private static final long serialVersionUID = 1L;

    private MergePointsBundle mergePointsBundle;

    @Inject
    private FluxoMergeService fluxoMergeService;

    public MergePointsBundle getMergePointsBundle() {
        return mergePointsBundle;
    }

    public void deploy(Fluxo fluxo) {
        try {
            mergePointsBundle = fluxoMergeService.publish(fluxo, getMergePointsBundle());
        } catch (RuntimeException e) {
            FacesMessages.instance().add(e.getMessage());
        }
    }
    
    public void clear() {
    	mergePointsBundle = null;
    }
}
