package br.com.infox.epp.test.access.crud;

import static br.com.infox.epp.access.crud.UsuarioLoginCrudAction.NAME;

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

    @Override
    protected void initEntity(final UsuarioLogin entity) {
        setEntityValue("nomeUsuario", entity.getNomeUsuario());
        setEntityValue("email", entity.getEmail());
        setEntityValue("login", entity.getLogin());
        setEntityValue("tipoUsuario", entity.getTipoUsuario());
        setEntityValue("ativo", entity.getAtivo());
        setEntityValue("provisorio", entity.getProvisorio());
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
    protected List<UsuarioLogin> getInactivateSuccessList() {
        final ArrayList<UsuarioLogin> list = new ArrayList<UsuarioLogin>();
        for (int i = 0; i < 32; i++) {
            list.add(createUsuarioPersistValues("Usuario Login Inactive", "usr-login-inac"+i+"@infox.com.br", "usr-login-inac"+i, UsuarioEnum.H, Boolean.TRUE));
        }
        return list;
    }

    @Override
    protected List<EntityActionContainer<UsuarioLogin>> getUpdateSuccessList() {
        final ArrayList<EntityActionContainer<UsuarioLogin>> list = new ArrayList<EntityActionContainer<UsuarioLogin>>();
        for (int i = 0; i < 30; i++) {
            list.add(new EntityActionContainer<UsuarioLogin>(createUsuarioPersistValues("Usuario Login Update"+i, MessageFormat.format("usr-login-upd{0}@infox.com.br", i), MessageFormat.format("usr-login-upd{0}", i))) {
                @Override
                public void execute() {
                    final String updatedNomeUsuario = getEntity().getNomeUsuario()+"(updated)";
                    setEntityValue("nomeUsuario", updatedNomeUsuario);
                }
            });
        }
        return list;
    }

    @Override
    protected List<EntityActionContainer<UsuarioLogin>> getUpdateFailList() {
        final ArrayList<EntityActionContainer<UsuarioLogin>> list = new ArrayList<EntityActionContainer<UsuarioLogin>>();
        for (int i = 0; i < 30; i++) {
            list.add(new EntityActionContainer<UsuarioLogin>(createUsuarioPersistValues("Usuario Login Update Fail "+i, MessageFormat.format("usr-login-upd-f{0}@infox.com.br", i), MessageFormat.format("usr-login-upd{0}-f", i))) {
                @Override
                public void execute() {
                  final String updatedNomeUsuario = fillStr(getEntity().getNomeUsuario()+"(updated)", LengthConstants.NOME_ATRIBUTO+1);
                  setEntityValue("nomeUsuario", updatedNomeUsuario);
                }
            });
        }
        return list;
    }

    private boolean areEquals(final Object obj1, final Object obj2) {
        return (obj1==obj2) || (obj1 != null && obj1.equals(obj2)) ;
    }
    
    @Override
    protected boolean compareEntityValues(final UsuarioLogin entity) {
        return areEquals(getEntityValue("nomeUsuario"), entity.getNomeUsuario())
                    && areEquals(getEntityValue("email"), entity.getEmail())
                    && areEquals(getEntityValue("login"), entity.getLogin())
                    && areEquals(getEntityValue("tipoUsuario"), entity.getTipoUsuario())
                    && areEquals(getEntityValue("ativo"), entity.getAtivo())
                    && areEquals(getEntityValue("provisorio"), entity.getProvisorio());
    }
}
