package br.com.infox.epp.test.it.access.action;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.REMOVED;
import static br.com.infox.core.action.AbstractAction.UPDATED;
import static java.text.MessageFormat.format;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.certificado.Certificado;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.util.DigitalSignatureUtils;
import br.com.infox.core.exception.BusinessException;
import br.com.infox.epp.access.action.UsuarioLocalizacaoAction;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.comparator.UsuarioLocalizacaoComparator;
import br.com.infox.epp.access.component.tree.LocalizacaoEstruturaTreeHandler;
import br.com.infox.epp.access.component.tree.LocalizacaoNode;
import br.com.infox.epp.access.component.tree.LocalizacaoNodeSearch;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.crud.UsuarioLoginCrudAction;
import br.com.infox.epp.access.dao.BloqueioUsuarioDAO;
import br.com.infox.epp.access.dao.LocalizacaoDAO;
import br.com.infox.epp.access.dao.UsuarioLocalizacaoDAO;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.UsuarioLocalizacaoManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.ajuda.util.HelpUtil;
import br.com.infox.epp.documento.dao.ModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.TipoProcessoDocumentoDAO;
import br.com.infox.epp.documento.dao.VariavelDAO;
import br.com.infox.epp.documento.home.DocumentoBinHome;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.filter.ControleFiltros;
import br.com.infox.epp.mail.entity.EMailData;
import br.com.infox.epp.mail.service.AccessMailService;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.dao.ProcessoEpaDAO;
import br.com.infox.epp.processo.documento.dao.ProcessoDocumentoDAO;
import br.com.infox.epp.processo.documento.home.ProcessoDocumentoHome;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.infox.epp.processo.localizacao.dao.ProcessoLocalizacaoIbpmDAO;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.processo.situacao.manager.SituacaoProcessoManager;
import br.com.infox.epp.search.Indexer;
import br.com.infox.epp.search.Reindexer;
import br.com.infox.epp.search.SearchHandler;
import br.com.infox.epp.system.dao.ParametroDAO;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.epp.tarefa.component.tree.TarefasEntityNode;
import br.com.infox.epp.tarefa.component.tree.TarefasTreeHandler;
import br.com.infox.epp.tarefa.dao.ProcessoEpaTarefaDAO;
import br.com.infox.epp.tarefa.manager.ProcessoEpaTarefaManager;
import br.com.infox.epp.test.crud.AbstractGenericCrudTest;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;
import br.com.infox.epp.turno.dao.LocalizacaoTurnoDAO;
import br.com.infox.ibpm.task.action.TaskPageAction;
import br.com.infox.ibpm.task.dao.TaskInstanceDAO;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.task.manager.TaskInstanceManager;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.ibpm.util.UserHandler;
import br.com.infox.ibpm.variable.VariableAccessHandler;
import br.com.infox.ibpm.variable.VariableHandler;
import br.com.infox.ibpm.variable.Variavel;

