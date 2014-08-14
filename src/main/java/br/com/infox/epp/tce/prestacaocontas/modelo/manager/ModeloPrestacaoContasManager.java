package br.com.infox.epp.tce.prestacaocontas.modelo.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.tce.prestacaocontas.modelo.dao.ModeloPrestacaoContasDAO;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ModeloPrestacaoContas;

@Name(ModeloPrestacaoContasManager.NAME)
@AutoCreate
public class ModeloPrestacaoContasManager extends Manager<ModeloPrestacaoContasDAO, ModeloPrestacaoContas> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "modeloPrestacaoContasManager";
    
    @Override
    public ModeloPrestacaoContas persist(ModeloPrestacaoContas o) throws DAOException {
        validateAssociations(o);
        return super.persist(o);
    }

    
    @Override
    public ModeloPrestacaoContas update(ModeloPrestacaoContas o) throws DAOException {
        validateAssociations(o);
        return super.update(o);
    }
    
    private void validateAssociations(ModeloPrestacaoContas o) throws DAOException {
        if (o.getClassificacoesDocumento().isEmpty()) {
            throw new DAOException("O modelo não possui documentos associados");
        }
        if (o.getResponsaveis().isEmpty()) {
            throw new DAOException("O modelo não possui responsáveis associados");
        }
    }
}
