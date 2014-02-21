package br.com.infox.epp.test.it.access.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.UPDATED;
import static java.text.MessageFormat.format;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.log.LogProvider;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.core.exception.BusinessException;
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
import br.com.infox.epp.mail.entity.EMailData;
import br.com.infox.epp.mail.service.AccessMailService;
import br.com.infox.epp.system.dao.ParametroDAO;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.RunnableTest;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class BloqueioUsuarioCrudActionIT extends AbstractCrudTest<BloqueioUsuario> {

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
            .addClasses(BloqueioUsuarioCrudAction.class, BloqueioUsuarioManager.class,
                    BloqueioUsuarioDAO.class,ParametroManager.class,ParametroDAO.class,
                    UsuarioLoginCrudAction.class,PasswordService.class,AccessMailService.class,
                    UsuarioLoginManager.class,BusinessException.class,UsuarioLoginDAO.class,
                    ModeloDocumentoManager.class,EMailData.class,UsuarioLoginDAO.class,
                    ModeloDocumentoDAO.class,VariavelDAO.class,LogProvider.class)
        .createDeployment();
    }
    
    private final RunnableTest<UsuarioLogin> persistUsuario = new PersistUsuarioTest();    
    /*
    <action execute="#{bloqueioUsuarioCrudAction.setUsuarioAtual(usuarioLoginCrudAction.instance)}" 
            if="#{usuarioLoginCrudAction.tab eq 'historicoBloqueioUsuario'}"/>        
    <action execute="#{bloqueioUsuarioList.entity.setUsuario(usuarioLoginCrudAction.instance)}" 
            if="#{usuarioLoginCrudAction.tab eq 'historicoBloqueioUsuario'}"/>
     * 
     * 
    <wi:dataForm
        formId="bloqueioUsuarioForm"
        formTitle="#{messages['form.title']}"
        home="#{bloqueioUsuarioCrudAction}"
        requiredForm="#{not bloqueioUsuarioCrudAction.usuarioAtual.bloqueio}">
        <wi:outputText
            id="bloqueio"
            value="#{bloqueioUsuarioCrudAction.usuarioAtual.bloqueio ? 'Sim': 'Não'}"
            label="#{messages['usuario.bloqueio']}" />
        
        <ui:define name="buttons">
           <wi:commandButton id="bloqueioButton" rendered="#{not bloqueioUsuarioCrudAction.usuarioAtual.bloqueio}"
               value="#{messages['usuario.bloquear']}" action="bloqueioUsuarioCrudAction.bloquear"
               reRender="historicoBloqueioUsuario, pageBodyDialogMessage"/>
           <wi:commandButton id="desbloqueioButton" rendered="#{bloqueioUsuarioCrudAction.usuarioAtual.bloqueio}"
               value="#{messages['usuario.desbloquear']}" action="bloqueioUsuarioCrudAction.desbloquear"
               reRender="historicoBloqueioUsuario, pageBodyDialogMessage"/>
        </ui:define>
    </wi:dataForm>
*/
    @Override
    protected void initEntity(final BloqueioUsuario entity,
            final CrudActions<BloqueioUsuario> crudActions) {
        //dataPrevisaoDesbloqueio
        crudActions.setEntityValue("dataPrevisaoDesbloqueio", entity.getDataPrevisaoDesbloqueio());
        crudActions.setEntityValue("motivoBloqueio", entity.getMotivoBloqueio());

    }

    @Override
    protected String getComponentName() {
        return BloqueioUsuarioCrudAction.NAME;
    }
    
    private static int id = 0;
    
    @Test
    public void BloqueioUsuarioSuccessTest() throws Exception {
        final String motivoBloqueio = "Motivo de Bloqueio";
        final GregorianCalendar calendar = new GregorianCalendar();
        
        for (int i = 0; i < 50; i++) {
            final UsuarioLogin usuarioLogin = persistUsuario.runTest(createUsuario("per-success", ++id), servletContext, session);
            calendar.add(Calendar.DAY_OF_MONTH, (i+1) * 5);
            testeBloqueioDesbloqueioSuccess(usuarioLogin, calendar.getTime(), motivoBloqueio);   
        }
    }
    
    @Test
    public void BloqueioUsuarioFailTest() throws Exception {
        final String motivoBloqueio = "Motivo de Bloqueio";
        
        //for(int i=0;i<50;i++)
        int i=0;
        {
            final UsuarioLogin usuarioLogin = persistUsuario.runTest(createUsuario("per-fail", ++id), servletContext, session);
            final GregorianCalendar calendar = new GregorianCalendar();
            new PersistBloqueioFailTest(usuarioLogin).runTest(new BloqueioUsuario(null, null), servletContext, session);
            new PersistBloqueioFailTest(usuarioLogin).runTest(new BloqueioUsuario(null, ""), servletContext, session);
            new PersistBloqueioFailTest(usuarioLogin).runTest(new BloqueioUsuario(null, fillStr(motivoBloqueio,LengthConstants.DESCRICAO_ENTIDADE+1)), servletContext, session);
            calendar.add(Calendar.DAY_OF_MONTH, -5);
            new PersistBloqueioFailTest(usuarioLogin).runTest(new BloqueioUsuario(calendar.getTime(), motivoBloqueio), servletContext, session);
            calendar.add(Calendar.DAY_OF_MONTH, (i+2)*5);
            testeBloqueioDesbloqueioSuccess(usuarioLogin, calendar.getTime(), motivoBloqueio);
            
            new PersistBloqueioFailTest(usuarioLogin, Boolean.TRUE).runTest(new BloqueioUsuario(null, null), servletContext, session);
            new PersistBloqueioFailTest(usuarioLogin, Boolean.TRUE).runTest(new BloqueioUsuario(null, ""), servletContext, session);
            new PersistBloqueioFailTest(usuarioLogin, Boolean.TRUE).runTest(new BloqueioUsuario(null, fillStr(motivoBloqueio,LengthConstants.DESCRICAO_ENTIDADE+1)), servletContext, session);
            calendar.add(Calendar.DAY_OF_MONTH, (-i-2)*10);
            new PersistBloqueioFailTest(usuarioLogin, Boolean.TRUE).runTest(new BloqueioUsuario(calendar.getTime(), motivoBloqueio), servletContext, session);
            
        }
    }

    private void testeBloqueioDesbloqueioSuccess(final UsuarioLogin usuarioLogin,
            final Date dataDesbloqueio, final String motivoBloqueio) throws Exception {
        new PersistBloqueioUsuarioTest(usuarioLogin).runTest(new BloqueioUsuario(dataDesbloqueio, motivoBloqueio), servletContext, session);
        new PersistDesbloqueioUsuarioTest(usuarioLogin).runTest((BloqueioUsuario)null, servletContext, session);
        new PersistBloqueioUsuarioTest(usuarioLogin).runTest(new BloqueioUsuario(null,motivoBloqueio), servletContext, session);
    }

    private UsuarioLogin createUsuario(final String suffix, final int currentId) {
        final String login = format("login-usr-{0}-{1}",currentId,suffix);
        final String nome = format("Nome Usuário {0} {1}",currentId,suffix);
        final String email = format("{0}@infox.com.br", login);
        UsuarioLogin usuarioLogin = new UsuarioLogin(nome,email,login);
        return usuarioLogin;
    }
    
    private final class PersistDesbloqueioUsuarioTest extends RunnableTest<BloqueioUsuario> {
        private UsuarioLogin usuario;
        private final CrudActions<UsuarioLogin> usrCrudActions;
        
        public PersistDesbloqueioUsuarioTest(final UsuarioLogin usuario) {
            super(BloqueioUsuarioCrudAction.NAME);
            this.usuario = usuario;
            this.usrCrudActions = new CrudActionsImpl<>(UsuarioLoginCrudAction.NAME);
        }
        
        @Override
        protected void testComponent() throws Exception {
            usuario = this.usrCrudActions.resetInstance(usuario.getIdUsuarioLogin());
            newInstance();
            setComponentValue("usuarioAtual", usuario);
            
            assertEquals("usuario bloqueado", Boolean.TRUE, usuario.getBloqueio());
            

            final Object bloquearRet = invokeMethod("desbloquear");
            assertEquals("persisted", true, PERSISTED.equals(bloquearRet) || UPDATED.equals(bloquearRet));
            
            final Integer id = getId();
            assertNotNull("id", id);
            newInstance();
            assertNull("nullId", getId());
            setId(id);
            setEntity(getInstance());
            
            usuario = this.usrCrudActions.resetInstance(usuario.getIdUsuarioLogin());
            assertEquals("usuario desbloqueado", Boolean.FALSE, usuario.getBloqueio());
        }
    }
    
    private final class PersistBloqueioFailTest extends RunnableTest<BloqueioUsuario> {
        private UsuarioLogin usuario;
        private final CrudActions<UsuarioLogin> usrCrudActions;
        private final Boolean bloqueadoStartValue;
        
        public PersistBloqueioFailTest(final UsuarioLogin usuario, final Boolean bloqueadoStartValue) {
            super(BloqueioUsuarioCrudAction.NAME);
            this.usuario = usuario;
            this.usrCrudActions = new CrudActionsImpl<>(UsuarioLoginCrudAction.NAME);
            this.bloqueadoStartValue = bloqueadoStartValue;
        }
        
        public PersistBloqueioFailTest(final UsuarioLogin usuario) {
            super(BloqueioUsuarioCrudAction.NAME);
            this.usuario = usuario;
            this.usrCrudActions = new CrudActionsImpl<>(UsuarioLoginCrudAction.NAME);
            this.bloqueadoStartValue = Boolean.FALSE;
        }
        
        @Override
        protected void testComponent() throws Exception {
            usuario = this.usrCrudActions.resetInstance(usuario.getIdUsuarioLogin());
            newInstance();
            setComponentValue("usuarioAtual", usuario);
            initEntity(getEntity(), this);

            assertEquals("usuario não bloqueado", this.bloqueadoStartValue, usuario.getBloqueio());
            final Object bloquearRet = invokeMethod("bloquear");
            assertEquals("persisted", false, PERSISTED.equals(bloquearRet) || UPDATED.equals(bloquearRet));

            final Integer id = getId();
            
            assertEquals("id", bloqueadoStartValue, Boolean.valueOf(id != null));
            
            setEntity(getInstance());
            
            usuario = this.usrCrudActions.resetInstance(usuario.getIdUsuarioLogin());
            assertEquals("usuário bloqueado", this.bloqueadoStartValue, usuario.getBloqueio());
        }
    }
    
    private final class PersistBloqueioUsuarioTest extends RunnableTest<BloqueioUsuario> {
        private UsuarioLogin usuario;
        private final CrudActions<UsuarioLogin> usrCrudActions;
        
        public PersistBloqueioUsuarioTest(final UsuarioLogin usuario) {
            super(BloqueioUsuarioCrudAction.NAME);
            this.usuario = usuario;
            this.usrCrudActions = new CrudActionsImpl<>(UsuarioLoginCrudAction.NAME);
        }
        
        @Override
        protected void testComponent() throws Exception {
            usuario = this.usrCrudActions.resetInstance(usuario.getIdUsuarioLogin());
            newInstance();
            setComponentValue("usuarioAtual", usuario);
            initEntity(getEntity(), this);
            assertEquals("usuario não bloqueado", Boolean.FALSE, usuario.getBloqueio());
            final Object bloquearRet = invokeMethod("bloquear");
            assertEquals("persisted", true, PERSISTED.equals(bloquearRet) || UPDATED.equals(bloquearRet));
            final Integer id = getId();
            assertNotNull("id", id);
            newInstance();
            assertNull("nullId", getId());
            setId(id);
            setEntity(getInstance());
            
            usuario = this.usrCrudActions.resetInstance(usuario.getIdUsuarioLogin());
            assertEquals("usuário bloqueado", Boolean.TRUE, usuario.getBloqueio());
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
            assertEquals("persisted", PERSISTED, save());

            final Integer id = getId();
            assertNotNull("id", id);
            newInstance();
            assertNull("nullId", getId());
            setId(id);
            setEntity(getInstance());
        }
    }
    
}
