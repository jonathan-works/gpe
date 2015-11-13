package br.com.infox.epp.test.it.access.crud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import br.com.infox.constants.LengthConstants;
import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.crud.LocalizacaoCrudAction;
import br.com.infox.epp.access.dao.BloqueioUsuarioDAO;
import br.com.infox.epp.access.dao.LocalizacaoDAO;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.documento.dao.ModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.VariavelDAO;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.mail.entity.EMailData;
import br.com.infox.epp.mail.service.AccessMailService;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.documento.dao.DocumentoBinDAO;
import br.com.infox.epp.processo.documento.dao.DocumentoDAO;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.sigilo.dao.SigiloDocumentoDAO;
import br.com.infox.epp.processo.documento.sigilo.dao.SigiloDocumentoPermissaoDAO;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoManager;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoPermissaoManager;
import br.com.infox.epp.processo.documento.sigilo.service.SigiloDocumentoService;
import br.com.infox.epp.system.dao.ParametroDAO;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.PersistSuccessTest;
import br.com.infox.epp.test.crud.RunnableTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaDAO;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;
import junit.framework.Assert;

//@RunWith(Arquillian.class)
public class LocalizacaoCrudActionIT extends AbstractCrudTest<Localizacao> {
    private static final String SERVLET_3_0 = "Servlet 3.0";

    private static ActionContainer<Localizacao> initEntityAction = new ActionContainer<Localizacao>() {
        @Override
        public void execute(final CrudActions<Localizacao> crudActions) {
            final Localizacao entity = getEntity();
            crudActions.setEntityValue("localizacao", entity.getLocalizacao());// required
            // crudActions.setEntityValue("estrutura", entity.getEstrutura());//
            // required
            crudActions.setEntityValue("localizacaoPai",
                    entity.getLocalizacaoPai());
            crudActions.setEntityValue("estruturaFilho",
                    entity.getEstruturaFilho());
            crudActions.setEntityValue("ativo", entity.getAtivo());// required
        }
    };

    private static final PersistSuccessTest<Localizacao> persistSuccessLocalizacao = new PersistSuccessTest<>(
            LocalizacaoCrudAction.NAME,
            LocalizacaoCrudActionIT.initEntityAction);

    private static final Localizacao persistEstruturaFilho(
            final Localizacao entity,
            final ActionContainer<Localizacao> action,
            final HashMap<String, Localizacao> localizacoes,
            final ServletContext servletContext, final HttpSession session)
            throws Exception {
        Localizacao result = null;
        if (entity != null) {
            // entity.setEstruturaFilho(persistEstruturaFilho(entity.getEstruturaFilho(),
            // action, localizacoes, servletContext, session));
            result = LocalizacaoCrudActionIT.persistIfNotOnMap(entity, action,
                    localizacoes, servletContext, session);
        }
        return result;
    }

    private static final Localizacao persistIfNotOnMap(
            final Localizacao entity,
            final ActionContainer<Localizacao> action,
            final HashMap<String, Localizacao> localizacoes,
            final ServletContext servletContext, final HttpSession session)
            throws Exception {
        Localizacao result;
        final String key = entity.getLocalizacao();
        if (localizacoes.containsKey(key)) {
            result = localizacoes.get(key);
        } else {
            result = LocalizacaoCrudActionIT.persistSuccessLocalizacao.runTest(
                    action, entity, servletContext, session);
            localizacoes.put(key, result);
        }
        return result;
    }

    private static final Localizacao persistParent(final Localizacao entity,
            final ActionContainer<Localizacao> action,
            final HashMap<String, Localizacao> localizacoes,
            final ServletContext servletContext, final HttpSession session)
            throws Exception {
        Localizacao result = null;
        if (entity != null) {
            entity.setParent(LocalizacaoCrudActionIT.persistParent(
                    entity.getParent(), action, localizacoes, servletContext,
                    session));
            result = LocalizacaoCrudActionIT.persistIfNotOnMap(entity, action,
                    localizacoes, servletContext, session);
        }
        return result;
    }

    private static final Localizacao persistSuccessTest(
            final Localizacao entity,
            final ActionContainer<Localizacao> action,
            final HashMap<String, Localizacao> localizacoes,
            final ServletContext servletContext, final HttpSession session)
            throws Exception {
        final Localizacao localizacaoPai = LocalizacaoCrudActionIT
                .persistParent(entity.getLocalizacaoPai(), action,
                        localizacoes, servletContext, session);
        entity.setLocalizacaoPai(localizacaoPai);
        // final Localizacao estruturaFilho =
        // persistEstruturaFilho(entity.getEstruturaFilho(), action,
        // localizacoes, servletContext, session);
        // entity.setEstruturaFilho(estruturaFilho);
        return LocalizacaoCrudActionIT.persistIfNotOnMap(entity, action,
                localizacoes, servletContext, session);
    }

