package br.com.infox.epp.test.access.crud;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.log.LogProvider;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import br.com.infox.core.action.AbstractAction;
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
public class UsuarioLoginCrudActionTest extends AbstractGenericCrudTest<UsuarioLogin> {
    private static final String COMPONENT_NAME = UsuarioLoginCrudAction.NAME;
    private static final String OVER_PROTOCOL = "Servlet 3.0";

    @Deployment
    @OverProtocol(OVER_PROTOCOL)
    public static WebArchive createDeployment() {
        final String[] importPackages = {"br.com.infox.core","br.com.itx"};
        final Class<?>[] classesToImport = {
            UsuarioLoginCrudAction.class,PasswordService.class,AccessMailService.class,
            UsuarioLoginManager.class,BusinessException.class,UsuarioLoginDAO.class,
            ModeloDocumentoManager.class,EMailData.class,UsuarioLoginDAO.class,
            ModeloDocumentoDAO.class,VariavelDAO.class,LogProvider.class,
            ParametroManager.class,ParametroDAO.class
        };
        final String archiveName = "epp-test.war";
        final String mockWebXMLPath = "src/test/resources/mock-web.xml";
        final String mockComponentsXMLPath = "src/test/resources/mock-components.xml";
        final String mockPersistenceXMLPath = "src/test/resources/mock-persistence.xml";
        final String pomPath = "pom.xml";
        final ArquillianSeamTestSetup arquillianTest = new ArquillianSeamTestSetup()
            .addPackages(importPackages)
            .addClasses(classesToImport)
            .setArchiveName(archiveName)
            .setMockWebXMLPath(mockWebXMLPath)
            .setMockComponentsXMLPath(mockComponentsXMLPath)
            .setMockPersistenceXMLPath(mockPersistenceXMLPath)
            .setPomPath(pomPath)
        ;
        WebArchive deployment = arquillianTest.createDeployment();
        //deployment.writeTo(System.out, Formatters.VERBOSE);
        return deployment;
    }
    
    @Override
    protected List<UsuarioLogin> getPersistSuccessList() {
        final ArrayList<UsuarioLogin> list = new ArrayList<>();
        
        UsuarioLogin usuarioLogin = createUsuarioPersistValues("Erik Liberal", "erikliberal@infox.com.br", "erikliberal", UsuarioEnum.H, Boolean.TRUE);
        list.add(usuarioLogin);
        for (int i = 0; i < 30; i++) {
            usuarioLogin = createUsuarioPersistValues(fillStr(""+i, 12), MessageFormat.format("a{0}@infox.com.br", fillStr(""+i, 12)), MessageFormat.format("usuario{0}", fillStr(""+i, 3)));
        }
        
        return list;
    }

    private UsuarioLogin createUsuarioPersistValues(final String nomeUsuario,
            final String email, final String login) {
        final UsuarioLogin usuario = new UsuarioLogin();
        usuario.setNomeUsuario(nomeUsuario);
        usuario.setEmail(email);
        usuario.setLogin(login);
        usuario.setTipoUsuario(UsuarioEnum.H);
        usuario.setAtivo(Boolean.TRUE);
        usuario.setProvisorio(Boolean.FALSE);
        return usuario;
    }
    
    private UsuarioLogin createUsuarioPersistValues(final String nomeUsuario,
            final String email, final String login,
            final UsuarioEnum tipoUsuario, final Boolean ativo) {
        final UsuarioLogin usuario = new UsuarioLogin();
        usuario.setNomeUsuario(nomeUsuario);
        usuario.setEmail(email);
        usuario.setLogin(login);
        usuario.setTipoUsuario(tipoUsuario);
        usuario.setAtivo(ativo);
        usuario.setProvisorio(Boolean.FALSE);
        return usuario;
    }

