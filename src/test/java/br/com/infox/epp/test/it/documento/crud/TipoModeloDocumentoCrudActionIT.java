package br.com.infox.epp.test.it.documento.crud;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import br.com.infox.epp.documento.crud.TipoModeloDocumentoCrudAction;
import br.com.infox.epp.documento.dao.GrupoModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.ModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.TipoModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.VariavelDAO;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.manager.GrupoModeloDocumentoManager;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoManager;
import br.com.infox.epp.processo.documento.dao.DocumentoBinDAO;
import br.com.infox.epp.processo.documento.dao.DocumentoDAO;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.sigilo.dao.SigiloDocumentoDAO;
import br.com.infox.epp.processo.documento.sigilo.dao.SigiloDocumentoPermissaoDAO;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoManager;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoPermissaoManager;
import br.com.infox.epp.processo.documento.sigilo.service.SigiloDocumentoService;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.PersistSuccessTest;
import br.com.infox.epp.test.crud.RunnableTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaDAO;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;

//@RunWith(Arquillian.class)
public class TipoModeloDocumentoCrudActionIT extends
        AbstractCrudTest<TipoModeloDocumento> {

    private static final Boolean[] BOOLEANS = new Boolean[] { Boolean.TRUE,
            Boolean.FALSE };

    @Deployment
    @OverProtocol(AbstractCrudTest.SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(
                TipoModeloDocumentoManager.class, TipoModeloDocumentoDAO.class,
                TipoModeloDocumentoCrudAction.class,
                ModeloDocumentoManager.class, VariavelDAO.class,
                ModeloDocumentoDAO.class,
                GrupoModeloDocumentoCrudAtion.class,
                GrupoModeloDocumentoManager.class,
                GrupoModeloDocumentoDAO.class,
                DominioVariavelTarefaManager.class,
                DominioVariavelTarefaDAO.class, DocumentoManager.class,
                DocumentoDAO.class,
                SigiloDocumentoService.class, SigiloDocumentoManager.class,
                SigiloDocumentoDAO.class,
                SigiloDocumentoPermissaoManager.class,
                SigiloDocumentoPermissaoDAO.class, DocumentoBinDAO.class,
                DocumentoBinManager.class).createDeployment();
    }

    public static final ActionContainer<TipoModeloDocumento> initEntityAction = new ActionContainer<TipoModeloDocumento>() {
        @Override
        public void execute(final CrudActions<TipoModeloDocumento> crud) {
            final TipoModeloDocumento entity = getEntity();
            crud.setEntityValue("grupoModeloDocumento",
                    entity.getGrupoModeloDocumento());
            crud.setEntityValue("tipoModeloDocumento",
                    entity.getTipoModeloDocumento());
            crud.setEntityValue("abreviacao", entity.getAbreviacao());
            crud.setEntityValue("ativo", entity.getAtivo());
            /*
             * <wi:suggest id="grupoModeloDocumento"
             * suggestProvider="#{grupoModeloDocumentoSuggest}" value=
             * "#{tipoModeloDocumentoCrudAction.instance.grupoModeloDocumento}"
             * label
             * ="#{infoxMessages['tipoModeloDocumento.grupoModeloDocumento']}"
             * required="true" /> <wi:inputText id="tipoModeloDocumento"
             * label="#{infoxMessages['tipoModeloDocumento.tipoModeloDocumento']}"
             * value
             * ="#{tipoModeloDocumentoCrudAction.instance.tipoModeloDocumento}"
             * maxlength="50" required="true" /> <wi:inputText id="abreviacao"
             * label="#{infoxMessages['tipoModeloDocumento.abreviacao']}"
             * value="#{tipoModeloDocumentoCrudAction.instance.abreviacao}"
             * required="true" maxlength="5" /> <wi:selectSituacaoRadio
             * id="ativo" label="#{infoxMessages['field.situacao']}"
             * value="#{tipoModeloDocumentoCrudAction.instance.ativo}" />
             */
        }
    };

    private static final PersistSuccessTest<TipoModeloDocumento> PERSIST_SUCCESS = new PersistSuccessTest<>(
            TipoModeloDocumentoCrudAction.NAME,
            TipoModeloDocumentoCrudActionIT.initEntityAction);

    public static final List<TipoModeloDocumento> getSuccessfullyPersisted(
            final ActionContainer<TipoModeloDocumento> action,
            final String suffix, final ServletContext servletContext,
            final HttpSession session) throws Exception {
        final ArrayList<TipoModeloDocumento> list = new ArrayList<>();

        final List<GrupoModeloDocumento> gruposModeloDocumento = new ArrayList<>(); 
//                GrupoModeloDocumentoCrudActionIT
//                .getSuccessfullyPersisted(null, new StringBuilder("tpMd")
//                        .append(suffix).toString(), servletContext, session);
        for (final GrupoModeloDocumento grupoModeloDocumento : gruposModeloDocumento) {
            for (final Boolean ativo : TipoModeloDocumentoCrudActionIT.BOOLEANS) {
                final String tipoModeloDocumento = MessageFormat.format(
                        "tipoModeloDoc{0}{1}",
                        ++TipoModeloDocumentoCrudActionIT.i, suffix);
                final int tipoBeginIndex = tipoModeloDocumento.length()
                        - LengthConstants.DESCRICAO_PADRAO_METADE;

                final String abreviacao = TipoModeloDocumentoCrudActionIT
                        .getHashedString(tipoModeloDocumento);
                final int abrevBeginIndex = abreviacao.length()
                        - LengthConstants.DESCRICAO_ABREVIADA;

                final TipoModeloDocumento entity = new TipoModeloDocumento(
                        grupoModeloDocumento,
                        tipoModeloDocumento.substring(tipoBeginIndex < 0 ? 0
                                : tipoBeginIndex),
                        abreviacao.substring(abrevBeginIndex < 0 ? 0
                                : abrevBeginIndex), ativo);

                list.add(TipoModeloDocumentoCrudActionIT.PERSIST_SUCCESS
                        .runTest(action, entity, servletContext, session));
            }
        }

        return list;
    }

    private static final String getHashedString(final String value)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest instance = MessageDigest.getInstance("SHA-1");
        instance.reset();
        instance.update(value.getBytes("utf8"));
        return new BigInteger(1, instance.digest()).toString(32);
    }

    @Override
    protected ActionContainer<TipoModeloDocumento> getInitEntityAction() {
        return TipoModeloDocumentoCrudActionIT.initEntityAction;
    }

    @Override
    protected String getComponentName() {
        return TipoModeloDocumentoCrudAction.NAME;
    }

    //@Test
    public void persistSuccessTest() throws Exception {
        TipoModeloDocumentoCrudActionIT.getSuccessfullyPersisted(null,
                "perSuc", this.servletContext, this.session);
    }

    private String getValidString(final String tipoModeloDocumento,
            final int limit) {
        final int beginIndex = tipoModeloDocumento.length() - limit;
        return tipoModeloDocumento.substring(beginIndex < 0 ? 0 : beginIndex);
    }

    private String createValidAbrev(final int numb, final String suffix)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return getValidString(
                TipoModeloDocumentoCrudActionIT.getHashedString(MessageFormat
                        .format("TMD{1}{0}", numb, suffix.charAt(0))),
                LengthConstants.DESCRICAO_ABREVIADA);
    }

    private String createValidDesc(final int i, final String suffix) {
        return getValidString(
                MessageFormat.format("tipoModeloDoc{0}{1}", i, suffix),
                LengthConstants.DESCRICAO_PADRAO_METADE);
    }

    private static int i = 0;

    //@Test
    public void persistFailTest() throws Exception {
        final String suffix = "pers-fail";

        final String tipoModeloDocumentoSuc = createValidDesc(
                ++TipoModeloDocumentoCrudActionIT.i, suffix);

        final String abreviacaoSuc = createValidAbrev(
                TipoModeloDocumentoCrudActionIT.i, suffix);

        final List<GrupoModeloDocumento> gruposModeloDocumento = new ArrayList<>(); 
//                GrupoModeloDocumentoCrudActionIT
//                .getSuccessfullyPersisted(null, new StringBuilder("tpMd")
//                        .append("suffix").toString(), this.servletContext,
//                        this.session);
        TipoModeloDocumentoCrudActionIT.PERSIST_SUCCESS.runTest(
                new TipoModeloDocumento(gruposModeloDocumento.get(0),
                        tipoModeloDocumentoSuc, abreviacaoSuc, Boolean.TRUE),
                this.servletContext, this.session);

        this.persistFail.runTest(
                new TipoModeloDocumento(null, createValidDesc(
                        ++TipoModeloDocumentoCrudActionIT.i, suffix),
                        createValidAbrev(TipoModeloDocumentoCrudActionIT.i,
                                suffix), Boolean.TRUE), this.servletContext,
                this.session);
        final String[] tipoModeloDocFail = new String[] {
                null,
                "",
                tipoModeloDocumentoSuc,
                fillStr(MessageFormat.format("grupoModeloDocumento-{0}-{1}",
                        ++TipoModeloDocumentoCrudActionIT.i, suffix),
                        LengthConstants.DESCRICAO_PEQUENA + 1) };
        testInvalidTipoModeloDoc(abreviacaoSuc, gruposModeloDocumento,
                tipoModeloDocFail);

        final String[] abreviacaoFail = new String[] {
                null,
                "",
                abreviacaoSuc,
                fillStr(createValidAbrev(TipoModeloDocumentoCrudActionIT.i,
                        suffix), LengthConstants.DESCRICAO_ABREVIADA + 1) };
        testInvalidAbreviacao(TipoModeloDocumentoCrudActionIT.i, suffix,
                gruposModeloDocumento, abreviacaoFail);

        this.persistFail.runTest(
                new TipoModeloDocumento(gruposModeloDocumento.get(0),
                        createValidDesc(++TipoModeloDocumentoCrudActionIT.i,
                                suffix), createValidAbrev(
                                TipoModeloDocumentoCrudActionIT.i, suffix),
                        null), this.servletContext, this.session);
    }

    private final RunnableTest<TipoModeloDocumento> updateSuccess = new RunnableTest<TipoModeloDocumento>(
            TipoModeloDocumentoCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final TipoModeloDocumento entity = getEntity();
            Assert.assertNotNull("null entity", entity);
            Assert.assertNotNull("null id", entity.getIdTipoModeloDocumento());

            resetInstance(entity.getIdTipoModeloDocumento());

            setEntityValue(
                    "tipoModeloDocumento",
                    getValidString(entity.getTipoModeloDocumento() + "changed",
                            LengthConstants.DESCRICAO_PEQUENA));

            Assert.assertEquals("failed to update: tipoModeloDocumento",
                    AbstractAction.UPDATED, save());

            resetInstance(entity.getIdTipoModeloDocumento());

            setEntityValue(
                    "abreviacao",
                    getValidString(TipoModeloDocumentoCrudActionIT
                            .getHashedString(entity.getAbreviacao() + "cd"),
                            LengthConstants.DESCRICAO_ABREVIADA));

            Assert.assertEquals("failed to update: abreviacao",
                    AbstractAction.UPDATED, save());

            resetInstance(entity.getIdTipoModeloDocumento());

            setEntityValue("ativo", !entity.getAtivo());

            Assert.assertEquals("failed to update: ativo",
                    AbstractAction.UPDATED, save());

            setEntity(resetInstance(entity.getIdTipoModeloDocumento()));
        }
    };

    //@Test
    public void updateSuccessTest() throws Exception {
        for (final TipoModeloDocumento tipoModeloDocumento : TipoModeloDocumentoCrudActionIT
                .getSuccessfullyPersisted(null, "updSuc", this.servletContext,
                        this.session)) {
            this.updateSuccess.runTest(tipoModeloDocumento,
                    this.servletContext, this.session);
        }
    }

    private void testInvalidAbreviacao(final int number, final String suffix,
            final List<GrupoModeloDocumento> gruposModeloDocumento,
            final String[] abreviacaoFail) throws Exception {
        int i = number;
        for (final GrupoModeloDocumento grupoModeloDocumento : gruposModeloDocumento) {
            for (final String abreviacao : abreviacaoFail) {
                for (final Boolean ativo : TipoModeloDocumentoCrudActionIT.BOOLEANS) {
                    this.persistFail.runTest(new TipoModeloDocumento(
                            grupoModeloDocumento, createValidDesc(++i, suffix),
                            abreviacao, ativo), this.servletContext,
                            this.session);
                }
            }
        }
    }

    private void testInvalidTipoModeloDoc(final String abreviacaoSuc,
            final List<GrupoModeloDocumento> gruposModeloDocumento,
            final String[] tipoModeloDocFail) throws Exception {
        for (final GrupoModeloDocumento grupoModeloDocumento : gruposModeloDocumento) {
            for (final String tipoModeloDocumento : tipoModeloDocFail) {
                for (final Boolean ativo : TipoModeloDocumentoCrudActionIT.BOOLEANS) {
                    this.persistFail.runTest(new TipoModeloDocumento(
                            grupoModeloDocumento, tipoModeloDocumento,
                            abreviacaoSuc, ativo), this.servletContext,
                            this.session);
                }
            }
        }
    }

}
