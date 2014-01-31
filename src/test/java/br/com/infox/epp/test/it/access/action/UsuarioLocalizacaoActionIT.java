package br.com.infox.epp.test.it.access.action;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static java.text.MessageFormat.format;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.ArrayList;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.certificado.Certificado;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.util.DigitalSignatureUtils;
import br.com.infox.core.exception.BusinessException;
import br.com.infox.epp.access.action.UsuarioLocalizacaoCrudAction;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.api.RolesMap;
import br.com.infox.epp.access.comparator.UsuarioLocalizacaoComparator;
import br.com.infox.epp.access.component.tree.LocalizacaoEstruturaTreeHandler;
import br.com.infox.epp.access.component.tree.LocalizacaoNode;
import br.com.infox.epp.access.component.tree.LocalizacaoNodeSearch;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.crud.LocalizacaoCrudAction;
import br.com.infox.epp.access.crud.PapelCrudAction;
import br.com.infox.epp.access.crud.UsuarioLoginCrudAction;
import br.com.infox.epp.access.dao.BloqueioUsuarioDAO;
import br.com.infox.epp.access.dao.LocalizacaoDAO;
import br.com.infox.epp.access.dao.PapelDAO;
import br.com.infox.epp.access.dao.RecursoDAO;
import br.com.infox.epp.access.dao.UsuarioLocalizacaoDAO;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.RecursoManager;
import br.com.infox.epp.access.manager.UsuarioLocalizacaoManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.access.service.PasswordService;
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
import br.com.infox.epp.processo.documento.AssinaturaException;
import br.com.infox.epp.processo.documento.dao.ProcessoDocumentoDAO;
import br.com.infox.epp.processo.documento.home.ProcessoDocumentoHome;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.documento.service.AssinaturaDocumentoService;
import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.infox.epp.processo.localizacao.dao.ProcessoLocalizacaoIbpmDAO;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.processo.situacao.manager.SituacaoProcessoManager;
import br.com.infox.epp.search.Indexer;
import br.com.infox.epp.search.Reindexer;
import br.com.infox.epp.search.SearchHandler;
import br.com.infox.epp.search.SearchService;
import br.com.infox.epp.system.dao.ParametroDAO;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.epp.tarefa.component.tree.TarefasEntityNode;
import br.com.infox.epp.tarefa.component.tree.TarefasTreeHandler;
import br.com.infox.epp.tarefa.dao.ProcessoEpaTarefaDAO;
import br.com.infox.epp.tarefa.manager.ProcessoEpaTarefaManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
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
public class UsuarioLocalizacaoActionIT  extends AbstractCrudTest<UsuarioLocalizacao> {