    private static void updateValueAndTest(final Integer id,
            final String field, final Object value,
            final boolean wasSuccessful,
            final CrudActions<Localizacao> crudActions) {
        crudActions.resetInstance(id);
        crudActions.setEntityValue(field, value);
        Assert.assertEquals("updated", wasSuccessful,
                AbstractAction.UPDATED.equals(crudActions.save()));
    }

    @Deployment
    @OverProtocol(LocalizacaoCrudActionIT.SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(
                LocalizacaoCrudAction.class, LocalizacaoManager.class,
                LocalizacaoTreeHandler.class, LocalizacaoDAO.class,
                Authenticator.class, UsuarioLoginManager.class,
                UsuarioLoginDAO.class, AuthenticatorService.class,
                ProcessoDAO.class, BloqueioUsuarioManager.class,
                BloqueioUsuarioDAO.class, UsuarioLoginManager.class,
                PasswordService.class, AccessMailService.class,
                ModeloDocumentoManager.class, VariavelDAO.class,
                ModeloDocumentoDAO.class, ParametroManager.class,
                ParametroDAO.class, EMailData.class,
                DominioVariavelTarefaManager.class,
                DominioVariavelTarefaDAO.class, DocumentoManager.class,
                DocumentoDAO.class,
                SigiloDocumentoService.class, SigiloDocumentoManager.class,
                SigiloDocumentoDAO.class,
                SigiloDocumentoPermissaoManager.class,
                SigiloDocumentoPermissaoDAO.class, DocumentoBinDAO.class,
                DocumentoBinManager.class).createDeployment();
    }

    public static final List<Localizacao> getSuccessfullyPersisted(
            final ActionContainer<Localizacao> action, final String suffix,
            final ServletContext servletContext, final HttpSession session)
            throws Exception {
        final HashMap<String, Localizacao> localizacoes = new HashMap<>();

        // final Localizacao estruturaEPP = new
        // Localizacao(format("EPP{0}",suffix), Boolean.TRUE, Boolean.TRUE);
        // final Localizacao estruturaEmpresa = new
        // Localizacao(format("Estrutura Empresa{0}",suffix), Boolean.TRUE,
        // Boolean.TRUE);
        //
        // final Localizacao localizacaoGerencia = new
        // Localizacao(format("Gerência{0}",suffix), Boolean.FALSE,
        // Boolean.TRUE, estruturaEmpresa, null);
        //
        // persistSuccessTest(new Localizacao(format("Setor Pessoal{0}",suffix),
        // Boolean.FALSE, Boolean.TRUE, localizacaoGerencia, null),
        // action,localizacoes, servletContext, session);
        // persistSuccessTest(new
        // Localizacao(format("Setor Financeiro{0}",suffix), Boolean.FALSE,
        // Boolean.TRUE, localizacaoGerencia, null), action,localizacoes,
        // servletContext, session);
        // persistSuccessTest(new
        // Localizacao(format("Setor de Compras{0}",suffix), Boolean.FALSE,
        // Boolean.TRUE, localizacaoGerencia, null), action,localizacoes,
        // servletContext, session);
        //
        // persistSuccessTest(new
        // Localizacao(format("Empresa Hipotética{0}",suffix), Boolean.FALSE,
        // Boolean.FALSE, estruturaEPP, estruturaEmpresa), action,localizacoes,
        // servletContext, session);
        //
        return new ArrayList<Localizacao>(localizacoes.values());
    }

