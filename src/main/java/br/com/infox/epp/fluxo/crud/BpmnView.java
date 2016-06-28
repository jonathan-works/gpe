package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.modeler.converter.BpmnJpdlService;
import br.com.infox.ibpm.process.definition.ProcessBuilder;

@Named
@ViewScoped
public class BpmnView implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private BpmnJpdlService bpmnJpdlService;
	
	private String bpmnInformation;
	private String elementKey;

	public Fluxo getFluxo() {
		return ProcessBuilder.instance().getFluxo();
	}
	
	@ExceptionHandled(successMessage = "Fluxo salvo com sucesso!")
	public void update() {
		ProcessBuilder.instance().update();
		FacesMessages.instance().clearGlobalMessages();
		
		JsonObject bpmnInfo = new Gson().fromJson(bpmnInformation, JsonObject.class);
		Fluxo fluxo = getFluxo();
		fluxo.setBpmn(bpmnInfo.get("bpmn").getAsString());
		fluxo.setSvg(bpmnInfo.get("svg").getAsString());
		fluxo = bpmnJpdlService.atualizarDefinicao(fluxo);
		ProcessBuilder.instance().load(fluxo);
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