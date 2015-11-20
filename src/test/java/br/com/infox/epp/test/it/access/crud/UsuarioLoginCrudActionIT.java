package br.com.infox.epp.test.it.access.crud;

import java.text.MessageFormat;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.access.crud.UsuarioLoginCrudAction;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.access.type.UsuarioEnum;
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
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaDAO;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;
import br.com.infox.log.LogProvider;
import br.com.infox.seam.exception.BusinessException;

//@RunWith(Arquillian.class)
public class UsuarioLoginCrudActionIT extends AbstractCrudTest<UsuarioLogin> {

    @Deployment
    @OverProtocol(AbstractCrudTest.SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(
                UsuarioLoginCrudAction.class, PasswordService.class,
                AccessMailService.class, UsuarioLoginManager.class,
                BusinessException.class, UsuarioLoginDAO.class,
                ModeloDocumentoManager.class, EMailData.class,
                UsuarioLoginDAO.class, ModeloDocumentoDAO.class,
                VariavelDAO.class, LogProvider.class, ParametroManager.class,
                ParametroDAO.class, SendmailCommand.class,
                DominioVariavelTarefaManager.class,
                DominioVariavelTarefaDAO.class, DocumentoManager.class,
                DocumentoDAO.class,
                SigiloDocumentoService.class, SigiloDocumentoManager.class,
                SigiloDocumentoDAO.class,
                SigiloDocumentoPermissaoManager.class,
                SigiloDocumentoPermissaoDAO.class, DocumentoBinDAO.class,
                DocumentoBinManager.class).createDeployment();
    }

    @Override
    protected String getComponentName() {
        return UsuarioLoginCrudAction.NAME;
    }

    public static final ActionContainer<UsuarioLogin> initEntityAction = new ActionContainer<UsuarioLogin>() {
        @Override
        public void execute(final CrudActions<UsuarioLogin> crudActions) {
            final UsuarioLogin entity = getEntity();
            crudActions.setEntityValue("nomeUsuario", entity.getNomeUsuario());
            crudActions.setEntityValue("email", entity.getEmail());
            crudActions.setEntityValue("login", entity.getLogin());
            crudActions.setEntityValue("tipoUsuario", entity.getTipoUsuario());
            crudActions.setEntityValue("ativo", entity.getAtivo());
            crudActions.setEntityValue("provisorio", entity.getProvisorio());
        }
    };

    @Override
    protected ActionContainer<UsuarioLogin> getInitEntityAction() {
        return UsuarioLoginCrudActionIT.initEntityAction;
    }

    //@Test
    public void getPersistSuccessList() throws Exception {
        for (int i = 0; i < 30; i++) {
            this.persistSuccess.runTest(
                    new UsuarioLogin("Usuario Login Persist" + i, MessageFormat
                            .format("usr-login-pers{0}@infox.com.br", i),
                            MessageFormat.format("usr-login-pers{0}", i)),
                    this.servletContext, this.session);
        }
    }

    //@Test
    public void getPersistFailList() throws Exception {
        this.persistFail.runTest(new UsuarioLogin(null,
                "usr-login-pers-fail1@infox.com.br", "usr-login-pers-fail"),
                this.servletContext, this.session);
        this.persistFail.runTest(
                new UsuarioLogin(fillStr("usr-login-pers-fail2@infox.com.br",
                        LengthConstants.NOME_ATRIBUTO + 1),
                        "usr-login-pers-fail2@infox.com.br",
                        "usr-login-pers-fail2"), this.servletContext,
                this.session);

        this.persistFail.runTest(new UsuarioLogin("Usuario Login", null,
                "usr-login-pers-fail3"), this.servletContext, this.session);
        this.persistFail.runTest(
                new UsuarioLogin("Usuario Login", fillStr(
                        "usr-login-pers-fail4@infox.com.br",
                        LengthConstants.DESCRICAO_PADRAO + 1),
                        "usr-login-pers-fail"), this.servletContext,
                this.session);

        this.persistFail.runTest(new UsuarioLogin("Usuario Login",
                "usr-login-pers-fail5@infox.com.br", null),
                this.servletContext, this.session);
        this.persistFail.runTest(
                new UsuarioLogin("Usuario Login",
                        "usr-login-pers-fail6@infox.com.br", fillStr(
                                "usr-login-pers-fail6",
                                LengthConstants.DESCRICAO_PADRAO + 1)),
                this.servletContext, this.session);
    }

    //@Test
    public void getInactivateSuccessList() throws Exception {
        for (int i = 0; i < 32; i++) {
            this.inactivateSuccess.runTest(new UsuarioLogin(
                    "Usuario Login Inactive", "usr-login-inac" + i
                            + "@infox.com.br", "usr-login-inac" + i,
                    UsuarioEnum.H, Boolean.TRUE), this.servletContext,
                    this.session);
        }
    }

    //@Test
    public void getUpdateSuccessList() throws Exception {
        for (int i = 0; i < 30; i++) {
            this.updateSuccess.runTest(
                    new ActionContainer<UsuarioLogin>(new UsuarioLogin(
                            "Usuario Login Update" + i, MessageFormat.format(
                                    "usr-login-upd{0}@infox.com.br", i),
                            MessageFormat.format("usr-login-upd{0}", i))) {
                        @Override
                        public void execute(
                                final CrudActions<UsuarioLogin> crudActions) {
                            final String updatedNomeUsuario = getEntity()
                                    .getNomeUsuario() + "(updated)";
                            crudActions.setEntityValue("nomeUsuario",
                                    updatedNomeUsuario);
                        }
                    }, this.servletContext, this.session);
        }
    }

    //@Test
    public void getUpdateFailList() throws Exception {
        for (int i = 0; i < 30; i++) {
            this.updateFail.runTest(
                    new ActionContainer<UsuarioLogin>(new UsuarioLogin(
                            "Usuario Login Update Fail " + i, MessageFormat
                                    .format("usr-login-upd-f{0}@infox.com.br",
                                            i), MessageFormat.format(
                                    "usr-login-upd{0}-f", i))) {
                        @Override
                        public void execute(
                                final CrudActions<UsuarioLogin> crudActions) {
                            final String updatedNomeUsuario = fillStr(
                                    getEntity().getNomeUsuario() + "(updated)",
                                    LengthConstants.NOME_ATRIBUTO + 1);
                            crudActions.setEntityValue("nomeUsuario",
                                    updatedNomeUsuario);
                        }
                    }, this.servletContext, this.session);
        }
    }

    @Override
    protected boolean compareEntityValues(final UsuarioLogin entity,
            final CrudActions<UsuarioLogin> crudActions) {
        return compareValues(crudActions.getEntityValue("nomeUsuario"),
                entity.getNomeUsuario())
                && compareValues(crudActions.getEntityValue("email"),
                        entity.getEmail())
                && compareValues(crudActions.getEntityValue("login"),
                        entity.getLogin())
                && compareValues(crudActions.getEntityValue("tipoUsuario"),
                        entity.getTipoUsuario())
                && compareValues(crudActions.getEntityValue("ativo"),
                        entity.getAtivo())
                && compareValues(crudActions.getEntityValue("provisorio"),
                        entity.getProvisorio());
    }
}
