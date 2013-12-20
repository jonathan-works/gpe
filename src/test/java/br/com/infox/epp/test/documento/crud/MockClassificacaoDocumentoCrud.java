package br.com.infox.epp.test.documento.crud;

import java.util.List;

import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.test.crud.AbstractMockCrudAction;

public class MockClassificacaoDocumentoCrud extends AbstractMockCrudAction<TipoProcessoDocumento> {
    
    @Override
    public List<TipoProcessoDocumento> getAll() {
        return getGenericManager().findAll(TipoProcessoDocumento.class);
    }
    
}