    private void setPersistData(final UsuarioLogin entity) {
        setValue(COMPONENT_NAME, "nomeUsuario", entity.getNomeUsuario());
        setValue(COMPONENT_NAME, "email", entity.getEmail());
        setValue(COMPONENT_NAME, "login", entity.getLogin());
        setValue(COMPONENT_NAME, "tipoUsuario", entity.getTipoUsuario());
        setValue(COMPONENT_NAME, "ativo", entity.getAtivo());
        setValue(COMPONENT_NAME, "provisorio", entity.getProvisorio());
    }

    @Override
    protected Runnable getPersistSuccessTest(final UsuarioLogin entity) {
        return new Runnable() {
            @Override
            public void run() {
                invokeMethod(COMPONENT_NAME, "newInstance");
                setPersistData(entity);
                final Object invokeMethodResult = invokeMethod(COMPONENT_NAME, "save");
                System.out.println("===FLAG "+invokeMethodResult);
                Assert.assertEquals(AbstractAction.PERSISTED, invokeMethodResult);
                
                Object id = getComponentValue(COMPONENT_NAME, "id");
                Assert.assertNotNull(id);
                System.out.println("===FLAG "+id);
//                invokeMethod(COMPONENT_NAME, "newInstance");
//
//                setComponentValue(COMPONENT_NAME, "id", id);
//                assert getValue(COMPONENT_NAME, "nomeUsuario").equals(entity.getNomeUsuario());
//                assert getValue(COMPONENT_NAME, "email").equals(entity.getEmail());
//                assert getValue(COMPONENT_NAME, "login").equals(entity.getLogin());
//                assert getValue(COMPONENT_NAME, "tipoUsuario").equals(entity.getTipoUsuario());
//                assert getValue(COMPONENT_NAME, "ativo").equals(entity.getAtivo());
//                assert getValue(COMPONENT_NAME, "provisorio").equals(entity.getProvisorio());
            }
        };
    }

    @Override
    protected List<UsuarioLogin> getPersistFailList() {
        // TODO Auto-generated method stub
        return new ArrayList<UsuarioLogin>();
    }

    @Override
    protected Runnable getPersistFailTest(UsuarioLogin entity) {
        return new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                
            }
        };
    }

    @Override
    protected List<UsuarioLogin> getInactivateSuccessList() {
        // TODO Auto-generated method stub
        return new ArrayList<UsuarioLogin>();
    }

    @Override
    protected Runnable getInactivateSuccessTest(UsuarioLogin entity) {
        return new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                
            }
        };
    }

    @Override
    protected List<UsuarioLogin> getInactivateFailList() {
        // TODO Auto-generated method stub
        return new ArrayList<UsuarioLogin>();
    }

    @Override
    protected Runnable getInactivateFailTest(UsuarioLogin entity) {
        return new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                
            }
        };
    }

    @Override
    protected List<UsuarioLogin> getUpdateSuccessList() {
        // TODO Auto-generated method stub
        return new ArrayList<UsuarioLogin>();
    }

    @Override
    protected Runnable getUpdateSuccessTest(UsuarioLogin entity) {
        return new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                
            }
        };
    }

    @Override
    protected List<UsuarioLogin> getUpdateFailList() {
        // TODO Auto-generated method stub
        return new ArrayList<UsuarioLogin>();
    }

    @Override
    protected Runnable getUpdateFailTest(UsuarioLogin entity) {
        return new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                
            }
        };
    }

    @Override
    protected List<UsuarioLogin> getRemoveSuccessList() {
        // TODO Auto-generated method stub
        return new ArrayList<UsuarioLogin>();
    }

    @Override
    protected Runnable getRemoveSuccessTest(UsuarioLogin entity) {
        return new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                
            }
        };
    }

    @Override
    protected List<UsuarioLogin> getRemoveFailList() {
        // TODO Auto-generated method stub
        return new ArrayList<UsuarioLogin>();
    }

    @Override
    protected Runnable getRemoveFailTest(UsuarioLogin entity) {
        return new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                
            }
        };
    }
    
}
