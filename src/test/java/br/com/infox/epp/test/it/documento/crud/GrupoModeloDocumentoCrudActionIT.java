package br.com.infox.epp.test.it.documento.crud;

import static java.text.MessageFormat.format;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.epp.documento.crud.GrupoModeloDocumentoCrudAtion;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.PersistSuccessTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;

@RunWith(Arquillian.class)
public class GrupoModeloDocumentoCrudActionIT extends AbstractCrudTest<GrupoModeloDocumento> {
    
    @Override
    protected String getComponentName() {
        return GrupoModeloDocumentoCrudAtion.NAME;
    }
    
    public static final ActionContainer<GrupoModeloDocumento> initEntityAction = new ActionContainer<GrupoModeloDocumento>() {
        @Override
        public void execute(CrudActions<GrupoModeloDocumento> crudActions) {
            /*
            <wi:inputText id="grupoModeloDocumento"
                label="#{messages['grupoModeloDocumento.grupoModeloDocumento']}"
                value="#{home.instance.grupoModeloDocumento}"
                required="true" maxlength="30" />

            <wi:selectSituacaoRadio id="ativo"
                label="#{messages['field.situacao']}"
                value="#{home.instance.ativo}" />
            */
            final GrupoModeloDocumento entity = getEntity();
            crudActions.setEntityValue("grupoModeloDocumento", entity.getGrupoModeloDocumento());
            crudActions.setEntityValue("ativo", entity.getAtivo());
        }
    };
    private static final PersistSuccessTest<GrupoModeloDocumento> PERSIST_SUCCESS_TEST = new PersistSuccessTest<GrupoModeloDocumento>(GrupoModeloDocumentoCrudAtion.NAME, initEntityAction);
    
    @Override
    protected ActionContainer<GrupoModeloDocumento> getInitEntityAction() {
        return initEntityAction;
    }
    
    @Test
    public void persistSuccessTest() throws Exception {
        final String suffix = "per-suc";
        int i=0;
        PERSIST_SUCCESS_TEST.runTest(new GrupoModeloDocumento(format("grupoModeloDocumento-{0}-{1}", ++i, suffix), Boolean.TRUE), servletContext, session);
        PERSIST_SUCCESS_TEST.runTest(new GrupoModeloDocumento(format("grupoModeloDocumento-{0}-{1}", ++i, suffix), Boolean.FALSE), servletContext, session);
    }

}
