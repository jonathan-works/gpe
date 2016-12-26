package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;

import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Named
@ViewScoped
public class FluxoController implements Serializable {
	private static final long serialVersionUID = 1L;

	private Fluxo fluxo;
	
	public Fluxo getFluxo() {
		return fluxo;
	}

	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
}
