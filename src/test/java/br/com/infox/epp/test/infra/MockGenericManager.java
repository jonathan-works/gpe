package br.com.infox.epp.test.infra;

import br.com.infox.core.manager.GenericManager;

public class MockGenericManager extends GenericManager {
    private static final long serialVersionUID = 1L;
    
    public MockGenericManager() {
        super();
        setGenericDAO(new MockGenericDAO());
    }

}
