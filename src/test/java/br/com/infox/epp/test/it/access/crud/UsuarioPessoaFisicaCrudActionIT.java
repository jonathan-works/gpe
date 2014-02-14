package br.com.infox.epp.test.it.access.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.REMOVED;
import static java.text.MessageFormat.format;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.GregorianCalendar;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.log.LogProvider;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.core.exception.BusinessException;
import br.com.infox.epp.access.crud.UsuarioLoginCrudAction;
import br.com.infox.epp.access.crud.UsuarioPessoaFisicaCrudAction;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.documento.dao.ModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.VariavelDAO;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.mail.entity.EMailData;
import br.com.infox.epp.mail.service.AccessMailService;
import br.com.infox.epp.pessoa.dao.PessoaDAO;
import br.com.infox.epp.pessoa.dao.PessoaFisicaDAO;
import br.com.infox.epp.pessoa.dao.PessoaJuridicaDAO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.system.dao.ParametroDAO;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class UsuarioPessoaFisicaCrudActionIT extends AbstractCrudTest<PessoaFisica>{

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
        .addClasses(UsuarioPessoaFisicaCrudAction.class,
                PessoaFisicaDAO.class,PessoaJuridicaDAO.class,
                UsuarioLoginCrudAction.class,PasswordService.class,AccessMailService.class,
                UsuarioLoginManager.class,BusinessException.class,UsuarioLoginDAO.class,
                ModeloDocumentoManager.class,EMailData.class,UsuarioLoginDAO.class,
                ModeloDocumentoDAO.class,VariavelDAO.class,LogProvider.class,
                ParametroManager.class,ParametroDAO.class, PessoaDAO.class,PessoaFisicaManager.class)
        .createDeployment();
    }

    private final CrudActions<UsuarioLogin> crudActionsUsuarioLogin = new CrudActionsImpl<>(UsuarioLoginCrudAction.NAME);
    
    //TODO: listener="#{usuarioPessoaFisicaCrudAction.searchByCpf(usuarioPessoaFisicaCrudAction.instance.cpf)}"
    @Override
    protected void initEntity(final PessoaFisica entity, final CrudActions<PessoaFisica> crudActions) {
        crudActions.setEntityValue("cpf",entity.getCpf());
        crudActions.setEntityValue("nome",entity.getNome());
        crudActions.setEntityValue("dataNascimento",entity.getDataNascimento());
    }

    private void initUsuarioLogin(final UsuarioLogin usuario) {
        crudActionsUsuarioLogin.setEntityValue("nomeUsuario", usuario.getNomeUsuario());
        crudActionsUsuarioLogin.setEntityValue("email", usuario.getEmail());
        crudActionsUsuarioLogin.setEntityValue("login", usuario.getLogin());
        crudActionsUsuarioLogin.setEntityValue("tipoUsuario", usuario.getTipoUsuario());
        crudActionsUsuarioLogin.setEntityValue("ativo", usuario.getAtivo());
        crudActionsUsuarioLogin.setEntityValue("provisorio", usuario.getProvisorio());
    }
    
    protected Integer persistUsuarioLogin(final UsuarioLogin entity) {
        crudActionsUsuarioLogin.newInstance();
        initUsuarioLogin(entity);
        crudActionsUsuarioLogin.save();
        return (Integer) crudActionsUsuarioLogin.getId();
    }

    @Override
    protected String getComponentName() {
        return UsuarioPessoaFisicaCrudAction.NAME;
    }
    
    private static int usr_id=0;
    
    private final InternalRunnableTest<PessoaFisica> persistSuccess = new InternalRunnableTest<PessoaFisica>() {
        @Override
        protected void testComponent() throws Exception {
            final PessoaFisica entity = getEntity();
            final UsuarioLogin user = createUser(entity);

            crudActions.newInstance();
            initEntity(entity, this.crudActions);
            crudActions.setComponentValue("usuarioAssociado", user);
            final String persistResult = crudActions.save();
            assertEquals(PERSISTED, persistResult);

            final Integer id = crudActions.getId();
            assertNotNull(id);
            crudActions.newInstance();
            final Integer nullId = crudActions.getId();
            assertNull(nullId);
            crudActions.setId(id);
            assert compareEntityValues(entity, this.crudActions);

            assert user.getPessoaFisica() != null;
            assert user.getPessoaFisica().equals(crudActions.getInstance());
        }
    };

    private UsuarioLogin createUser(final PessoaFisica entity) {
        crudActionsUsuarioLogin.newInstance();
        usr_id++;
        final String login = format("login-{0}", usr_id);
        initUsuarioLogin(new UsuarioLogin(entity.getNome(), format("{0}@infox.com.br", login), login));
        crudActionsUsuarioLogin.save();
        final Integer id = crudActionsUsuarioLogin.getId();
        crudActionsUsuarioLogin.newInstance();
        crudActionsUsuarioLogin.setId(id);
        return crudActionsUsuarioLogin.getInstance();
    }
    
    @Test
    public void persistSuccessTest() throws Exception {
        persistSuccess.runTest(new PessoaFisica("", "", new GregorianCalendar(1960,11,10).getTime(), Boolean.TRUE));
    }
    
    private final InternalRunnableTest<PessoaFisica> removeSuccess = new InternalRunnableTest<PessoaFisica>() {
        @Override
        protected void testComponent() throws Exception {
            final PessoaFisica entity = getEntity();
            UsuarioLogin user = createUser(entity);
            
            crudActions.setComponentValue("usuarioAssociado", user);
            
            crudActions.newInstance();
            initEntity(entity, crudActions);
            assert PERSISTED.equals(crudActions.save());
            assert crudActions.getId() != null;
            
            crudActionsUsuarioLogin.newInstance();
            crudActionsUsuarioLogin.setId(user.getIdUsuarioLogin());
            user = (UsuarioLogin) crudActionsUsuarioLogin.getInstance();
            assert user.getPessoaFisica() != null;
            assert REMOVED.equals(crudActions.remove(user.getPessoaFisica()));
            
            crudActionsUsuarioLogin.newInstance();
            crudActionsUsuarioLogin.setId(user.getIdUsuarioLogin());
            user = (UsuarioLogin) crudActionsUsuarioLogin.getInstance();
            assert user.getPessoaFisica() == null;
        }
    };
    
    @Test
    public void removeSuccessTest() throws Exception {
        removeSuccess.runTest(new PessoaFisica("111111111","",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        removeSuccess.runTest(new PessoaFisica("324789655","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
        removeSuccess.runTest(new PessoaFisica("123332123","Pessoa",new GregorianCalendar(1955,11,9).getTime(),Boolean.TRUE));
    }
    
}
