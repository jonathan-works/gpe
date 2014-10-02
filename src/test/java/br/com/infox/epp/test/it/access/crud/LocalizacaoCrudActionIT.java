package br.com.infox.epp.test.it.access.crud;

import static br.com.infox.core.action.AbstractAction.UPDATED;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.crud.LocalizacaoCrudAction;
import br.com.infox.epp.access.dao.BloqueioUsuarioDAO;
import br.com.infox.epp.access.dao.LocalizacaoDAO;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.documento.dao.ModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.VariavelDAO;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.mail.entity.EMailData;
import br.com.infox.epp.mail.service.AccessMailService;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.documento.dao.ProcessoDocumentoBinDAO;
import br.com.infox.epp.processo.documento.dao.DocumentoDAO;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoBinManager;
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
import br.com.infox.epp.test.crud.PersistSuccessTest;
import br.com.infox.epp.test.crud.RunnableTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;
import br.com.infox.hibernate.session.SessionAssistant;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaDAO;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;

@RunWith(Arquillian.class)
public class LocalizacaoCrudActionIT extends AbstractCrudTest<Localizacao> {
    private static final String SERVLET_3_0 = "Servlet 3.0";
    
    private static ActionContainer<Localizacao> initEntityAction = new ActionContainer<Localizacao>() {
        @Override
        public void execute(final CrudActions<Localizacao> crudActions) {
            final Localizacao entity = getEntity();
            crudActions.setEntityValue("localizacao", entity.getLocalizacao());// required
//            crudActions.setEntityValue("estrutura", entity.getEstrutura());// required
            crudActions.setEntityValue("localizacaoPai", entity.getLocalizacaoPai());
            crudActions.setEntityValue("estruturaFilho", entity.getEstruturaFilho());
            crudActions.setEntityValue("ativo", entity.getAtivo());// required
        }
    };

    private static final PersistSuccessTest<Localizacao> persistSuccessLocalizacao = new PersistSuccessTest<>(LocalizacaoCrudAction.NAME, initEntityAction);

    private static final Localizacao persistEstruturaFilho(final Localizacao entity, final ActionContainer<Localizacao> action, final HashMap<String, Localizacao> localizacoes,final ServletContext servletContext, final HttpSession session) throws Exception {
        Localizacao result = null;
        if (entity != null) {
//            entity.setEstruturaFilho(persistEstruturaFilho(entity.getEstruturaFilho(), action, localizacoes, servletContext, session));
            result = persistIfNotOnMap(entity, action, localizacoes, servletContext, session);
        }
        return result;
    }

    private static final Localizacao persistIfNotOnMap(final Localizacao entity, final ActionContainer<Localizacao> action, final HashMap<String, Localizacao> localizacoes, final ServletContext servletContext, final HttpSession session) throws Exception {
        Localizacao result;
        final String key = entity.getLocalizacao();
        if (localizacoes.containsKey(key)) {
            result = localizacoes.get(key);
        } else {
            result = persistSuccessLocalizacao.runTest(action, entity, servletContext, session);
            localizacoes.put(key, result);
        }
        return result;
    }
    
    private static final Localizacao persistParent(final Localizacao entity, final ActionContainer<Localizacao> action, final HashMap<String, Localizacao> localizacoes,final ServletContext servletContext, final HttpSession session) throws Exception {
        Localizacao result = null;
        if (entity != null) {
            entity.setParent(persistParent(entity.getParent(), action, localizacoes, servletContext, session));
            result = persistIfNotOnMap(entity, action, localizacoes, servletContext, session);
        }
        return result;
    }

    private static final Localizacao persistSuccessTest(final Localizacao entity, final ActionContainer<Localizacao> action, final HashMap<String, Localizacao> localizacoes,final ServletContext servletContext, final HttpSession session) throws Exception {
        final Localizacao localizacaoPai = persistParent(entity.getLocalizacaoPai(), action, localizacoes, servletContext, session);
        entity.setLocalizacaoPai(localizacaoPai);
//        final Localizacao estruturaFilho = persistEstruturaFilho(entity.getEstruturaFilho(), action, localizacoes, servletContext, session);
//        entity.setEstruturaFilho(estruturaFilho);
        return persistIfNotOnMap(entity, action, localizacoes, servletContext, session);
    }

