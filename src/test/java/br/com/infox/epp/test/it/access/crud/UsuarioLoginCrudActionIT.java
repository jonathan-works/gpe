package br.com.infox.epp.test.it.access.crud;

import static br.com.infox.epp.access.crud.UsuarioLoginCrudAction.NAME;

import java.text.MessageFormat;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.log.LogProvider;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.core.exception.BusinessException;
import br.com.infox.epp.access.crud.UsuarioLoginCrudAction;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.access.type.UsuarioEnum;
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
public class UsuarioLoginCrudActionIT extends AbstractGenericCrudTest<UsuarioLogin> {

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
            .addClasses(UsuarioLoginCrudAction.class,PasswordService.class,AccessMailService.class,
                UsuarioLoginManager.class,BusinessException.class,UsuarioLoginDAO.class,
                ModeloDocumentoManager.class,EMailData.class,UsuarioLoginDAO.class,
                ModeloDocumentoDAO.class,VariavelDAO.class,LogProvider.class,
                ParametroManager.class,ParametroDAO.class)
            .createDeployment()
        ;
    }
    
    @Override
    protected String getComponentName() {
        return NAME;
    }
    
    @Override
    protected void initEntity(final UsuarioLogin entity) {
        final CrudActions<UsuarioLogin> crudActions = getCrudActions();
        crudActions.setEntityValue("nomeUsuario", entity.getNomeUsuario());
        crudActions.setEntityValue("email", entity.getEmail());
        crudActions.setEntityValue("login", entity.getLogin());
        crudActions.setEntityValue("tipoUsuario", entity.getTipoUsuario());
        crudActions.setEntityValue("ativo", entity.getAtivo());
        crudActions.setEntityValue("provisorio", entity.getProvisorio());
    }
    
    @Test
    public void getPersistSuccessList() throws Exception {
        for (int i = 0; i < 30; i++) {
            persistSuccess.runTest(new UsuarioLogin("Usuario Login Persist"+i, MessageFormat.format("usr-login-pers{0}@infox.com.br", i), MessageFormat.format("usr-login-pers{0}", i)));
        }
    }

    @Test
    public void getPersistFailList() throws Exception {
        persistFail.runTest(new UsuarioLogin(null, "usr-login-pers-fail1@infox.com.br", "usr-login-pers-fail"));
        persistFail.runTest(new UsuarioLogin(fillStr("usr-login-pers-fail2@infox.com.br",LengthConstants.NOME_ATRIBUTO+1), "usr-login-pers-fail2@infox.com.br", "usr-login-pers-fail2"));
        
        persistFail.runTest(new UsuarioLogin("Usuario Login", null, "usr-login-pers-fail3"));
        persistFail.runTest(new UsuarioLogin("Usuario Login", fillStr("usr-login-pers-fail4@infox.com.br",LengthConstants.DESCRICAO_PADRAO+1), "usr-login-pers-fail"));
        
        persistFail.runTest(new UsuarioLogin("Usuario Login", "usr-login-pers-fail5@infox.com.br", null));
        persistFail.runTest(new UsuarioLogin("Usuario Login", "usr-login-pers-fail6@infox.com.br", fillStr("usr-login-pers-fail6",LengthConstants.DESCRICAO_PADRAO+1)));
    }

    @Test
    public void getInactivateSuccessList() throws Exception {
        for (int i = 0; i < 32; i++) {
            inactivateSuccess.runTest(new UsuarioLogin("Usuario Login Inactive", "usr-login-inac"+i+"@infox.com.br", "usr-login-inac"+i, UsuarioEnum.H, Boolean.TRUE));
        }
    }

    @Test
    public void getUpdateSuccessList() throws Exception {
        for (int i = 0; i < 30; i++) {
            updateSuccess.runTest(new EntityActionContainer<UsuarioLogin>(new UsuarioLogin("Usuario Login Update"+i, MessageFormat.format("usr-login-upd{0}@infox.com.br", i), MessageFormat.format("usr-login-upd{0}", i))) {
                @Override
                public void execute() {
                    final String updatedNomeUsuario = getEntity().getNomeUsuario()+"(updated)";
                    getCrudActions().setEntityValue("nomeUsuario", updatedNomeUsuario);
                }
            });
        }
    }

    @Test
    public void getUpdateFailList() throws Exception {
        for (int i = 0; i < 30; i++) {
            updateFail.runTest(new EntityActionContainer<UsuarioLogin>(new UsuarioLogin("Usuario Login Update Fail "+i, MessageFormat.format("usr-login-upd-f{0}@infox.com.br", i), MessageFormat.format("usr-login-upd{0}-f", i))) {
                @Override
                public void execute() {
                  final String updatedNomeUsuario = fillStr(getEntity().getNomeUsuario()+"(updated)", LengthConstants.NOME_ATRIBUTO+1);
                  getCrudActions().setEntityValue("nomeUsuario", updatedNomeUsuario);
                }
            });
        }
    }
    
    @Override
    protected boolean compareEntityValues(final UsuarioLogin entity) {
        final CrudActions<UsuarioLogin> crudActions = getCrudActions();
        return compareValues(crudActions.getEntityValue("nomeUsuario"), entity.getNomeUsuario())
                    && compareValues(crudActions.getEntityValue("email"), entity.getEmail())
                    && compareValues(crudActions.getEntityValue("login"), entity.getLogin())
                    && compareValues(crudActions.getEntityValue("tipoUsuario"), entity.getTipoUsuario())
                    && compareValues(crudActions.getEntityValue("ativo"), entity.getAtivo())
                    && compareValues(crudActions.getEntityValue("provisorio"), entity.getProvisorio());
    }
}
