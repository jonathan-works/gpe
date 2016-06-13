package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.ibpm.process.definition.ProcessBuilder;

@Named
@ViewScoped
public class BpmnView implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private FluxoManager fluxoManager;
	
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
	
	public String getEscapedBpmn() {
		return fluxo.getBpmnXml();
	}
}