package br.com.infox.epp.processo.service;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Named;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.type.TipoProcesso;


@Named
@Stateless
public class ProcessoService implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public boolean isTipoProcessoDocumento(Processo processo) {
		return isTipoProcesso(TipoProcesso.DOCUMENTO.toString(), processo);
	}
	
	public boolean isTipoProcesso(String tipoProcesso, Processo processo){
		TipoProcesso byName = TipoProcesso.getByName(tipoProcesso);
		TipoProcesso tpProcesso = processo.getMetadado(EppMetadadoProvider.TIPO_PROCESSO).getValue();
		return byName.equals(tpProcesso);
	}

}
