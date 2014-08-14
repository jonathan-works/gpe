package br.com.infox.epp.tce.prestacaocontas.modelo.service;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ModeloPrestacaoContas;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ModeloPrestacaoContasClassificacaoDocumento;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ResponsavelModeloPrestacaoContas;
import br.com.infox.epp.tce.prestacaocontas.modelo.manager.ModeloPrestacaoContasManager;

@Name(ModeloPrestacaoContasService.NAME)
@AutoCreate
@Scope(ScopeType.EVENT)
public class ModeloPrestacaoContasService {
    public static final String NAME = "modeloPrestacaoContasService";
    
    @In
    private ModeloPrestacaoContasManager modeloPrestacaoContasManager;
    @In
    private GenericManager genericManager;
    
    public void adicionarClassificacaoDocumento(ModeloPrestacaoContasClassificacaoDocumento classificacao) throws DAOException {
        genericManager.persist(classificacao);
        ModeloPrestacaoContas modelo = classificacao.getModeloPrestacaoContas();
        if (!modelo.isValido() && modeloPrestacaoContasManager.totalResponsaveisAssociados(modelo) > 0) {
            modelo.setValido(true);
            modeloPrestacaoContasManager.update(modelo);
        }
    }
    
    public void adicionarResponsavel(ResponsavelModeloPrestacaoContas responsavel) throws DAOException {
        genericManager.persist(responsavel);
        ModeloPrestacaoContas modelo = responsavel.getModeloPrestacaoContas();
        if (!modelo.isValido() && modeloPrestacaoContasManager.totalDocumentosAssociados(modelo) > 0) {
            modelo.setValido(true);
            modeloPrestacaoContasManager.update(modelo);
        }
    }
    
    public void removerResponsavel(ResponsavelModeloPrestacaoContas responsavel) throws DAOException {
        if (modeloPrestacaoContasManager.totalResponsaveisAssociados(responsavel.getModeloPrestacaoContas()) == 1) {
            throw new DAOException("Não é possível remover o último cargo de responsável do modelo");
        }
        genericManager.remove(responsavel);
    }
    
    public void removerClassificacaoDocumento(ModeloPrestacaoContasClassificacaoDocumento classificacao) throws DAOException {
        if (modeloPrestacaoContasManager.totalDocumentosAssociados(classificacao.getModeloPrestacaoContas()) == 1) {
            throw new DAOException("Não é possível remover o último documento do modelo");
        }
        genericManager.remove(classificacao);
    }
}