@RunWith(Arquillian.class)
public class UsuarioLocalizacaoActionIT  extends AbstractGenericCrudTest<UsuarioLocalizacao> {

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
            .addPackages("br.com.infox.ibpm.task")
            .addClasses(UsuarioLocalizacaoAction.class, UsuarioLocalizacaoManager.class,
                    LocalizacaoEstruturaTreeHandler.class,PapelTreeHandler.class,
                    UsuarioLocalizacaoDAO.class, Authenticator.class,
                    UsuarioLoginCrudAction.class,PasswordService.class,AccessMailService.class,
                    UsuarioLoginManager.class,BusinessException.class,
                    ModeloDocumentoManager.class,EMailData.class,UsuarioLoginDAO.class,
                    ModeloDocumentoDAO.class,VariavelDAO.class,ParametroManager.class,ParametroDAO.class,
                    AuthenticatorService.class,ParametroUtil.class,UsuarioLocalizacaoComparator.class,
                    BloqueioUsuarioManager.class,ProcessoDAO.class,BloqueioUsuarioDAO.class,
                    JbpmUtil.class,LocalizacaoManager.class,ProcessoDocumentoManager.class,
                    ProcessoManager.class,LocalizacaoDAO.class,ControleFiltros.class,
                    ProcessoDocumentoDAO.class,ProcessoEpaDAO.class,ProcessoLocalizacaoIbpmDAO.class,
                    ProcessoHome.class,Certificado.class,CertificadoException.class,
                    TipoProcessoDocumentoDAO.class,ProcessoDocumentoHome.class,
                    ProcessoEpaManager.class,TaskInstanceHome.class,DigitalSignatureUtils.class,
                    DocumentoBinHome.class,SituacaoProcessoManager.class,Indexer.class,Reindexer.class,
                    SearchHandler.class,ProcessoEpaTarefaManager.class,TaskPageAction.class,
                    TaskInstanceManager.class,UserHandler.class,SituacaoProcessoDAO.class,
                    HelpUtil.class,VariableHandler.class, Variavel.class,ProcessoEpaTarefaDAO.class,
                    LocalizacaoTurnoDAO.class,TaskInstanceDAO.class,TarefasTreeHandler.class,
                    LocalizacaoNode.class,LocalizacaoNodeSearch.class,VariableAccessHandler.class,
                    TarefasEntityNode.class)
            .createDeployment();
    }
    
    private CrudActions<UsuarioLogin> crudActionsUsuario = new CrudActions<>(UsuarioLoginCrudAction.NAME);
    private static int id=0;
    
    private final RunnableTest<UsuarioLogin> persistSuccessUsuarioLogin = new RunnableTest<UsuarioLogin>() {
        @Override
        protected void testComponent() throws Exception {
            final UsuarioLogin entity = getEntity();
            crudActionsUsuario.newInstance();
            initUsuarioLogin(entity);
            assertEquals("persisted", PERSISTED, crudActionsUsuario.save());

            final Integer id = crudActionsUsuario.getId();
            assertNotNull("id", id);
            crudActionsUsuario.newInstance();
            assertNull("nullId", crudActionsUsuario.getId());
            crudActionsUsuario.setId(id);
            setEntity(crudActionsUsuario.getInstance());
        }
    };
    
    private void initUsuarioLogin(final UsuarioLogin entity) {
        crudActionsUsuario.setEntityValue("nomeUsuario", entity.getNomeUsuario());
        crudActionsUsuario.setEntityValue("email", entity.getEmail());
        crudActionsUsuario.setEntityValue("login", entity.getLogin());
        crudActionsUsuario.setEntityValue("tipoUsuario", entity.getTipoUsuario());
        crudActionsUsuario.setEntityValue("ativo", entity.getAtivo());
        crudActionsUsuario.setEntityValue("provisorio", entity.getProvisorio());
    }
    
    private UsuarioLogin createUsuarioLogin(final UsuarioLocalizacao entity) throws Exception {
        final String login = format("login-no",++id);
        return persistSuccessUsuarioLogin.runTest(new UsuarioLogin("nome",format("{0}@infox.com.br",login),login));
    }
    
    @Override
    protected void initEntity(final UsuarioLocalizacao entity) {
        String t = REMOVED+UPDATED;
        final CrudActions<UsuarioLocalizacao> crudActions = getCrudActions();
        crudActions.setComponentValue("usuarioGerenciado", entity.getUsuario());//req
        crudActions.setComponentValue("localizacao", entity.getLocalizacao());//req
        crudActions.setComponentValue("papel", entity.getPapel());//req
        crudActions.setComponentValue("responsavelLocalizacao", entity.getResponsavelLocalizacao());
    }
    
    @Override
    protected String getComponentName() {
        return UsuarioLocalizacaoAction.NAME;
    }
    
    @Test
    public void test() throws Exception {
        
    }
    
}
