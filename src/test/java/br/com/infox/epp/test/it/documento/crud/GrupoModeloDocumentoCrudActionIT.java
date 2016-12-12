package br.com.infox.epp.test.it.documento.crud;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;

import br.com.infox.constants.LengthConstants;
import br.com.infox.core.action.AbstractAction;
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

//@RunWith(Arquillian.class)
public class GrupoModeloDocumentoCrudActionIT extends
        AbstractCrudTest<GrupoModeloDocumento> {

    private static final Boolean[] BOOLEANS = new Boolean[] { Boolean.TRUE,
            Boolean.FALSE };

    @Deployment
    @OverProtocol(AbstractCrudTest.SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(
                GrupoModeloDocumentoCrudAtion.class,
                GrupoModeloDocumentoManager.class,
                GrupoModeloDocumentoDAO.class).createDeployment();
    }

    @Override
    protected String getComponentName() {
        return GrupoModeloDocumentoCrudAtion.NAME;
    }

    private static final ActionContainer<GrupoModeloDocumento> initEntityAction = new ActionContainer<GrupoModeloDocumento>() {
        @Override
        public void execute(final CrudActions<GrupoModeloDocumento> crudActions) {
            /*
             * <wi:inputText id="grupoModeloDocumento"
             * label="#{infoxMessages['grupoModeloDocumento.grupoModeloDocumento']}"
             * value="#{home.instance.grupoModeloDocumento}" required="true"
             * maxlength="30" />
             * 
             * <wi:selectSituacaoRadio id="ativo"
             * label="#{infoxMessages['field.situacao']}"
             * value="#{home.instance.ativo}" />
             */
            final GrupoModeloDocumento entity = getEntity();
            crudActions.setEntityValue("grupoModeloDocumento",
                    entity.getGrupoModeloDocumento());
            crudActions.setEntityValue("ativo", entity.getAtivo());
        }
    };

    private static final PersistSuccessTest<GrupoModeloDocumento> PERSIST_SUCCESS = new PersistSuccessTest<GrupoModeloDocumento>(
            GrupoModeloDocumentoCrudAtion.NAME,
            GrupoModeloDocumentoCrudActionIT.initEntityAction);

    @Override
    protected ActionContainer<GrupoModeloDocumento> getInitEntityAction() {
        return GrupoModeloDocumentoCrudActionIT.initEntityAction;
    }

    public static final List<GrupoModeloDocumento> getSuccessfullyPersisted(
            final ActionContainer<GrupoModeloDocumento> action,
            final String suffix, final ServletContext servletContext,
            final HttpSession session) throws Exception {
        final ArrayList<GrupoModeloDocumento> list = new ArrayList<>();
        int i = 0;
        for (final Boolean ativo : GrupoModeloDocumentoCrudActionIT.BOOLEANS) {
            final String grupoModeloDocumento = MessageFormat.format(
                    "grupoModeloDocumento{0}{1}", ++i, suffix);
            final int length = grupoModeloDocumento.length();
            final int beginIndex = length - LengthConstants.DESCRICAO_PEQUENA;
            final GrupoModeloDocumento entity = new GrupoModeloDocumento(
                    grupoModeloDocumento.substring(beginIndex < 0 ? 0
                            : beginIndex, length), ativo);
            final GrupoModeloDocumento e = GrupoModeloDocumentoCrudActionIT.PERSIST_SUCCESS
                    .runTest(action, entity, servletContext, session);
            list.add(e);
        }
        return list;
    }

    //@Test
    public void persistSuccessTest() throws Exception {
        final String suffix = "per-suc";
        GrupoModeloDocumentoCrudActionIT.getSuccessfullyPersisted(null, suffix,
                this.servletContext, this.session);
    }

    //@Test
    public void persistFailTest() throws Exception {
        int i = 0;
        final String suffix = "pers-fail";
        final String successFullyPersisted = fillStr(MessageFormat.format(
                "grupoModeloDocumento-{0}-{1}", ++i, suffix),
                LengthConstants.DESCRICAO_PEQUENA);
        final String[] grupoModeloDocFail = new String[] {
                null,
                "",
                successFullyPersisted,
                fillStr(MessageFormat.format("grupoModeloDocumento-{0}-{1}",
                        ++i, suffix), LengthConstants.DESCRICAO_PEQUENA + 1) };
        GrupoModeloDocumentoCrudActionIT.PERSIST_SUCCESS.runTest(
                new GrupoModeloDocumento(successFullyPersisted, Boolean.TRUE),
                this.servletContext, this.session);
        for (final String grupoModeloDocumento : grupoModeloDocFail) {
            for (final Boolean ativo : GrupoModeloDocumentoCrudActionIT.BOOLEANS) {
                this.persistFail.runTest(new GrupoModeloDocumento(
                        grupoModeloDocumento, ativo), this.servletContext,
                        this.session);
            }
        }
        final String grupoModeloDocumento = fillStr(MessageFormat.format(
                "grupoModeloDocumento-{0}-{1}", ++i, suffix),
                LengthConstants.DESCRICAO_PEQUENA);
        this.persistFail.runTest(new GrupoModeloDocumento(grupoModeloDocumento,
                null), this.servletContext, this.session);
    }

    private final RunnableTest<GrupoModeloDocumento> updateSuccess = new RunnableTest<GrupoModeloDocumento>(
            GrupoModeloDocumentoCrudAtion.NAME) {
        @Override
        public void testComponent() {
            final GrupoModeloDocumento entity = getEntity();
            final String grupoModeloDocumento = fillStr(
                    entity.getGrupoModeloDocumento() + "changed",
                    LengthConstants.DESCRICAO_PEQUENA);
            Assert.assertNotNull("null entity", entity);

            Assert.assertNotNull("null id", entity.getIdGrupoModeloDocumento());
            resetInstance(entity.getIdGrupoModeloDocumento());
            setEntityValue("grupoModeloDocumento", grupoModeloDocumento);
            Assert.assertEquals("failed to update : grupoModeloDocumento",
                    AbstractAction.UPDATED, save());

            resetInstance(entity.getIdGrupoModeloDocumento());
            setEntityValue("ativo", !entity.getAtivo());
            Assert.assertEquals("failed to update : ativo",
                    AbstractAction.UPDATED, save());
        }
    };

    private final RunnableTest<GrupoModeloDocumento> updateFail = new RunnableTest<GrupoModeloDocumento>(
            GrupoModeloDocumentoCrudAtion.NAME) {
        @Override
        public void testComponent() {
            final GrupoModeloDocumento entity = getEntity();
            Assert.assertNotNull("null entity", entity);
            Assert.assertNotNull("null id", entity.getIdGrupoModeloDocumento());
            for (final String string : new String[] {
                    null,
                    "",
                    fillStr(entity.getGrupoModeloDocumento() + "changed",
                            LengthConstants.DESCRICAO_PEQUENA + 1) }) {
                resetInstance(entity.getIdGrupoModeloDocumento());
                setEntityValue("grupoModeloDocumento", string);
                Assert.assertEquals("update succeeded: grupoModeloDocumento",
                        false, AbstractAction.UPDATED.equals(save()));
            }
            resetInstance(entity.getIdGrupoModeloDocumento());
            setEntityValue("ativo", null);
            Assert.assertEquals("update succeeded : ativo", false,
                    AbstractAction.UPDATED.equals(save()));
        }
    };

    //@Test
    public void updateSuccessTest() throws Exception {
        final List<GrupoModeloDocumento> successfullyPersisted = GrupoModeloDocumentoCrudActionIT
                .getSuccessfullyPersisted(null, "upd-suc", this.servletContext,
                        this.session);
        for (final GrupoModeloDocumento grupoModeloDocumento : successfullyPersisted) {
            this.updateSuccess.runTest(grupoModeloDocumento,
                    this.servletContext, this.session);
        }
    }

    //@Test
    public void updateFailTest() throws Exception {
        final List<GrupoModeloDocumento> successfullyPersisted = GrupoModeloDocumentoCrudActionIT
                .getSuccessfullyPersisted(null, "upd-fail",
                        this.servletContext, this.session);
        for (final GrupoModeloDocumento grupoModeloDocumento : successfullyPersisted) {
            this.updateFail.runTest(grupoModeloDocumento, this.servletContext,
                    this.session);
        }
    }

    //@Test
    public void inactivateSuccessTest() throws Exception {
        for (int i = 1; i <= LengthConstants.DESCRICAO_PEQUENA; i++) {
            this.inactivateSuccess
                    .runTest(
                            new GrupoModeloDocumento(fillStr(
                                    "grupoModeloDocumento1inact-suc", i),
                                    Boolean.TRUE), this.servletContext,
                            this.session);
        }
    }

}
