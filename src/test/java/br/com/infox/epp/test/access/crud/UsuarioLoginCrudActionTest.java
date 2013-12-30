package br.com.infox.epp.test.access.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.UPDATED;
import static br.com.infox.epp.access.crud.UsuarioLoginCrudAction.NAME;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.log.LogProvider;
import org.jboss.shrinkwrap.api.spec.WebArchive;
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
public class UsuarioLoginCrudActionTest extends AbstractGenericCrudTest<UsuarioLogin> {

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
            .addPackages("br.com.infox.core", "br.com.itx")
            .addClasses(UsuarioLoginCrudAction.class,PasswordService.class,AccessMailService.class,
                UsuarioLoginManager.class,BusinessException.class,UsuarioLoginDAO.class,
                ModeloDocumentoManager.class,EMailData.class,UsuarioLoginDAO.class,
                ModeloDocumentoDAO.class,VariavelDAO.class,LogProvider.class,
                ParametroManager.class,ParametroDAO.class)
            .setArchiveName("epp-test.war")
            .setMockWebXMLPath("src/test/resources/mock-web.xml")
            .setMockComponentsXMLPath("src/test/resources/mock-components.xml")
            .setMockPersistenceXMLPath("src/test/resources/mock-persistence.xml")
            .setPomPath("pom.xml")
            .createDeployment()
        ;
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

    protected void setPersistData(final UsuarioLogin entity) {
        setValue(NAME, "nomeUsuario", entity.getNomeUsuario());
        setValue(NAME, "email", entity.getEmail());
        setValue(NAME, "login", entity.getLogin());
        setValue(NAME, "tipoUsuario", entity.getTipoUsuario());
        setValue(NAME, "ativo", entity.getAtivo());
        setValue(NAME, "provisorio", entity.getProvisorio());
    }
    
    @Override
    protected List<UsuarioLogin> getPersistSuccessList() {
        final ArrayList<UsuarioLogin> list = new ArrayList<>();
        
        for (int i = 0; i < 30; i++) {
            list.add(createUsuarioPersistValues("Usuario Login Persist"+i, MessageFormat.format("usr-login-pers{0}@infox.com.br", i), MessageFormat.format("usr-login-pers{0}", i)));
        }
        
        return list;
    }

    @Override
    protected Runnable getPersistSuccessTest(final UsuarioLogin entity) {
        return new Runnable() {
            
            @Override
            public void run() {
                newInstance();
                setPersistData(entity);
                final Object persistResult = save();
                assertEquals(PERSISTED, persistResult);
                
                Object id = getId();
                assertNotNull(id);
                newInstance();
                Object nullId = getId();
                assertNull(nullId);
                setId(id);
                
                assert compareEntity(entity);
            }
        };
    }

    @Override
    protected List<UsuarioLogin> getPersistFailList() {
        final ArrayList<UsuarioLogin> list = new ArrayList<>();
        list.add(createUsuarioPersistValues(null, "usr-login-pers-fail1@infox.com.br", "usr-login-pers-fail"));
        list.add(createUsuarioPersistValues(fillStr("usr-login-pers-fail2@infox.com.br",LengthConstants.NOME_ATRIBUTO+1), "usr-login-pers-fail2@infox.com.br", "usr-login-pers-fail2"));
        
        list.add(createUsuarioPersistValues("Usuario Login", null, "usr-login-pers-fail3"));
        list.add(createUsuarioPersistValues("Usuario Login", fillStr("usr-login-pers-fail4@infox.com.br",LengthConstants.DESCRICAO_PADRAO+1), "usr-login-pers-fail"));
        
        list.add(createUsuarioPersistValues("Usuario Login", "usr-login-pers-fail5@infox.com.br", null));
        list.add(createUsuarioPersistValues("Usuario Login", "usr-login-pers-fail6@infox.com.br", fillStr("usr-login-pers-fail6",LengthConstants.DESCRICAO_PADRAO+1)));
        return list;
    }

    @Override
    protected Runnable getPersistFailTest(final UsuarioLogin entity) {
        return new Runnable() {
            @Override
            public void run() {
                newInstance();
                setPersistData(entity);
                
                assert !PERSISTED.equals(save());
                Object id = getId();
                assertNull(id);
            }
        };
    }

    @Override
    protected List<UsuarioLogin> getInactivateSuccessList() {
        final ArrayList<UsuarioLogin> list = new ArrayList<UsuarioLogin>();
        for (int i = 0; i < 32; i++) {
            list.add(createUsuarioPersistValues("Usuario Login Inactive", "usr-login-inac"+i+"@infox.com.br", "usr-login-inac"+i, UsuarioEnum.H, Boolean.TRUE));
        }
        return list;
    }

