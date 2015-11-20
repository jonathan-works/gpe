package br.com.infox.epp.test.it.access.crud;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import br.com.infox.constants.LengthConstants;
import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.access.crud.BloqueioUsuarioCrudAction;
import br.com.infox.epp.access.crud.UsuarioLoginCrudAction;
import br.com.infox.epp.access.dao.BloqueioUsuarioDAO;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.documento.dao.ModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.VariavelDAO;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.mail.command.SendmailCommand;
import br.com.infox.epp.mail.entity.EMailData;
import br.com.infox.epp.mail.service.AccessMailService;
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
import br.com.infox.epp.test.crud.RunnableTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaDAO;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;
import br.com.infox.log.LogProvider;
import br.com.infox.seam.exception.BusinessException;
import junit.framework.Assert;

//@RunWith(Arquillian.class)
public class BloqueioUsuarioCrudActionIT extends
        AbstractCrudTest<BloqueioUsuario> {

    @Deployment
    @OverProtocol(AbstractCrudTest.SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(
                BloqueioUsuarioCrudAction.class, BloqueioUsuarioManager.class,
                BloqueioUsuarioDAO.class, ParametroManager.class,
                ParametroDAO.class, UsuarioLoginCrudAction.class,
                PasswordService.class, AccessMailService.class,
                UsuarioLoginManager.class, BusinessException.class,
                UsuarioLoginDAO.class, ModeloDocumentoManager.class,
                EMailData.class, UsuarioLoginDAO.class,
                ModeloDocumentoDAO.class, VariavelDAO.class, LogProvider.class,
                SendmailCommand.class, DominioVariavelTarefaManager.class,
                DominioVariavelTarefaDAO.class, DocumentoManager.class,
                DocumentoDAO.class, 
                SigiloDocumentoService.class, SigiloDocumentoManager.class,
                SigiloDocumentoDAO.class,
                SigiloDocumentoPermissaoManager.class,
                SigiloDocumentoPermissaoDAO.class, DocumentoBinDAO.class,
                DocumentoBinManager.class).createDeployment();
    }

    private final RunnableTest<UsuarioLogin> persistUsuario = new PersistUsuarioTest();
    /*
     * <action execute=
     * "#{bloqueioUsuarioCrudAction.setUsuarioAtual(usuarioLoginCrudAction.instance)}"
     * if="#{usuarioLoginCrudAction.tab eq 'historicoBloqueioUsuario'}"/>
     * <action execute=
     * "#{bloqueioUsuarioList.entity.setUsuario(usuarioLoginCrudAction.instance)}"
     * if="#{usuarioLoginCrudAction.tab eq 'historicoBloqueioUsuario'}"/>
     * 
     * 
     * <wi:dataForm formId="bloqueioUsuarioForm"
     * formTitle="#{infoxMessages['form.title']}"
     * home="#{bloqueioUsuarioCrudAction}"
     * requiredForm="#{not bloqueioUsuarioCrudAction.usuarioAtual.bloqueio}">
     * <wi:outputText id="bloqueio"
     * value="#{bloqueioUsuarioCrudAction.usuarioAtual.bloqueio ? 'Sim': 'Não'}"
     * label="#{infoxMessages['usuario.bloqueio']}" />
     * 
     * <ui:define name="buttons"> <wi:commandButton id="bloqueioButton"
     * rendered="#{not bloqueioUsuarioCrudAction.usuarioAtual.bloqueio}"
     * value="#{infoxMessages['usuario.bloquear']}"
     * action="bloqueioUsuarioCrudAction.bloquear"
     * reRender="historicoBloqueioUsuario, pageBodyDialogMessage"/>
     * <wi:commandButton id="desbloqueioButton"
     * rendered="#{bloqueioUsuarioCrudAction.usuarioAtual.bloqueio}"
     * value="#{infoxMessages['usuario.desbloquear']}"
     * action="bloqueioUsuarioCrudAction.desbloquear"
     * reRender="historicoBloqueioUsuario, pageBodyDialogMessage"/> </ui:define>
     * </wi:dataForm>
     */
    public static final ActionContainer<BloqueioUsuario> initEntityAction = new ActionContainer<BloqueioUsuario>() {
        @Override
        public void execute(final CrudActions<BloqueioUsuario> crudActions) {
            final BloqueioUsuario entity = getEntity();
            // dataPrevisaoDesbloqueio
            crudActions.setEntityValue("dataPrevisaoDesbloqueio",
                    entity.getDataPrevisaoDesbloqueio());
            crudActions.setEntityValue("motivoBloqueio",
                    entity.getMotivoBloqueio());
        }
    };

    @Override
    protected ActionContainer<BloqueioUsuario> getInitEntityAction() {
        return BloqueioUsuarioCrudActionIT.initEntityAction;
    }

    @Override
    protected String getComponentName() {
        return BloqueioUsuarioCrudAction.NAME;
    }

    private static int id = 0;

    //@Test
    public void BloqueioUsuarioSuccessTest() throws Exception {
        final String motivoBloqueio = "Motivo de Bloqueio";
        final GregorianCalendar calendar = new GregorianCalendar();

        for (int i = 0; i < 50; i++) {
            final UsuarioLogin usuarioLogin = this.persistUsuario.runTest(
                    createUsuario("per-success",
                            ++BloqueioUsuarioCrudActionIT.id),
                    this.servletContext, this.session);
            calendar.add(Calendar.DAY_OF_MONTH, (i + 1) * 5);
            testeBloqueioDesbloqueioSuccess(usuarioLogin, calendar.getTime(),
                    motivoBloqueio);
        }
    }

    //@Test
    public void BloqueioUsuarioFailTest() throws Exception {
        final String motivoBloqueio = "Motivo de Bloqueio";

        // for(int i=0;i<50;i++)
        final int i = 0;
        {
            final UsuarioLogin usuarioLogin = this.persistUsuario
                    .runTest(
                            createUsuario("per-fail",
                                    ++BloqueioUsuarioCrudActionIT.id),
                            this.servletContext, this.session);
            final GregorianCalendar calendar = new GregorianCalendar();
            new PersistBloqueioFailTest(usuarioLogin).runTest(
                    new BloqueioUsuario(null, null), this.servletContext,
                    this.session);
            new PersistBloqueioFailTest(usuarioLogin).runTest(
                    new BloqueioUsuario(null, ""), this.servletContext,
                    this.session);
            new PersistBloqueioFailTest(usuarioLogin).runTest(
                    new BloqueioUsuario(null, fillStr(motivoBloqueio,
                            LengthConstants.DESCRICAO_ENTIDADE + 1)),
                    this.servletContext, this.session);
            calendar.add(Calendar.DAY_OF_MONTH, -5);
            new PersistBloqueioFailTest(usuarioLogin).runTest(
                    new BloqueioUsuario(calendar.getTime(), motivoBloqueio),
                    this.servletContext, this.session);
            calendar.add(Calendar.DAY_OF_MONTH, (i + 2) * 5);
            testeBloqueioDesbloqueioSuccess(usuarioLogin, calendar.getTime(),
                    motivoBloqueio);

            new PersistBloqueioFailTest(usuarioLogin, Boolean.TRUE).runTest(
                    new BloqueioUsuario(null, null), this.servletContext,
                    this.session);
            new PersistBloqueioFailTest(usuarioLogin, Boolean.TRUE).runTest(
                    new BloqueioUsuario(null, ""), this.servletContext,
                    this.session);
            new PersistBloqueioFailTest(usuarioLogin, Boolean.TRUE).runTest(
                    new BloqueioUsuario(null, fillStr(motivoBloqueio,
                            LengthConstants.DESCRICAO_ENTIDADE + 1)),
                    this.servletContext, this.session);
            calendar.add(Calendar.DAY_OF_MONTH, (-i - 2) * 10);
            new PersistBloqueioFailTest(usuarioLogin, Boolean.TRUE).runTest(
                    new BloqueioUsuario(calendar.getTime(), motivoBloqueio),
                    this.servletContext, this.session);

        }
    }

    private void testeBloqueioDesbloqueioSuccess(
            final UsuarioLogin usuarioLogin, final Date dataDesbloqueio,
            final String motivoBloqueio) throws Exception {
        new PersistBloqueioUsuarioTest(usuarioLogin).runTest(
                new BloqueioUsuario(dataDesbloqueio, motivoBloqueio),
                this.servletContext, this.session);
        new PersistDesbloqueioUsuarioTest(usuarioLogin).runTest(
                (BloqueioUsuario) null, this.servletContext, this.session);
        new PersistBloqueioUsuarioTest(usuarioLogin).runTest(
                new BloqueioUsuario(null, motivoBloqueio), this.servletContext,
                this.session);
    }

    private UsuarioLogin createUsuario(final String suffix, final int currentId) {
        final String login = MessageFormat.format("login-usr-{0}-{1}",
                currentId, suffix);
        final String nome = MessageFormat.format("Nome Usuário {0} {1}",
                currentId, suffix);
        final String email = MessageFormat.format("{0}@infox.com.br", login);
        final UsuarioLogin usuarioLogin = new UsuarioLogin(nome, email, login);
        return usuarioLogin;
    }

    private final class PersistDesbloqueioUsuarioTest extends
            RunnableTest<BloqueioUsuario> {
        private UsuarioLogin usuario;
        private final CrudActions<UsuarioLogin> usrCrudActions;

        public PersistDesbloqueioUsuarioTest(final UsuarioLogin usuario) {
            super(BloqueioUsuarioCrudAction.NAME);
            this.usuario = usuario;
            this.usrCrudActions = new CrudActionsImpl<>(
                    UsuarioLoginCrudAction.NAME);
        }

        @Override
        protected void testComponent() throws Exception {
            this.usuario = this.usrCrudActions.resetInstance(this.usuario
                    .getIdUsuarioLogin());
            newInstance();
            setComponentValue("usuarioAtual", this.usuario);

            Assert.assertEquals("usuario bloqueado", Boolean.TRUE,
                    this.usuario.getBloqueio());

            final Object bloquearRet = invokeMethod("desbloquear");
            Assert.assertEquals("persisted", true,
                    AbstractAction.PERSISTED.equals(bloquearRet)
                            || AbstractAction.UPDATED.equals(bloquearRet));

            final Integer id = getId();
            Assert.assertNotNull("id", id);
            newInstance();
            Assert.assertNull("nullId", getId());
            setId(id);
            setEntity(getInstance());

            this.usuario = this.usrCrudActions.resetInstance(this.usuario
                    .getIdUsuarioLogin());
            Assert.assertEquals("usuario desbloqueado", Boolean.FALSE,
                    this.usuario.getBloqueio());
        }
    }

    private final class PersistBloqueioFailTest extends
            RunnableTest<BloqueioUsuario> {
        private UsuarioLogin usuario;
        private final CrudActions<UsuarioLogin> usrCrudActions;
        private final Boolean bloqueadoStartValue;

        public PersistBloqueioFailTest(final UsuarioLogin usuario,
                final Boolean bloqueadoStartValue) {
            super(BloqueioUsuarioCrudAction.NAME);
            this.usuario = usuario;
            this.usrCrudActions = new CrudActionsImpl<>(
                    UsuarioLoginCrudAction.NAME);
            this.bloqueadoStartValue = bloqueadoStartValue;
        }

        public PersistBloqueioFailTest(final UsuarioLogin usuario) {
            super(BloqueioUsuarioCrudAction.NAME);
            this.usuario = usuario;
            this.usrCrudActions = new CrudActionsImpl<>(
                    UsuarioLoginCrudAction.NAME);
            this.bloqueadoStartValue = Boolean.FALSE;
        }

        @Override
        protected void testComponent() throws Exception {
            this.usuario = this.usrCrudActions.resetInstance(this.usuario
                    .getIdUsuarioLogin());
            newInstance();
            setComponentValue("usuarioAtual", this.usuario);
            BloqueioUsuarioCrudActionIT.initEntityAction.setEntity(getEntity());
            BloqueioUsuarioCrudActionIT.initEntityAction.execute(this);

            Assert.assertEquals("usuario não bloqueado",
                    this.bloqueadoStartValue, this.usuario.getBloqueio());
            final Object bloquearRet = invokeMethod("bloquear");
            Assert.assertEquals("persisted", false,
                    AbstractAction.PERSISTED.equals(bloquearRet)
                            || AbstractAction.UPDATED.equals(bloquearRet));

            final Integer id = getId();

            Assert.assertEquals("id", this.bloqueadoStartValue,
                    Boolean.valueOf(id != null));

            setEntity(getInstance());

            this.usuario = this.usrCrudActions.resetInstance(this.usuario
                    .getIdUsuarioLogin());
            Assert.assertEquals("usuário bloqueado", this.bloqueadoStartValue,
                    this.usuario.getBloqueio());
        }
    }

    private final class PersistBloqueioUsuarioTest extends
            RunnableTest<BloqueioUsuario> {
        private UsuarioLogin usuario;
        private final CrudActions<UsuarioLogin> usrCrudActions;

        public PersistBloqueioUsuarioTest(final UsuarioLogin usuario) {
            super(BloqueioUsuarioCrudAction.NAME);
            this.usuario = usuario;
            this.usrCrudActions = new CrudActionsImpl<>(
                    UsuarioLoginCrudAction.NAME);
        }

        @Override
        protected void testComponent() throws Exception {
            this.usuario = this.usrCrudActions.resetInstance(this.usuario
                    .getIdUsuarioLogin());
            newInstance();
            setComponentValue("usuarioAtual", this.usuario);
            BloqueioUsuarioCrudActionIT.initEntityAction.setEntity(getEntity());
            BloqueioUsuarioCrudActionIT.initEntityAction.execute(this);
            Assert.assertEquals("usuario não bloqueado", Boolean.FALSE,
                    this.usuario.getBloqueio());
            final Object bloquearRet = invokeMethod("bloquear");
            Assert.assertEquals("persisted", true,
                    AbstractAction.PERSISTED.equals(bloquearRet)
                            || AbstractAction.UPDATED.equals(bloquearRet));
            final Integer id = getId();
            Assert.assertNotNull("id", id);
            newInstance();
            Assert.assertNull("nullId", getId());
            setId(id);
            setEntity(getInstance());

            this.usuario = this.usrCrudActions.resetInstance(this.usuario
                    .getIdUsuarioLogin());
            Assert.assertEquals("usuário bloqueado", Boolean.TRUE,
                    this.usuario.getBloqueio());
        }

    }

    private final class PersistUsuarioTest extends RunnableTest<UsuarioLogin> {

        public PersistUsuarioTest() {
            super(UsuarioLoginCrudAction.NAME);
        }

        private void initEntity(final UsuarioLogin entity) {
            setEntityValue("nomeUsuario", entity.getNomeUsuario());
            setEntityValue("email", entity.getEmail());
            setEntityValue("login", entity.getLogin());
            setEntityValue("tipoUsuario", entity.getTipoUsuario());
            setEntityValue("ativo", entity.getAtivo());
            setEntityValue("provisorio", entity.getProvisorio());
        }

        @Override
        protected void testComponent() throws Exception {
            newInstance();
            initEntity(getEntity());
            Assert.assertEquals("persisted", AbstractAction.PERSISTED, save());

            final Integer id = getId();
            Assert.assertNotNull("id", id);
            newInstance();
            Assert.assertNull("nullId", getId());
            setId(id);
            setEntity(getInstance());
        }
    }

}
