package br.com.infox.epp.relacionamentoprocessos;

import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno;

@Named
@ViewScoped
public class RelacionamentoProcessoInternoList extends RelacionamentoProcessoGenericList<RelacionamentoProcessoInterno> {

	private static final long serialVersionUID = 1L;

	@Override
	protected Class<RelacionamentoProcessoInterno> getClasseRelacionamento() {
		return RelacionamentoProcessoInterno.class;
	}
	
}