    private final RunnableTest<Localizacao> inactivateSuccess = new RunnableTest<Localizacao>(
            LocalizacaoCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Localizacao entity = getEntity();
            if (entity.getIdLocalizacao() != null) {
                resetInstance(entity.getIdLocalizacao());
            }
            Assert.assertEquals("children inactive", true,
                    assertChildrenActive(getInstance()));
            Assert.assertEquals("inactivate success true",
                    AbstractAction.UPDATED, inactivate());
            Assert.assertEquals("children active", true,
                    assertChildrenInactive(getInstance()));
        }
    };

    private final RunnableTest<Localizacao> updateSuccess = new RunnableTest<Localizacao>(
            LocalizacaoCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Localizacao entity = getEntity();
            final Integer id = entity.getIdLocalizacao();
            LocalizacaoCrudActionIT.updateValueAndTest(id, "localizacao",
                    entity.getLocalizacao() + ".changed", true, this);
            LocalizacaoCrudActionIT.updateValueAndTest(id, "localizacaoPai",
                    null, true, this);
            final Localizacao localizacao = this.resetInstance(id);
            Assert.assertEquals("localizacao changed", true, localizacao
                    .getLocalizacao().endsWith(".changed"));
            Assert.assertNull("localizacaoPai null",
                    localizacao.getLocalizacaoPai());
        }
    };

    private final RunnableTest<Localizacao> updateFail = new RunnableTest<Localizacao>(
            LocalizacaoCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Localizacao entity = getEntity();
            final Integer id = entity.getIdLocalizacao();

            LocalizacaoCrudActionIT.updateValueAndTest(
                    id,
                    "localizacao",
                    fillStr(entity.getLocalizacao() + ".changed",
                            LengthConstants.DESCRICAO_PADRAO + 1), false, this);

            LocalizacaoCrudActionIT.updateValueAndTest(id, "localizacao", null,
                    false, this);

            LocalizacaoCrudActionIT.updateValueAndTest(id, "estrutura", null,
                    false, this);

            LocalizacaoCrudActionIT.updateValueAndTest(id, "ativo", null,
                    false, this);
        }
    };

    private boolean assertChildrenActive(final Localizacao l) {
        boolean result = l.getAtivo();
        for (final Localizacao localizacao : l.getLocalizacaoList()) {
            result = result && assertChildrenActive(localizacao);
        }
        return result;
    }

    private boolean assertChildrenInactive(final Localizacao l) {
        boolean result = l.getAtivo();
        for (final Localizacao localizacao : l.getLocalizacaoList()) {
            result = result || assertChildrenInactive(localizacao);
        }
        return !result;
    }

    @Override
    protected String getComponentName() {
        return LocalizacaoCrudAction.NAME;
    }

    @Override
    protected ActionContainer<Localizacao> getInitEntityAction() {
        return LocalizacaoCrudActionIT.initEntityAction;
    }

    //@Test
    public void inactivateSuccessTest() throws Exception {
        final List<Localizacao> successfullyPersisted = LocalizacaoCrudActionIT
                .getSuccessfullyPersisted(null, ".inac-suc",
                        this.servletContext, this.session);
        for (final Localizacao localizacao : successfullyPersisted) {
            if ("Estrutura Empresa.inac-suc".equals(localizacao
                    .getLocalizacao())) {
                this.inactivateSuccess.runTest(localizacao,
                        this.servletContext, this.session);
                break;
            }
        }
    }

    //@Test
    public void persistFailTest() throws Exception {
        // FALHAS AO INSERIR DESCRIÇÕES INVÁLIDAS
        // persistFail.runTest(new Localizacao(fillStr("Setor Pessoal.Fail",
        // LengthConstants.DESCRICAO_PADRAO+1), Boolean.TRUE, Boolean.TRUE,
        // null, null), servletContext, session);
        // persistFail.runTest(new Localizacao(null, Boolean.TRUE,
        // Boolean.TRUE), servletContext, session);
        // FALHAS AO INSERIR VALOR INVÁLIDO PARA ESTRUTURA
        // persistFail.runTest(new Localizacao("Setor Pessoal.Fail", null,
        // Boolean.TRUE), servletContext, session);

        // FALHAS AO INSERIR VALOR INVÁLIDO PARA ATIVO
        this.persistFail.runTest(new Localizacao("Setor Pessoal.Fail",
                Boolean.TRUE, null), this.servletContext, this.session);
    }

    //@Test
    public void persistSuccessTest() throws Exception {
        LocalizacaoCrudActionIT.getSuccessfullyPersisted(null, "",
                this.servletContext, this.session);
    }

    //@Test
    public void updateFailTest() throws Exception {
        final List<Localizacao> successfullyPersisted = LocalizacaoCrudActionIT
                .getSuccessfullyPersisted(null, "updtFail",
                        this.servletContext, this.session);
        for (final Localizacao localizacao : successfullyPersisted) {
            this.updateFail.runTest(localizacao, this.servletContext,
                    this.session);
        }
    }

    //@Test
    public void updateSuccessTest() throws Exception {
        final List<Localizacao> successfullyPersisted = LocalizacaoCrudActionIT
                .getSuccessfullyPersisted(null, "updtSuc", this.servletContext,
                        this.session);
        for (final Localizacao localizacao : successfullyPersisted) {
            this.updateSuccess.runTest(localizacao, this.servletContext,
                    this.session);
        }
    }

}