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
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
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
        setRelator(processo, relator);
        setUnidadeDecisoraMonocratica(processo, unidadeDecisoraMonocratica);
        setUnidadeDecisoraColegiada(processo, unidadeDecisoraColegiada);
        processoDAO.update(processo);
    }
    
    private void setRelator(Processo processo, PessoaFisica relator)
            throws DAOException {
        if (relator != null) {
            metadadoProcessoManager.addMetadadoProcesso(processo, EppMetadadoProvider.RELATOR, relator.getIdPessoa().toString());
        } else {
            metadadoProcessoManager.remove(processo.getMetadado(EppMetadadoProvider.RELATOR));
        }
    }

    private void setUnidadeDecisoraMonocratica(Processo processo,
            UnidadeDecisoraMonocratica unidadeDecisoraMonocratica)
            throws DAOException {
        if (unidadeDecisoraMonocratica != null) {
            metadadoProcessoManager.addMetadadoProcesso(processo, EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA, unidadeDecisoraMonocratica.getIdUnidadeDecisoraMonocratica().toString());
        } else {
            metadadoProcessoManager.remove(processo.getMetadado(EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA));
        }
    }

    private void setUnidadeDecisoraColegiada(Processo processo, UnidadeDecisoraColegiada unidadeDecisoraColegiada) throws DAOException {
        if (unidadeDecisoraColegiada != null) {
            metadadoProcessoManager.addMetadadoProcesso(processo, EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA, unidadeDecisoraColegiada.getIdUnidadeDecisoraColegiada().toString());
        } else {
            metadadoProcessoManager.remove(processo.getMetadado(EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA));
        }
    }
    
}
