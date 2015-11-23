package br.com.infox.epp.test.it.access.crud;

import java.text.MessageFormat;
import java.util.GregorianCalendar;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.access.crud.UsuarioLoginCrudAction;
import br.com.infox.epp.access.crud.UsuarioPessoaFisicaCrudAction;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.documento.dao.ModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.VariavelDAO;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.mail.command.SendmailCommand;
import br.com.infox.epp.mail.entity.EMailData;
import br.com.infox.epp.mail.service.AccessMailService;
import br.com.infox.epp.pessoa.dao.PessoaFisicaDAO;
import br.com.infox.epp.pessoa.dao.PessoaJuridicaDAO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
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
public class UsuarioPessoaFisicaCrudActionIT extends
        AbstractCrudTest<PessoaFisica> {

    @Deployment
    @OverProtocol(AbstractCrudTest.SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(
                UsuarioPessoaFisicaCrudAction.class, PessoaFisicaDAO.class,
                PessoaJuridicaDAO.class, UsuarioLoginCrudAction.class,
                PasswordService.class, AccessMailService.class,
                UsuarioLoginManager.class, BusinessException.class,
                UsuarioLoginDAO.class, ModeloDocumentoManager.class,
                EMailData.class, UsuarioLoginDAO.class,
                ModeloDocumentoDAO.class, VariavelDAO.class, LogProvider.class,
                ParametroManager.class, ParametroDAO.class,
                PessoaFisicaManager.class, UsuarioLoginCrudActionIT.class,
                SendmailCommand.class, DominioVariavelTarefaManager.class,
                DominioVariavelTarefaDAO.class, DocumentoManager.class,
                DocumentoDAO.class,
                SigiloDocumentoService.class, SigiloDocumentoManager.class,
                SigiloDocumentoDAO.class,
                SigiloDocumentoPermissaoManager.class,
                SigiloDocumentoPermissaoDAO.class, DocumentoBinDAO.class,
                DocumentoBinManager.class).createDeployment();
    }

    private final CrudActions<UsuarioLogin> crudActionsUsuarioLogin = new CrudActionsImpl<>(
            UsuarioLoginCrudAction.NAME);

    // TODO:
    // listener="#{usuarioPessoaFisicaCrudAction.searchByCpf(usuarioPessoaFisicaCrudAction.instance.cpf)}"
    public static final ActionContainer<PessoaFisica> initEntityAction = new ActionContainer<PessoaFisica>() {
        @Override
        public void execute(final CrudActions<PessoaFisica> crud) {
            final PessoaFisica entity = getEntity();
            crud.setEntityValue("cpf", entity.getCpf());
            crud.setEntityValue("nome", entity.getNome());
            crud.setEntityValue("dataNascimento", entity.getDataNascimento());
        }
    };

    @Override
    protected ActionContainer<PessoaFisica> getInitEntityAction() {
        return UsuarioPessoaFisicaCrudActionIT.initEntityAction;
    }

    private void initUsuarioLogin(final UsuarioLogin usuario) {
        UsuarioLoginCrudActionIT.initEntityAction.execute(usuario,
                this.crudActionsUsuarioLogin);
    }

    protected Integer persistUsuarioLogin(final UsuarioLogin entity) {
        this.crudActionsUsuarioLogin.newInstance();
        initUsuarioLogin(entity);
        this.crudActionsUsuarioLogin.save();
        return this.crudActionsUsuarioLogin.getId();
    }

    @Override
    protected String getComponentName() {
        return UsuarioPessoaFisicaCrudAction.NAME;
    }

    private static int usr_id = 0;

    private final RunnableTest<PessoaFisica> persistSuccess = new RunnableTest<PessoaFisica>(
            UsuarioPessoaFisicaCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final PessoaFisica entity = getEntity();
            final UsuarioLogin user = createUser(entity);

            this.newInstance();
            UsuarioPessoaFisicaCrudActionIT.initEntityAction.execute(entity,
                    this);
            this.setComponentValue("usuarioAssociado", user);
            final String persistResult = this.save();
            Assert.assertEquals(AbstractAction.PERSISTED, persistResult);

            final Integer id = this.getId();
            Assert.assertNotNull(id);
            this.newInstance();
            final Integer nullId = this.getId();
            Assert.assertNull(nullId);
            this.setId(id);
            assert compareEntityValues(entity, this);

            assert user.getPessoaFisica() != null;
            assert user.getPessoaFisica().equals(this.getInstance());
        }
    };

    private UsuarioLogin createUser(final PessoaFisica entity) {
        this.crudActionsUsuarioLogin.newInstance();
        UsuarioPessoaFisicaCrudActionIT.usr_id++;
        final String login = MessageFormat.format("login-{0}",
                UsuarioPessoaFisicaCrudActionIT.usr_id);
        initUsuarioLogin(new UsuarioLogin(entity.getNome(),
                MessageFormat.format("{0}@infox.com.br", login), login));
        this.crudActionsUsuarioLogin.save();
        final Integer id = this.crudActionsUsuarioLogin.getId();
        this.crudActionsUsuarioLogin.newInstance();
        this.crudActionsUsuarioLogin.setId(id);
        return this.crudActionsUsuarioLogin.getInstance();
    }

    //@Test
    public void persistSuccessTest() throws Exception {
        this.persistSuccess.runTest(new PessoaFisica("", "",
                new GregorianCalendar(1960, 11, 10).getTime(), Boolean.TRUE),
                this.servletContext, this.session);
    }

    private final RunnableTest<PessoaFisica> removeSuccess = new RunnableTest<PessoaFisica>(
            UsuarioPessoaFisicaCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final PessoaFisica entity = getEntity();
            UsuarioLogin user = createUser(entity);

            this.setComponentValue("usuarioAssociado", user);

            this.newInstance();
            UsuarioPessoaFisicaCrudActionIT.initEntityAction.execute(entity,
                    this);
            assert AbstractAction.PERSISTED.equals(this.save());
            assert this.getId() != null;

            UsuarioPessoaFisicaCrudActionIT.this.crudActionsUsuarioLogin
                    .newInstance();
            UsuarioPessoaFisicaCrudActionIT.this.crudActionsUsuarioLogin
                    .setId(user.getIdUsuarioLogin());
            user = UsuarioPessoaFisicaCrudActionIT.this.crudActionsUsuarioLogin
                    .getInstance();
            assert user.getPessoaFisica() != null;
            assert AbstractAction.REMOVED.equals(this.remove(user
                    .getPessoaFisica()));

            UsuarioPessoaFisicaCrudActionIT.this.crudActionsUsuarioLogin
                    .newInstance();
            UsuarioPessoaFisicaCrudActionIT.this.crudActionsUsuarioLogin
                    .setId(user.getIdUsuarioLogin());
            user = UsuarioPessoaFisicaCrudActionIT.this.crudActionsUsuarioLogin
                    .getInstance();
            assert user.getPessoaFisica() == null;
        }
    };

    //@Test
    public void removeSuccessTest() throws Exception {
        this.removeSuccess.runTest(new PessoaFisica("111111111", "",
                new GregorianCalendar(1955, 11, 9).getTime(), Boolean.TRUE),
                this.servletContext, this.session);
        this.removeSuccess.runTest(new PessoaFisica("324789655", "Pessoa",
                new GregorianCalendar(1955, 11, 9).getTime(), Boolean.TRUE),
                this.servletContext, this.session);
        this.removeSuccess.runTest(new PessoaFisica("123332123", "Pessoa",
                new GregorianCalendar(1955, 11, 9).getTime(), Boolean.TRUE),
                this.servletContext, this.session);
    }

}
