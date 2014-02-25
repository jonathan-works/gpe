package br.com.infox.epp.test.it.documento.crud;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.epp.documento.crud.GrupoModeloDocumentoCrudAtion;
import br.com.infox.epp.documento.crud.TipoModeloDocumentoCrudAction;
import br.com.infox.epp.documento.dao.GrupoModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.ModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.TipoModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.VariavelDAO;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.manager.GrupoModeloDocumentoManager;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class TipoModeloDocumentoCrudActionIT extends AbstractCrudTest<TipoModeloDocumento>{

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
        .addClasses(TipoModeloDocumentoManager.class, TipoModeloDocumentoDAO.class,
                ModeloDocumentoManager.class, VariavelDAO.class,
                ModeloDocumentoDAO.class, GrupoModeloDocumentoCrudActionIT.class,
                GrupoModeloDocumentoCrudAtion.class, GrupoModeloDocumentoManager.class,
                GrupoModeloDocumentoDAO.class)
        .createDeployment();
    }
    
    public static final ActionContainer<TipoModeloDocumento> initEntityAction = new ActionContainer<TipoModeloDocumento>() {
        @Override
        public void execute(CrudActions<TipoModeloDocumento> crudActions) {
            
        }
    };
    
    @Override
    protected ActionContainer<TipoModeloDocumento> getInitEntityAction() {
        return super.getInitEntityAction();
    }
    
    @Override
    protected String getComponentName() {
        return TipoModeloDocumentoCrudAction.NAME;
    }

    @Test
    public void persistSuccessTest() throws Exception {
    }
    
}