    private static final String EMPRESA_Y = "Empresa Y";
    private static final String EMPRESA_X = "Empresa X";
    private static final String ESTRUTURA_EMPRESA = "Estrutura Empresa";
    private static final String ESTRUTURA_EPP = "Estrutura Epp";
    private static final String SETOR_DE_COMPRAS = "Setor de Compras";
    private static final String SETOR_FINANCEIRO = "Setor Financeiro";
    private static final String SETOR_PESSOAL = "Setor Pessoal";
    private static final String GERENCIA = "Gerência";

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
            .addPackages("br.com.infox.ibpm.task")
            .addClasses(UsuarioLocalizacaoCrudAction.class, UsuarioLocalizacaoManager.class,
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
                    SearchService.class,VariableHandler.class, Variavel.class,ProcessoEpaTarefaDAO.class,
                    LocalizacaoTurnoDAO.class,TaskInstanceDAO.class,TarefasTreeHandler.class,
                    LocalizacaoNode.class,LocalizacaoNodeSearch.class,VariableAccessHandler.class,
                    TarefasEntityNode.class, AssinaturaException.class,LocalizacaoCrudAction.class, 
                    LocalizacaoTreeHandler.class,PapelCrudAction.class,RolesMap.class,PapelManager.class,
                    RecursoManager.class,PapelDAO.class,RecursoDAO.class,LogProvider.class,
                    AssinaturaDocumentoService.class)
            .createDeployment();
    }
    
    @Override
    protected void initEntity(final UsuarioLocalizacao entity, final CrudActions<UsuarioLocalizacao> crudActions) {
        crudActions.setComponentValue("usuarioGerenciado", entity.getUsuario());//req
        crudActions.setEntityValue("localizacao", entity.getLocalizacao());//req
        crudActions.setEntityValue("papel", entity.getPapel());//req
        crudActions.setEntityValue("estrutura", entity.getEstrutura());
        crudActions.setEntityValue("responsavelLocalizacao", entity.getResponsavelLocalizacao());
    }
    
    @Override
    protected String getComponentName() {
        return UsuarioLocalizacaoCrudAction.NAME;
    }
    
    private final RunnableTest<Localizacao> persistLocalizacao = new RunnableTest<Localizacao>(LocalizacaoCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            this.crudActions.newInstance();
            initEntity(getEntity());
            assertEquals("persisted", PERSISTED, this.crudActions.save());

            final Integer id = this.crudActions.getId();
            assertNotNull("id", id);
            this.crudActions.newInstance();
            assertNull("nullId", this.crudActions.getId());
            this.crudActions.setId(id);
            setEntity(this.crudActions.getInstance());
        }

        private void initEntity(Localizacao entity) {
            this.crudActions.setEntityValue("localizacao", entity.getLocalizacao());// required
            this.crudActions.setEntityValue("estrutura", entity.getEstrutura());// required
            this.crudActions.setEntityValue("localizacaoPai", entity.getLocalizacaoPai());
            this.crudActions.setEntityValue("estruturaFilho", entity.getEstruturaFilho());
            this.crudActions.setEntityValue("ativo", entity.getAtivo());// required
        }
    };
    
    private final RunnableTest<Papel> persistPapel = new RunnableTest<Papel>(PapelCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Papel entity = getEntity();
            this.crudActions.newInstance();
            initEntity(entity);
            assertEquals("persisted", PERSISTED, this.crudActions.save());

            final Integer id = this.crudActions.getId();
            assertNotNull("id", id);
            this.crudActions.newInstance();
            assertNull("nullId", this.crudActions.getId());
            this.crudActions.setId(id);
            
            boolean roleExists = IdentityManager.instance().roleExists(entity.getIdentificador());
            assertEquals("roleExists", true, roleExists);
            
            setEntity(this.crudActions.getInstance());
        }

        private void initEntity(final Papel entity) {
            this.crudActions.setEntityValue("identificador", entity.getIdentificador()); //req
            this.crudActions.setEntityValue("nome", entity.getNome()); // req
        }
    };

    private final RunnableTest<UsuarioLogin> persistUsuario = new RunnableTest<UsuarioLogin>(UsuarioLoginCrudAction.NAME) {
        private void initEntity(final UsuarioLogin entity) {
            this.crudActions.setEntityValue("nomeUsuario", entity.getNomeUsuario());
            this.crudActions.setEntityValue("email", entity.getEmail());
            this.crudActions.setEntityValue("login", entity.getLogin());
            this.crudActions.setEntityValue("tipoUsuario", entity.getTipoUsuario());
            this.crudActions.setEntityValue("ativo", entity.getAtivo());
            this.crudActions.setEntityValue("provisorio", entity.getProvisorio());
        }
        
        @Override
        protected void testComponent() throws Exception {
            this.crudActions.newInstance();
            initEntity(getEntity());
            assertEquals("persisted", PERSISTED, this.crudActions.save());

            final Integer id = this.crudActions.getId();
            assertNotNull("id", id);
            this.crudActions.newInstance();
            assertNull("nullId", this.crudActions.getId());
            this.crudActions.setId(id);
            setEntity(this.crudActions.getInstance());
        }
    };
    