    private static void updateValueAndTest(final Integer id, final String field, final Object value, final boolean wasSuccessful, final CrudActions<Localizacao> crudActions) {
        crudActions.resetInstance(id);
        crudActions.setEntityValue(field, value);
        assertEquals("updated", wasSuccessful, UPDATED.equals(crudActions.save()));
    }

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
            .addClasses(LocalizacaoCrudAction.class, LocalizacaoManager.class, LocalizacaoTreeHandler.class,
                        LocalizacaoDAO.class, Authenticator.class, UsuarioLoginManager.class,
                        UsuarioLoginDAO.class, AuthenticatorService.class,
                        ProcessoDAO.class, BloqueioUsuarioManager.class, BloqueioUsuarioDAO.class,
                        UsuarioLoginManager.class, PasswordService.class,
                        AccessMailService.class, ModeloDocumentoManager.class, VariavelDAO.class,
                        ModeloDocumentoDAO.class, ParametroManager.class, ParametroDAO.class,
                        EMailData.class,
                        DominioVariavelTarefaManager.class, DominioVariavelTarefaDAO.class,
                        DocumentoManager.class, DocumentoDAO.class, SessionAssistant.class,
                        SigiloDocumentoService.class, SigiloDocumentoManager.class, SigiloDocumentoDAO.class,
                        SigiloDocumentoPermissaoManager.class, SigiloDocumentoPermissaoDAO.class,
                        ProcessoDocumentoBinDAO.class, ProcessoDocumentoBinManager.class).createDeployment();
    }
    
    public static final List<Localizacao> getSuccessfullyPersisted(final ActionContainer<Localizacao> action, final String suffix,final ServletContext servletContext, final HttpSession session) throws Exception {
        final HashMap<String, Localizacao> localizacoes = new HashMap<>();

//        final Localizacao estruturaEPP = new Localizacao(format("EPP{0}",suffix), Boolean.TRUE, Boolean.TRUE);
//        final Localizacao estruturaEmpresa = new Localizacao(format("Estrutura Empresa{0}",suffix), Boolean.TRUE, Boolean.TRUE);
//        
//        final Localizacao localizacaoGerencia = new Localizacao(format("Gerência{0}",suffix), Boolean.FALSE, Boolean.TRUE, estruturaEmpresa, null);
//        
//        persistSuccessTest(new Localizacao(format("Setor Pessoal{0}",suffix), Boolean.FALSE, Boolean.TRUE, localizacaoGerencia, null), action,localizacoes, servletContext, session);
//        persistSuccessTest(new Localizacao(format("Setor Financeiro{0}",suffix), Boolean.FALSE, Boolean.TRUE, localizacaoGerencia, null), action,localizacoes, servletContext, session);
//        persistSuccessTest(new Localizacao(format("Setor de Compras{0}",suffix), Boolean.FALSE, Boolean.TRUE, localizacaoGerencia, null), action,localizacoes, servletContext, session);
//
//        persistSuccessTest(new Localizacao(format("Empresa Hipotética{0}",suffix), Boolean.FALSE, Boolean.FALSE, estruturaEPP, estruturaEmpresa), action,localizacoes, servletContext, session);
//        
        return new ArrayList<Localizacao>(localizacoes.values());
    }
    
    private final RunnableTest<Localizacao> inactivateSuccess = new RunnableTest<Localizacao>(LocalizacaoCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Localizacao entity = getEntity();
            if (entity.getIdLocalizacao() !=  null) {
                resetInstance(entity.getIdLocalizacao());
            }
            assertEquals("children inactive", true, assertChildrenActive(getInstance()));
            assertEquals("inactivate success true", UPDATED, inactivate());
            assertEquals("children active", true, assertChildrenInactive(getInstance()));
        }
    };

    private final RunnableTest<Localizacao> updateSuccess = new RunnableTest<Localizacao>(LocalizacaoCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Localizacao entity = getEntity();
            final Integer id = entity.getIdLocalizacao();
            updateValueAndTest(id, "localizacao", entity.getLocalizacao()+".changed", true, this);
            updateValueAndTest(id, "localizacaoPai", null, true, this);
            final Localizacao localizacao = this.resetInstance(id);
            assertEquals("localizacao changed",true,localizacao.getLocalizacao().endsWith(".changed"));
            assertNull("localizacaoPai null", localizacao.getLocalizacaoPai());
        }
    };

    private final RunnableTest<Localizacao> updateFail = new RunnableTest<Localizacao>(LocalizacaoCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Localizacao entity = getEntity();
            final Integer id = entity.getIdLocalizacao();

            updateValueAndTest(id, "localizacao", fillStr(entity.getLocalizacao()+".changed",LengthConstants.DESCRICAO_PADRAO+1), false, this);
            
            updateValueAndTest(id, "localizacao", null, false, this);
            
            updateValueAndTest(id, "estrutura", null, false, this);
            
            updateValueAndTest(id,"ativo", null, false, this);
        }
    };

    private boolean assertChildrenActive(final Localizacao l) {
        boolean result = l.getAtivo();
        for (final Localizacao localizacao : l.getLocalizacaoList()) {
            result = result && assertChildrenActive(localizacao);
        }
        return result;
    }
    
    private boolean assertChildrenInactive(final Localizacao l) {
        boolean result = l.getAtivo();
        for (final Localizacao localizacao : l.getLocalizacaoList()) {
            result = result || assertChildrenInactive(localizacao);
        }
        return !result;
    }
    
    @Override
    protected String getComponentName() {
        return LocalizacaoCrudAction.NAME;
    }
    
    @Override
    protected ActionContainer<Localizacao> getInitEntityAction() {
        return initEntityAction;
    }
    
    @Test
    public void inactivateSuccessTest() throws Exception {
        final List<Localizacao> successfullyPersisted = getSuccessfullyPersisted(null, ".inac-suc", servletContext, session);
        for (final Localizacao localizacao : successfullyPersisted) {
            if ("Estrutura Empresa.inac-suc".equals(localizacao.getLocalizacao())) {
                inactivateSuccess.runTest(localizacao, servletContext, session);
                break;
            }
        }
    }
    
    @Test
    public void persistFailTest() throws Exception {
        //FALHAS AO INSERIR DESCRIÇÕES INVÁLIDAS
//        persistFail.runTest(new Localizacao(fillStr("Setor Pessoal.Fail", LengthConstants.DESCRICAO_PADRAO+1), Boolean.TRUE, Boolean.TRUE, null, null), servletContext, session);
//        persistFail.runTest(new Localizacao(null, Boolean.TRUE, Boolean.TRUE), servletContext, session);
        //FALHAS AO INSERIR VALOR INVÁLIDO PARA ESTRUTURA
//        persistFail.runTest(new Localizacao("Setor Pessoal.Fail", null, Boolean.TRUE), servletContext, session);
        
        //FALHAS AO INSERIR VALOR INVÁLIDO PARA ATIVO
        persistFail.runTest(new Localizacao("Setor Pessoal.Fail", Boolean.TRUE, null), servletContext, session);
    }
    
    @Test
    public void persistSuccessTest() throws Exception {
        getSuccessfullyPersisted(null, "", servletContext, session);
    }
    
    @Test
    public void updateFailTest() throws Exception {
        final List<Localizacao> successfullyPersisted = getSuccessfullyPersisted(null, "updtFail", servletContext, session);
        for (final Localizacao localizacao : successfullyPersisted) {
            updateFail.runTest(localizacao, servletContext, session);
        }
    }
    
    @Test
    public void updateSuccessTest() throws Exception {
        final List<Localizacao> successfullyPersisted = getSuccessfullyPersisted(null, "updtSuc", servletContext, session);
        for (final Localizacao localizacao : successfullyPersisted) {
            updateSuccess.runTest(localizacao, servletContext, session);
        }
    }
    
}