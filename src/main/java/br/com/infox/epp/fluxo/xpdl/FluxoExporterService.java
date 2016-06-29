package br.com.infox.epp.fluxo.xpdl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
import br.com.infox.ibpm.jpdl.VariaveisProcessoXmlWriter;

@Stateless
public class FluxoExporterService {
	
	@Inject
	private DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;
	
	//FIXME gerar o xml das variaveis juntar com o outro xml do fluxo colocar no .epp
	public void exportarFluxo(Fluxo fluxo) {
		VariaveisProcessoXmlWriter variablesWriter = new VariaveisProcessoXmlWriter();
		
		String xmlVariables = variablesWriter.getVariablesAsXml(definicaoVariavelProcessoManager.listVariaveisByFluxo(fluxo));
	}

}
