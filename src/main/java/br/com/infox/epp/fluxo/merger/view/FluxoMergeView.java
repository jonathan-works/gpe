package br.com.infox.epp.fluxo.merger.view;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.graph.def.ProcessDefinition;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.merger.model.MergePointsBundle;
import br.com.infox.epp.fluxo.merger.service.FluxoMergeService;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.seam.util.ComponentUtil;

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

    @Transactional
    public void merge() {
        Fluxo base = getBase();
        base.setXml(getReference().getXml());
        publish(base);
    }

    @Transactional
    public void deploy(Fluxo fluxo) {
        publish(fluxo);
    }

    private void publish(Fluxo fluxo) {
        ProcessBuilder processBuilder = ComponentUtil.getComponent(ProcessBuilder.NAME);
        String modifiedXml = fluxo.getXml();
        String publishedXml = fluxo.getXmlExecucao();
        if (publishedXml == null) {
            ProcessDefinition modifiedProcessDef = fluxoMergeService.jpdlToProcessDefinition(modifiedXml);
            modifiedProcessDef.setName(fluxo.getFluxo());
            processBuilder.load(fluxo);
            processBuilder.setInstance(modifiedProcessDef);
            processBuilder.deploy();
        } else if (!Objects.equals(modifiedXml, publishedXml)) {
            ProcessDefinition modifiedProcessDef = fluxoMergeService.jpdlToProcessDefinition(modifiedXml);
            ProcessDefinition publishedProcessDef = fluxoMergeService.jpdlToProcessDefinition(publishedXml);
            mergePointsBundle = fluxoMergeService.verifyMerge(publishedProcessDef, modifiedProcessDef);
            if (mergePointsBundle.isValid()) {
                modifiedProcessDef.setName(fluxo.getFluxo());
                processBuilder.load(fluxo);
                processBuilder.setInstance(modifiedProcessDef);
                processBuilder.deploy();
            }
        }
    }

}
