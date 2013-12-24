package br.com.infox.epp.test.documento.crud;

import java.util.List;

import br.com.infox.epp.documento.crud.ClassificacaoDocumentoCrudAction;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.test.core.messages.MockMessagesHandler;
import br.com.infox.epp.test.crud.MockCrudAction;
import br.com.infox.epp.test.infra.MockGenericManager;

public class MockClassificacaoDocumentoCrud extends ClassificacaoDocumentoCrudAction implements MockCrudAction<TipoProcessoDocumento> {
    
    public MockClassificacaoDocumentoCrud() {
        super();
        setGenericManager(new MockGenericManager());
    }
    
    public List<TipoProcessoDocumento> getAll() {
        return getGenericManager().findAll(TipoProcessoDocumento.class);
    }
    
    @Override
    protected final MockMessagesHandler getMessagesHandler() {
        return MockMessagesHandler.instance();
    }
}