    @Override
    protected Runnable getInactivateSuccessTest(final UsuarioLogin entity) {
        return new Runnable() {
            @Override
            public void run() {
                newInstance();
                setPersistData(entity);
                assertEquals(PERSISTED, save());
                
                assertNotNull(getId());
                assert getValue(NAME, "ativo").equals(Boolean.TRUE);
                assertEquals(UPDATED, inactive());
                assert getValue(NAME, "ativo").equals(Boolean.FALSE);
            }
        };
    }

    @Override
    protected List<UsuarioLogin> getUpdateSuccessList() {
        final ArrayList<UsuarioLogin> list = new ArrayList<UsuarioLogin>();
        for (int i = 0; i < 30; i++) {
            list.add(createUsuarioPersistValues("Usuario Login Update"+i, MessageFormat.format("usr-login-upd{0}@infox.com.br", i), MessageFormat.format("usr-login-upd{0}", i)));
        }
        return list;
    }

    @Override
    protected Runnable getUpdateSuccessTest(final UsuarioLogin entity) {
        return new Runnable() {
            @Override
            public void run() {
                newInstance();
                setPersistData(entity);
                
                assert PERSISTED.equals(save());
                
                final Object id = getId();
                assert id != null;
                newInstance();
                assert getId() == null;
                setId(id);
                
                assert compareEntity(entity);
                final String updatedNomeUsuario = entity.getNomeUsuario()+"(updated)";
                setValue(NAME, "nomeUsuario", updatedNomeUsuario);
//                setValue(NAME, "email", "");
//                setValue(NAME, "login", "");
//                setValue(NAME, "tipoUsuario", "");
//                setValue(NAME, "ativo", "");
//                setValue(NAME, "provisorio", "");
                assert UPDATED.equals(save());
                assert updatedNomeUsuario.equals(getValue(NAME,"nomeUsuario"));
            }
        };
    }

    @Override
    protected List<UsuarioLogin> getUpdateFailList() {
        final ArrayList<UsuarioLogin> list = new ArrayList<UsuarioLogin>();
        for (int i = 0; i < 30; i++) {
            list.add(createUsuarioPersistValues("Usuario Login Update Fail "+i, MessageFormat.format("usr-login-upd-f{0}@infox.com.br", i), MessageFormat.format("usr-login-upd{0}-f", i)));
        }
        return list;
    }

    @Override
    protected Runnable getUpdateFailTest(final UsuarioLogin entity) {
        return new Runnable() {
            @Override
            public void run() {
                newInstance();
                setPersistData(entity);
                
                assert PERSISTED.equals(save());
                
                final Object id = getId();
                assert id != null;
                newInstance();
                assert getId() == null;
                setId(id);
                
                assert compareEntity(entity);
                
                final String updatedNomeUsuario = fillStr(entity.getNomeUsuario()+"(updated)", LengthConstants.NOME_ATRIBUTO+1);
                setValue(NAME, "nomeUsuario", updatedNomeUsuario);
//                setValue(NAME, "email", "");
//                setValue(NAME, "login", "");
//                setValue(NAME, "tipoUsuario", "");
//                setValue(NAME, "ativo", "");
//                setValue(NAME, "provisorio", "");
                assert !UPDATED.equals(save());
                newInstance();
                assert getId() == null;
                setId(id);
                assert entity.getNomeUsuario().equals(getValue(NAME,"nomeUsuario"));
            }
        };
    }

    private boolean compareEntity(final UsuarioLogin entity) {
        return getValue(NAME, "nomeUsuario").equals(entity.getNomeUsuario()) 
                && getValue(NAME, "email").equals(entity.getEmail())
                && getValue(NAME, "login").equals(entity.getLogin())
                && getValue(NAME, "tipoUsuario").equals(entity.getTipoUsuario())
                && getValue(NAME, "ativo").equals(entity.getAtivo())
                && getValue(NAME, "provisorio").equals(entity.getProvisorio());
    }

    private void setId(Object id) {
        setComponentValue(NAME, "id", id);
    }

    private Object getId() {
        return getComponentValue(NAME, "id");
    }

    private Object save() {
        return invokeMethod(NAME, "save");
    }
    
    private Object inactive() {
        return invokeMethod(NAME, "inactive("+NAME+".instance)");
    }

    private void newInstance() {
        invokeMethod(NAME, "newInstance");
    }
}
