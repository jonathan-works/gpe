package br.com.infox.epp.test.it.access.crud;

import static java.text.MessageFormat.format;

import java.util.HashMap;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.crud.LocalizacaoCrudAction;
import br.com.infox.epp.access.dao.LocalizacaoDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.test.crud.AbstractGenericCrudTest;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class LocalizacaoCrudActionIT extends AbstractGenericCrudTest<Localizacao> {
    private static final String SERVLET_3_0 = "Servlet 3.0";
    
    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
            .addClasses(LocalizacaoCrudAction.class, LocalizacaoManager.class, LocalizacaoTreeHandler.class,
                        LocalizacaoDAO.class).createDeployment();
    }

    private final HashMap<String, Localizacao> localizacoes = new HashMap<>();

    private Localizacao persistIfNotOnMap(final Localizacao entity, final RunnableTest<Localizacao> runnable) throws Exception {
        Localizacao result;
        final String key = entity.getLocalizacao();
        if (localizacoes.containsKey(key)) {
            result = localizacoes.get(key);
        } else {
            result = runnable.runTest(entity);
            System.out.println(format("'{'id:{0},caminhoCompleto:{1}'}'", result.getIdLocalizacao(), result.getCaminhoCompleto()));
            localizacoes.put(key, result);
        }
        return result;
    }
    
    private Localizacao persistEstruturaFilho(final Localizacao entity, final RunnableTest<Localizacao> runnable) throws Exception {
        Localizacao result = null;
        if (entity != null) {
            entity.setEstruturaFilho(persistEstruturaFilho(entity.getEstruturaFilho(), runnable));
            result = persistIfNotOnMap(entity, runnable);
        }
        return result;
    }

    private Localizacao persistParent(final Localizacao entity, final RunnableTest<Localizacao> runnable) throws Exception {
        Localizacao result = null;
        if (entity != null) {
            entity.setParent(persistParent(entity.getParent(),runnable));
            result = persistIfNotOnMap(entity, runnable);
        }
        return result;
    }

    protected Localizacao persistSuccessTest(final Localizacao entity) throws Exception {
        entity.setLocalizacaoPai(persistParent(entity.getLocalizacaoPai(),persistSuccess));
        entity.setEstruturaFilho(persistEstruturaFilho(entity.getEstruturaFilho(),persistSuccess));
        return persistIfNotOnMap(entity, persistSuccess);
    }

    protected void initEntity(final Localizacao entity) {
        final CrudActions<Localizacao> crudActions = getCrudActions();
        crudActions.setEntityValue("localizacao", entity.getLocalizacao());// required
        crudActions.setEntityValue("estrutura", entity.getEstrutura());// required
        crudActions.setEntityValue("localizacaoPai", entity.getLocalizacaoPai());
        crudActions.setEntityValue("estruturaFilho", entity.getEstruturaFilho());
        crudActions.setEntityValue("ativo", entity.getAtivo());// required
    }

    protected String getComponentName() {
        return LocalizacaoCrudAction.NAME;
    }

    @Test
    public void persistSuccessTest() throws Exception {
        final Localizacao estruturaEPP = new Localizacao("EPP", Boolean.TRUE, Boolean.TRUE);
        final Localizacao estruturaEmpresa = new Localizacao("Estrutura Empresa", Boolean.TRUE, Boolean.TRUE);
        
        final Localizacao localizacaoGerencia = new Localizacao("Gerência", Boolean.TRUE, Boolean.FALSE, estruturaEmpresa, null);
        
        persistSuccessTest(new Localizacao("Setor Pessoal", Boolean.TRUE, Boolean.FALSE, localizacaoGerencia, null));
        persistSuccessTest(new Localizacao("Setor Financeiro", Boolean.TRUE, Boolean.FALSE, localizacaoGerencia, null));
        persistSuccessTest(new Localizacao("Setor de Compras", Boolean.TRUE, Boolean.FALSE, localizacaoGerencia, null));

        persistSuccessTest(new Localizacao("Empresa Hipotética", Boolean.TRUE, Boolean.FALSE, estruturaEPP, estruturaEmpresa));
    }

    @Test
    public void persistFailTest() throws Exception {
        //FALHAS AO INSERIR DESCRIÇÕES INVÁLIDAS
        persistFail.runTest(new Localizacao(fillStr("Setor Pessoal.Fail", LengthConstants.DESCRICAO_PADRAO+1), Boolean.TRUE, Boolean.TRUE, null, null));
        persistFail.runTest(new Localizacao(null, Boolean.TRUE, Boolean.TRUE));
        //FALHAS AO INSERIR VALOR INVÁLIDO PARA ESTRUTURA
        persistFail.runTest(new Localizacao("Setor Pessoal.Fail", null, Boolean.TRUE));
        
        //FALHAS AO INSERIR VALOR INVÁLIDO PARA ATIVO
        persistFail.runTest(new Localizacao("Setor Pessoal.Fail", Boolean.TRUE, null));
    }
    
}