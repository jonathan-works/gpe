package br.com.infox.epp.processo.metadado.manager;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

@Stateless
public class EppMetadadoProcessoService {
	
	@Inject
	private MetadadoProcessoManager metadadoProcessoManager;
	
    private void setMetadado(MetadadoProcessoDefinition definition, Processo processo, String valor) {
        MetadadoProcesso metadadoExistente = processo.getMetadado(definition);
        if (metadadoExistente != null) {
            metadadoExistente.setValor(valor);
            metadadoProcessoManager.update(metadadoExistente);
        } else {
            metadadoProcessoManager.addMetadadoProcesso(processo, definition, valor);;
        }
    }
    
	public void setRelator(Processo processo, PessoaFisica relator) {
        setMetadado(EppMetadadoProvider.RELATOR, processo, relator.getIdPessoa().toString());        
	}

	public void setUnidadeDecisoraMonocratica(Processo processo, UnidadeDecisoraMonocratica udm) {
        setMetadado(EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA, processo, udm.getIdUnidadeDecisoraMonocratica().toString());		
	}

	public void setUnidadeDecisoraColegiada(Processo processo, UnidadeDecisoraColegiada udc){
	    setMetadado(EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA, processo, udc.getIdUnidadeDecisoraColegiada().toString());
	}

}
