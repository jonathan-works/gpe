package br.com.infox.epp.fluxo.merger.view;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.merger.model.MergePointsBundle;
import br.com.infox.epp.fluxo.merger.service.FluxoMergeService;

@Named
@ViewScoped
public class FluxoMergeView implements Serializable {

    private static final long serialVersionUID = 1L;

    private Fluxo base;
    private Fluxo reference;
    private MergePointsBundle mergePointsBundle;
    private List<Fluxo> availableReferences;

    @Inject
    private FluxoManager fluxoManager;
    @Inject
    private FluxoMergeService fluxoMergeService;

    @PostConstruct
    public void init() {
        availableReferences = fluxoManager.findAll();
        setBase(fluxoManager.getFluxoByCodigo("Inst_v36"));
    }

    public Fluxo getBase() {
        return base;
    }

    public void setBase(Fluxo base) {
        this.base = base;
    }

    public Fluxo getReference() {
        return reference;
    }

    public void setReference(Fluxo reference) {
        this.reference = reference;
    }

    public List<Fluxo> getAvailableReferences() {
        return availableReferences;
    }

    public MergePointsBundle getMergePointsBundle() {
        return mergePointsBundle;
    }

    public void merge() {
        try {
            Fluxo base = getBase();
            base.setXml(getReference().getXml());
            mergePointsBundle = fluxoMergeService.publish(base, getMergePointsBundle());
        } catch (RuntimeException e) {
            FacesMessages.instance().add(e.getMessage());
        }
    }

    public void deploy(Fluxo fluxo) {
        try {
            mergePointsBundle = fluxoMergeService.publish(fluxo, getMergePointsBundle());
        } catch (RuntimeException e) {
            FacesMessages.instance().add(handleExceptionMessage(e));
        }
    }

    private String handleExceptionMessage(Exception exception) {
        if (exception.getCause() == null) {
            return exception.getMessage();
        }
        return handleExceptionMessage((Exception) exception.getCause());
    }

}
