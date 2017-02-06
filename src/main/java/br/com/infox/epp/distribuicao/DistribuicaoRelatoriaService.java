package br.com.infox.epp.distribuicao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.unidadedecisora.dao.UnidadeDecisoraMonocraticaDAO;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.seam.exception.BusinessException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DistribuicaoRelatoriaService extends PersistenceController {
	
	@Inject
	private MetadadoProcessoManager metadadoProcessoManager;
	@Inject
	private UnidadeDecisoraMonocraticaDAO unidadeDecisoraMonocraticaDAO;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void distribuirRelatoria(String codigoLocalizacao, Processo processo) {
    	if (codigoLocalizacao == null) {
    		throw new BusinessException("Falha ao tentar Distribuir Relatoria pelo código da UDM. Código da localização não encontrado.");
    	}
    	UnidadeDecisoraMonocratica unidadeDecisoraMonocratica = unidadeDecisoraMonocraticaDAO.findByCodigoLocalizacao(codigoLocalizacao);
    	distribuirRelatoria(unidadeDecisoraMonocratica, processo);
    }
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void distribuirRelatoria(Integer idUnidadeDecisoraMonocratica, Processo processo) {
    	if ( idUnidadeDecisoraMonocratica == null ) {
    		throw new BusinessException("Falha ao tentar Distribuir Relatoria. Não existe Unidade Decisora Monocrática configurada para este código.");
    	}
		UnidadeDecisoraMonocratica unidadeDecisoraMonocratica = unidadeDecisoraMonocraticaDAO.find(idUnidadeDecisoraMonocratica);
		distribuirRelatoria(unidadeDecisoraMonocratica, processo);
    }
	
	private void distribuirRelatoria(UnidadeDecisoraMonocratica unidadeDecisoraMonocratica, Processo processo) {
    	if ( processo == null ) {
    		throw new BusinessException("Falha ao tentar Distribuir Relatoria pelo código da UDM. Processo não encontrado.");
    	}
    	if ( unidadeDecisoraMonocratica == null ) {
    		throw new BusinessException("Falha ao tentar Distribuir Relatoria. Não existe Unidade Decisora Monocrática configurada para este código.");
    	}
    	PessoaFisica relator = unidadeDecisoraMonocratica.getChefeGabinete();
        if (relator == null) {
        	throw new BusinessException("Falha ao tentar Distribuir Relatoria. UDM não possui titular definido");
        }
        setMetadado(EppMetadadoProvider.RELATOR, processo, relator.getIdPessoa().toString());
        setMetadado(EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA, processo, unidadeDecisoraMonocratica.getIdUnidadeDecisoraMonocratica().toString());
    }
	
	private void setMetadado(MetadadoProcessoDefinition definition, Processo processo, String valor) {
        MetadadoProcesso metadadoExistente = processo.getMetadado(definition);
        if (metadadoExistente != null) {
            metadadoExistente.setValor(valor);
            metadadoProcessoManager.update(metadadoExistente);
        } else {
            metadadoProcessoManager.addMetadadoProcesso(processo, definition, valor);;
        }
    }

}
