package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.modeler.converter.BpmnJpdlService;
import br.com.infox.ibpm.process.definition.ProcessBuilder;

@Named
@ViewScoped
public class BpmnView implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private FluxoManager fluxoManager;
	@Inject
	private BpmnJpdlService bpmnJpdlService;
	
	private Fluxo fluxo;
	
	private String bpmnInformation;
	private String elementKey;

	public Fluxo getFluxo() {
		return fluxo;
	}
	
	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
	
	public void refresh() {
		if (fluxo != null) {
			fluxo = fluxoManager.find(fluxo.getIdFluxo());
			fluxoManager.refresh(fluxo);
			ProcessBuilder.instance().load(fluxo);
		}
	}
	
	@ExceptionHandled(successMessage = "Fluxo salvo com sucesso!")
	public void update() {
		JsonObject bpmnInfo = new Gson().fromJson(bpmnInformation, JsonObject.class);
		fluxo.setBpmn(bpmnInfo.get("bpmn").getAsString());
		fluxo.setSvg(bpmnInfo.get("svg").getAsString());
		fluxo = bpmnJpdlService.atualizarDefinicao(fluxo);
		refresh();
		bpmnInformation = null;
	}
	
	public void configureElement() {
		ProcessBuilder.instance().setTab("nodesTab");
		ProcessBuilder.instance().getNodeFitter().setCurrentNodeByKey(elementKey);
		elementKey = null;
	}
	
	public String getBpmnInformation() {
		return bpmnInformation;
	}
	
	public void setBpmnInformation(String bpmnInformation) {
		this.bpmnInformation = bpmnInformation;
	}
	
	public String getElementKey() {
		return elementKey;
	}
	
	public void setElementKey(String elementKey) {
		this.elementKey = elementKey;
	}
}