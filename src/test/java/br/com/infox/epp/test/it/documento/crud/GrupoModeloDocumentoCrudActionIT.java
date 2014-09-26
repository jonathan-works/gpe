package br.com.infox.epp.test.it.documento.crud;

import static br.com.infox.core.action.AbstractAction.UPDATED;
import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PEQUENA;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.text.MessageFormat.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.epp.documento.crud.GrupoModeloDocumentoCrudAtion;
import br.com.infox.epp.documento.dao.GrupoModeloDocumentoDAO;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;
import br.com.infox.epp.documento.manager.GrupoModeloDocumentoManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.PersistSuccessTest;
import br.com.infox.epp.test.crud.RunnableTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class GrupoModeloDocumentoCrudActionIT extends AbstractCrudTest<GrupoModeloDocumento> {
    
    private static final Boolean[] BOOLEANS = new Boolean[]{TRUE, FALSE};

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
        .addClasses(GrupoModeloDocumentoCrudAtion.class,
                GrupoModeloDocumentoManager.class,
                GrupoModeloDocumentoDAO.class)
        .createDeployment();
    }
    
    @Override
    protected String getComponentName() {
        return GrupoModeloDocumentoCrudAtion.NAME;
    }
    
    private static final ActionContainer<GrupoModeloDocumento> initEntityAction = new ActionContainer<GrupoModeloDocumento>() {
        @Override
        public void execute(final CrudActions<GrupoModeloDocumento> crudActions) {
            /*
            <wi:inputText id="grupoModeloDocumento"
                label="#{eppmessages['grupoModeloDocumento.grupoModeloDocumento']}"
                value="#{home.instance.grupoModeloDocumento}"
                required="true" maxlength="30" />

            <wi:selectSituacaoRadio id="ativo"
                label="#{eppmessages['field.situacao']}"
                value="#{home.instance.ativo}" />
            */
            final GrupoModeloDocumento entity = getEntity();
            crudActions.setEntityValue("grupoModeloDocumento", entity.getGrupoModeloDocumento());
            crudActions.setEntityValue("ativo", entity.getAtivo());
        }
    };
    
    private static final PersistSuccessTest<GrupoModeloDocumento> PERSIST_SUCCESS = new PersistSuccessTest<GrupoModeloDocumento>(GrupoModeloDocumentoCrudAtion.NAME, initEntityAction);
    
    @Override
    protected ActionContainer<GrupoModeloDocumento> getInitEntityAction() {
        return initEntityAction;
    }

    public static final List<GrupoModeloDocumento> getSuccessfullyPersisted(final ActionContainer<GrupoModeloDocumento> action, final String suffix, final ServletContext servletContext, final HttpSession session) throws Exception {
        final ArrayList<GrupoModeloDocumento> list = new ArrayList<>();
        int i=0;
        for (final Boolean ativo : BOOLEANS) {
            final String grupoModeloDocumento = format("grupoModeloDocumento{0}{1}", ++i, suffix);
            final int length = grupoModeloDocumento.length();
            final int beginIndex = length-DESCRICAO_PEQUENA;
            final GrupoModeloDocumento entity = new GrupoModeloDocumento(grupoModeloDocumento.substring(beginIndex < 0 ? 0:beginIndex, length), ativo);
            final GrupoModeloDocumento e = PERSIST_SUCCESS.runTest(action, entity, servletContext, session);
            list.add(e);
        }
        return list;
    }
    
    @Test
    public void persistSuccessTest() throws Exception {
        final String suffix = "per-suc";
        getSuccessfullyPersisted(null, suffix, servletContext, session);
    }
    
    @Test
    public void persistFailTest() throws Exception {
        int i=0;
        final String suffix = "pers-fail";
        final String successFullyPersisted = fillStr(format("grupoModeloDocumento-{0}-{1}", ++i, suffix), DESCRICAO_PEQUENA);
        final String[] grupoModeloDocFail = new String[]{ null, "", successFullyPersisted, fillStr(format("grupoModeloDocumento-{0}-{1}", ++i, suffix), DESCRICAO_PEQUENA+1) };
        PERSIST_SUCCESS.runTest(new GrupoModeloDocumento(successFullyPersisted, TRUE), servletContext, session);
        for (final String grupoModeloDocumento : grupoModeloDocFail) {
            for (final Boolean ativo : BOOLEANS) {
                persistFail.runTest(new GrupoModeloDocumento(grupoModeloDocumento, ativo), servletContext, session);
            }
        }
        final String grupoModeloDocumento = fillStr(format("grupoModeloDocumento-{0}-{1}", ++i, suffix), DESCRICAO_PEQUENA);
        persistFail.runTest(new GrupoModeloDocumento(grupoModeloDocumento, null), servletContext, session);
    }
    
    private final RunnableTest<GrupoModeloDocumento> updateSuccess = new RunnableTest<GrupoModeloDocumento>(GrupoModeloDocumentoCrudAtion.NAME) {
        @Override
        public void testComponent() {
            final GrupoModeloDocumento entity = getEntity();
            final String grupoModeloDocumento = fillStr(entity.getGrupoModeloDocumento()+"changed", DESCRICAO_PEQUENA);
            assertNotNull("null entity",entity);
            
            assertNotNull("null id", entity.getIdGrupoModeloDocumento());
            resetInstance(entity.getIdGrupoModeloDocumento());
            setEntityValue("grupoModeloDocumento", grupoModeloDocumento);
            assertEquals("failed to update : grupoModeloDocumento", UPDATED, save());
            
            resetInstance(entity.getIdGrupoModeloDocumento());
            setEntityValue("ativo", !entity.getAtivo());
            assertEquals("failed to update : ativo", UPDATED, save());
        }
    };

    private final RunnableTest<GrupoModeloDocumento> updateFail = new RunnableTest<GrupoModeloDocumento>(GrupoModeloDocumentoCrudAtion.NAME) {
        @Override
        public void testComponent() {
            final GrupoModeloDocumento entity = getEntity();
            assertNotNull("null entity",entity);
            assertNotNull("null id", entity.getIdGrupoModeloDocumento());
            for (final String string : new String[]{ null, "", fillStr(entity.getGrupoModeloDocumento()+"changed", DESCRICAO_PEQUENA+1) }) {
                resetInstance(entity.getIdGrupoModeloDocumento());
                setEntityValue("grupoModeloDocumento", string);
                assertEquals("update succeeded: grupoModeloDocumento", false, UPDATED.equals(save()));
            }
            resetInstance(entity.getIdGrupoModeloDocumento());
            setEntityValue("ativo", null);
            assertEquals("update succeeded : ativo", false, UPDATED.equals(save()));
        }
    };
    
    @Test
    public void updateSuccessTest() throws Exception {
        final List<GrupoModeloDocumento> successfullyPersisted = getSuccessfullyPersisted(null, "upd-suc", servletContext, session);
        for (final GrupoModeloDocumento grupoModeloDocumento : successfullyPersisted) {
            updateSuccess.runTest(grupoModeloDocumento, servletContext, session);
        }
    }
    
    @Test
    public void updateFailTest() throws Exception {
        final List<GrupoModeloDocumento> successfullyPersisted = getSuccessfullyPersisted(null, "upd-fail", servletContext, session);
        for (final GrupoModeloDocumento grupoModeloDocumento : successfullyPersisted) {
            updateFail.runTest(grupoModeloDocumento, servletContext, session);
        }
    }
    
    @Test
    public void inactivateSuccessTest() throws Exception {
        for (int i = 1; i <= DESCRICAO_PEQUENA; i++) {
            inactivateSuccess.runTest(new GrupoModeloDocumento(fillStr("grupoModeloDocumento1inact-suc", i), TRUE), servletContext, session);   
        }        
    }

}
