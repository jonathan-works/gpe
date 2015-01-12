package br.com.infox.epp.processo.service;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

@Name(DistribuicaoProcessoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class DistribuicaoProcessoService {
	
	@In
	private MetadadoProcessoManager metadadoProcessoManager;
	@In
	private ProcessoDAO processoDAO;
	
	public static final String NAME = "distribuicaoProcessoService";
	
	//TODO remover o Telescoping Anti-Pattern
	public void distribuirProcesso(Processo processo, PessoaFisica relator, UnidadeDecisoraMonocratica unidadeDecisoraMonocratica) throws DAOException {
        distribuirProcesso(processo, relator, unidadeDecisoraMonocratica, null);
    }

    public void distribuirProcesso(Processo processo, UnidadeDecisoraMonocratica unidadeDecisoraMonocratica) throws DAOException {
        distribuirProcesso(processo, null, unidadeDecisoraMonocratica, null);
    }
    
    public void distribuirProcesso(Processo processo, UnidadeDecisoraColegiada unidadeDecisoraColegiada) throws DAOException {
        distribuirProcesso(processo, null, null, unidadeDecisoraColegiada);
    }

    public void distribuirProcesso(Processo processo) throws DAOException {
        distribuirProcesso(processo,null,null,null);
    }

    @Transactional
    public void distribuirProcesso(Processo processo, PessoaFisica relator, UnidadeDecisoraMonocratica unidadeDecisoraMonocratica, UnidadeDecisoraColegiada unidadeDecisoraColegiada) throws DAOException {
    	String idRelator = relator != null ? relator.getIdPessoa().toString() : null;
    	String idUDM = unidadeDecisoraMonocratica != null ? unidadeDecisoraMonocratica.getIdUnidadeDecisoraMonocratica().toString() : null;
    	String idUDC = unidadeDecisoraColegiada != null ? unidadeDecisoraColegiada.getIdUnidadeDecisoraColegiada().toString() : null;
    	
    	setMetadado(EppMetadadoProvider.RELATOR, processo, idRelator);
    	setMetadado(EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA, processo, idUDM);
    	setMetadado(EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA, processo, idUDC);
        processoDAO.update(processo);
    }
    
    private void setMetadado(MetadadoProcessoDefinition metadadoDefinition, Processo processo, String valor) throws DAOException {
    	MetadadoProcesso metadadoExistente = processo.getMetadado(metadadoDefinition);
        if (metadadoExistente != null) {
        	if (valor != null) {
        		metadadoExistente.setValor(valor);
        		metadadoProcessoManager.update(metadadoExistente);
        	} else {
        		metadadoProcessoManager.remove(metadadoExistente);
        		processo.getMetadadoProcessoList().remove(metadadoExistente);
        	}
        } else if (valor != null) {
        	metadadoProcessoManager.addMetadadoProcesso(processo, metadadoDefinition, valor);
        }
    }
}
