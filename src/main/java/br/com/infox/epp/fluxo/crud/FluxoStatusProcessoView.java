package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoManager;
import br.com.infox.epp.processo.status.manager.StatusProcessoSearch;

@Named
@ViewScoped
public class FluxoStatusProcessoView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private StatusProcessoSearch statusProcessoSearch;
	
	@Inject
	private StatusProcessoManager statusProcessoManager;

	private StatusProcesso statusProcesso;

	@ExceptionHandled(value = MethodType.UPDATE)
	public void incluir(Fluxo fluxo) {
		statusProcesso.getFluxos().add(fluxo);
		statusProcessoManager.update(statusProcesso);
		statusProcesso = null;
	}

	@ExceptionHandled(value = MethodType.REMOVE)
	public void remover(Fluxo fluxo, StatusProcesso status) {
		status.getFluxos().remove(fluxo);
		statusProcessoManager.update(status);
		statusProcesso = null;
	}

	public List<StatusProcesso> statusProcessosNaoRelacionados(Fluxo fluxo) {
		return statusProcessoSearch.getStatusProcessosAtivoNaoRelacionados(fluxo);
	}

	public List<StatusProcesso> statusProcessosRelacionados(Fluxo fluxo) {
		return statusProcessoSearch.getStatusProcessosAtivoRelacionados(fluxo);
	}

	public StatusProcesso getStatusProcesso() {
		return statusProcesso;
	}

	public void setStatusProcesso(StatusProcesso statusProcesso) {
		this.statusProcesso = statusProcesso;
	}

}
