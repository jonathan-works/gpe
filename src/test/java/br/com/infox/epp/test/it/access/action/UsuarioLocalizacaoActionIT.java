package br.com.infox.epp.test.it.access.action;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.REMOVED;
import static br.com.infox.core.action.AbstractAction.UPDATED;
import static java.text.MessageFormat.format;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.log.LogProvider;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import br.com.infox.core.exception.BusinessException;
import br.com.infox.epp.access.action.UsuarioLocalizacaoAction;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.component.tree.LocalizacaoEstruturaTreeHandler;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.crud.UsuarioLoginCrudAction;
import br.com.infox.epp.access.dao.UsuarioLocalizacaoDAO;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLocalizacaoManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.documento.dao.ModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.VariavelDAO;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.mail.entity.EMailData;
import br.com.infox.epp.mail.service.AccessMailService;
import br.com.infox.epp.system.dao.ParametroDAO;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.test.crud.AbstractGenericCrudTest;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class UsuarioLocalizacaoActionIT  extends AbstractGenericCrudTest<UsuarioLocalizacao> {

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
            .addClasses(UsuarioLocalizacaoAction.class, UsuarioLocalizacaoManager.class,
                    LocalizacaoEstruturaTreeHandler.class,PapelTreeHandler.class,
                    UsuarioLocalizacaoDAO.class, Authenticator.class,
                    UsuarioLoginCrudAction.class,PasswordService.class,AccessMailService.class,
                    UsuarioLoginManager.class,BusinessException.class,
                    ModeloDocumentoManager.class,EMailData.class,UsuarioLoginDAO.class,
                    ModeloDocumentoDAO.class,VariavelDAO.class,LogProvider.class,
                    ParametroManager.class,ParametroDAO.class)
            .createDeployment();
    }
    
    private CrudActions<UsuarioLogin> crudActionsUsuario = new CrudActions<>(UsuarioLoginCrudAction.NAME);
    private static int id=0;
    
    private UsuarioLogin createUsuarioLogin(final UsuarioLocalizacao entity) {
        final String login = format("login-no",++id);
        final UsuarioLogin usuario = new UsuarioLogin("nome",format("{0}@infox.com.br",login),login);
        
        crudActionsUsuario.newInstance();
        crudActionsUsuario.setEntityValue("nomeUsuario", usuario.getNomeUsuario());
        crudActionsUsuario.setEntityValue("email", usuario.getEmail());
        crudActionsUsuario.setEntityValue("login", usuario.getLogin());
        crudActionsUsuario.setEntityValue("tipoUsuario", usuario.getTipoUsuario());
        crudActionsUsuario.setEntityValue("ativo", usuario.getAtivo());
        crudActionsUsuario.setEntityValue("provisorio", usuario.getProvisorio());
        assert PERSISTED.equals(crudActionsUsuario.save());
        final Integer usrId = crudActionsUsuario.getId();
        assert usrId != null;
        crudActionsUsuario.newInstance();
        crudActionsUsuario.setId(usrId);
        
        return crudActionsUsuario.getInstance();
    }
    
    @Override
    protected void initEntity(final UsuarioLocalizacao entity) {
        String t = REMOVED+UPDATED;
        final UsuarioLogin usuario = createUsuarioLogin(entity);
        getCrudActions().setComponentValue("usuarioGerenciado", usuario);
        
        
    }
    
    @Override
    protected String getComponentName() {
        return UsuarioLocalizacaoAction.NAME;
    }
    
}
