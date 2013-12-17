package br.com.infox.epp.test.documento.crud;

import br.com.infox.epp.documento.crud.ClassificacaoDocumentoCrudAction;
import br.com.infox.epp.test.core.messages.MockMessagesHandler;
import br.com.infox.epp.test.infra.MockGenericManager;

public class MockClassificacaoDocumentoCrud extends ClassificacaoDocumentoCrudAction {

    public MockClassificacaoDocumentoCrud() {
        super();
        setGenericManager(new MockGenericManager());
        setMessagesInterface(MockMessagesHandler.getInstance());
    }
    
}
