package br.com.infox.epp.relacionamentoprocessos;

import java.io.Serializable;

import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoExterno;

@Named
@ViewScoped
public class RelacionamentoProcessoExternoList extends RelacionamentoProcessoGenericList<RelacionamentoProcessoExterno> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	protected Class<RelacionamentoProcessoExterno> getClasseRelacionamento() {
		return RelacionamentoProcessoExterno.class;
	}	
	
}
