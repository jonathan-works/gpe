package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;

import javax.faces.context.FacesContext;
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
		String json = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bpmnInformation");
		JsonObject bpmnInfo = new Gson().fromJson(json, JsonObject.class);
		fluxo.setBpmn(bpmnInfo.get("bpmn").getAsString());
		fluxo.setSvg(bpmnInfo.get("svg").getAsString());
		fluxo = bpmnJpdlService.atualizarDefinicaoJpdl(fluxo);
		refresh();
	}
}