//    private final RunnableTest<UsuarioLocalizacao> persistSuccess = new RunnableTest<UsuarioLocalizacao>() {
//        @Override
//        protected void testComponent() throws Exception {
//            crudActions.newInstance();
//            initEntity(getEntity());
//            
//            assertEquals("persisted", true, PERSISTED.equals(crudActions.invokeMethod("persist")));
//        }
//    };
    
    private ArrayList<Localizacao> initLocalizacoes(final String suffix) throws Exception {
        final ArrayList<Localizacao> resultMap = new ArrayList<>();
        final Localizacao estruturaEpp = persistLocalizacao.runTest(new Localizacao(ESTRUTURA_EPP+suffix, Boolean.TRUE, Boolean.TRUE));
        resultMap.add(estruturaEpp);
        final Localizacao estruturaEmpresa = persistLocalizacao.runTest(new Localizacao(ESTRUTURA_EMPRESA+suffix, Boolean.TRUE, Boolean.TRUE));
        resultMap.add(estruturaEmpresa);
        final Localizacao gerencia = persistLocalizacao.runTest(new Localizacao(GERENCIA+suffix, Boolean.FALSE, Boolean.TRUE, estruturaEmpresa, null));
        resultMap.add(gerencia);
        final Localizacao setorPessoal = persistLocalizacao.runTest(new Localizacao(SETOR_PESSOAL+suffix, Boolean.TRUE, Boolean.TRUE, gerencia, null));
        resultMap.add(setorPessoal);
        final Localizacao setorFinanceiro = persistLocalizacao.runTest(new Localizacao(SETOR_FINANCEIRO+suffix, Boolean.TRUE, Boolean.TRUE, gerencia, null));
        resultMap.add(setorFinanceiro);
        final Localizacao setorCompras = persistLocalizacao.runTest(new Localizacao(SETOR_DE_COMPRAS+suffix, Boolean.TRUE, Boolean.TRUE, gerencia, null));
        resultMap.add(setorCompras);
        final Localizacao empresaX = persistLocalizacao.runTest(new Localizacao(EMPRESA_X+suffix, Boolean.FALSE, Boolean.TRUE, estruturaEpp, estruturaEmpresa));
        resultMap.add(empresaX);
        final Localizacao empresaY = persistLocalizacao.runTest(new Localizacao(EMPRESA_Y+suffix, Boolean.FALSE, Boolean.TRUE, estruturaEpp, estruturaEmpresa));
        resultMap.add(empresaY);
        return resultMap;
    }
    
    private ArrayList<Papel> initPapeis(final String suffix) throws Exception {
        final ArrayList<Papel> result = new ArrayList<>();
        result.add(persistPapel.runTest(new Papel("Gestor"+suffix,"gestor"+suffix)));
        result.add(persistPapel.runTest(new Papel("Administrador Admin"+suffix,"admin"+suffix)));
        result.add(persistPapel.runTest(new Papel("Comprador"+suffix,"comprador"+suffix)));
        result.add(persistPapel.runTest(new Papel("Colaborador"+suffix,"colaborador"+suffix)));
        return result;
    }
    
    @Test
    public void persistSuccessTest() throws Exception {
        final String login = "usuario1";
        final UsuarioLogin usuarioLogin = persistUsuario.runTest(new UsuarioLogin("Nome "+login,format("{0}@infox.com.br",login),login));
        final ArrayList<Papel> papeis = initPapeis("pers.success");
        final ArrayList<Localizacao> locs = initLocalizacoes("pers.success");
        for (Localizacao localizacao : locs) {
            for (Localizacao estrutura : locs) {
                for (Papel papel : papeis) {
                    persistSuccess.runTest(new UsuarioLocalizacao(usuarioLogin, localizacao, estrutura, papel));   
                }
            }
        }
    }
    
    @Test
    public void persistFailTest() throws Exception {
        final String login = "usuario2";
        final UsuarioLogin usuarioLogin = persistUsuario.runTest(new UsuarioLogin("Nome "+login,format("{0}@infox.com.br",login),login));
        final ArrayList<Papel> papeis = initPapeis("pers.fail");
        final ArrayList<Localizacao> locs = initLocalizacoes("pers.fail");
        for (Localizacao estrutura : locs) {
            for (Papel papel : papeis) {
                persistFail.runTest(new UsuarioLocalizacao(usuarioLogin, null, estrutura, papel));   
            }
        }
        
        for (Localizacao localizacao : locs) {
            for (Localizacao estrutura : locs) {
                persistFail.runTest(new UsuarioLocalizacao(usuarioLogin, localizacao, estrutura, null));
            }
        }
    }
    
    @Test
    public void removeSuccessTest() throws Exception {
        final String login = "usuario3";
        final UsuarioLogin usuarioLogin = persistUsuario.runTest(new UsuarioLogin("Nome "+login,format("{0}@infox.com.br",login),login));
        final ArrayList<Papel> papeis = initPapeis("rem.success");
        final ArrayList<Localizacao> locs = initLocalizacoes("rem.success");
        
        for (Localizacao localizacao : locs) {
            for (Localizacao estrutura : locs) {
                for (Papel papel : papeis) {
                    removeSuccess.runTest(new UsuarioLocalizacao(usuarioLogin, localizacao, estrutura, papel));   
                }
            }
        }
    }
    
}
