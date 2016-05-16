package br.com.infox.epp.processo.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.type.TipoProcesso;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ProcessoService {

	public boolean isTipoProcessoDocumento(Processo processo) {
		return isTipoProcesso(TipoProcesso.DOCUMENTO.toString(), processo);
	}
	
	public boolean isTipoProcessoComunicacao(Processo processo) {
		return isTipoProcesso(TipoProcesso.COMUNICACAO.toString(), processo);
	}
	
	public boolean isTipoProcesso(String tipoProcesso, Processo processo){
		TipoProcesso byName = TipoProcesso.getByName(tipoProcesso);
		MetadadoProcesso metadadoTipoProcesso = processo.getMetadado(EppMetadadoProvider.TIPO_PROCESSO);
		if(metadadoTipoProcesso != null){
		    TipoProcesso tpProcesso = metadadoTipoProcesso.getValue();
		    return byName.equals(tpProcesso);
		}
		return false; 
	}
	
}
