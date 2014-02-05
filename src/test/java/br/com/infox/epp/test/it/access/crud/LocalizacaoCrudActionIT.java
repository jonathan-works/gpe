package br.com.infox.epp.test.it.access.crud;

import static br.com.infox.core.action.AbstractAction.UPDATED;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

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
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class LocalizacaoCrudActionIT extends AbstractCrudTest<Localizacao> {
    private static final String SERVLET_3_0 = "Servlet 3.0";
    
    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
            .addClasses(LocalizacaoCrudAction.class, LocalizacaoManager.class, LocalizacaoTreeHandler.class,
                        LocalizacaoDAO.class).createDeployment();
    }

    private final HashMap<String, Localizacao> localizacoes = new HashMap<>();

    private Localizacao persistIfNotOnMap(final Localizacao entity, final InternalRunnableTest<Localizacao> runnable) throws Exception {
        Localizacao result;
        final String key = entity.getLocalizacao();
        if (localizacoes.containsKey(key)) {
            result = localizacoes.get(key);
        } else {
            result = runnable.runTest(entity);
            localizacoes.put(key, result);
        }
        return result;
    }
    
    private Localizacao persistEstruturaFilho(final Localizacao entity, final InternalRunnableTest<Localizacao> runnable) throws Exception {
        Localizacao result = null;
        if (entity != null) {
            entity.setEstruturaFilho(persistEstruturaFilho(entity.getEstruturaFilho(), runnable));
            result = persistIfNotOnMap(entity, runnable);
        }
        return result;
    }

    private Localizacao persistParent(final Localizacao entity, final InternalRunnableTest<Localizacao> runnable) throws Exception {
        Localizacao result = null;
        if (entity != null) {
            entity.setParent(persistParent(entity.getParent(),runnable));
            result = persistIfNotOnMap(entity, runnable);
        }
        return result;
    }

    protected Localizacao persistSuccessTest(final Localizacao entity) throws Exception {
        final Localizacao localizacaoPai = persistParent(entity.getLocalizacaoPai(),persistSuccess);
        entity.setLocalizacaoPai(localizacaoPai);
        final Localizacao estruturaFilho = persistEstruturaFilho(entity.getEstruturaFilho(),persistSuccess);
        entity.setEstruturaFilho(estruturaFilho);
        return persistIfNotOnMap(entity, persistSuccess);
    }

    protected void initEntity(final Localizacao entity, final CrudActions<Localizacao> crudActions) {
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
        
        final Localizacao localizacaoGerencia = new Localizacao("Gerência", Boolean.FALSE, Boolean.TRUE, estruturaEmpresa, null);
        
        persistSuccessTest(new Localizacao("Setor Pessoal", Boolean.FALSE, Boolean.TRUE, localizacaoGerencia, null));
        persistSuccessTest(new Localizacao("Setor Financeiro", Boolean.FALSE, Boolean.TRUE, localizacaoGerencia, null));
        persistSuccessTest(new Localizacao("Setor de Compras", Boolean.FALSE, Boolean.TRUE, localizacaoGerencia, null));

        persistSuccessTest(new Localizacao("Empresa Hipotética", Boolean.FALSE, Boolean.FALSE, estruturaEPP, estruturaEmpresa));
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
    
    private final InternalRunnableTest<Localizacao> inactivateSuccess = new InternalRunnableTest<Localizacao>() {
        @Override
        protected void testComponent() throws Exception {
            final Localizacao entity = getEntity();
            if (entity.getIdLocalizacao() !=  null) {
                crudActions.resetInstance(entity.getIdLocalizacao());
            }
            assertEquals("children inactive", true, assertChildrenActive(crudActions.getInstance()));
            assertEquals("inactivate success true", UPDATED, crudActions.inactivate());
            assertEquals("children active", true, assertChildrenInactive(crudActions.getInstance()));
        }
    };
    
    @Test
    public void inactivateTest() throws Exception {
        final Localizacao estruturaEPP = persistSuccessTest(new Localizacao("EPP.inactiveSuc", Boolean.TRUE, Boolean.TRUE));
        final Localizacao estruturaEmpresa = persistSuccessTest(new Localizacao("Estrutura Empresa.inactiveSuc", Boolean.TRUE, Boolean.TRUE));
        
        final Localizacao locGerencia = persistSuccessTest(new Localizacao("Gerência.inactiveSuc", Boolean.FALSE, Boolean.TRUE, estruturaEmpresa, null));
        final Localizacao locSetorPessoal = persistSuccessTest(new Localizacao("Setor Pessoal.inactiveSuc", Boolean.FALSE, Boolean.TRUE, locGerencia, null));
        final Localizacao locSetorFinanceiro = persistSuccessTest(new Localizacao("Setor Financeiro.inactiveSuc", Boolean.FALSE, Boolean.TRUE, locSetorPessoal, null));
        persistSuccessTest(new Localizacao("Setor de Compras.inactiveSuc", Boolean.FALSE, Boolean.TRUE, locSetorFinanceiro, null));
        persistSuccessTest(new Localizacao("Empresa Hipotética.inactiveSuc", Boolean.FALSE, Boolean.TRUE, estruturaEPP, estruturaEmpresa));
        
        inactivateSuccess.runTest(estruturaEmpresa);
    }
    
    final InternalRunnableTest<Localizacao> updateSuccess = new InternalRunnableTest<Localizacao>() {
        @Override
        protected void testComponent() throws Exception {
            final Localizacao entity = getEntity();
            final Integer id = entity.getIdLocalizacao();
            updateValueAndTest(id, "localizacao", entity.getLocalizacao()+".changed", true, this.crudActions);
            updateValueAndTest(id, "localizacaoPai", null, true, this.crudActions);
            final Localizacao localizacao = crudActions.resetInstance(id);
            assertEquals("localizacao changed",true,localizacao.getLocalizacao().endsWith(".changed"));
            assertNull("localizacaoPai null", localizacao.getLocalizacaoPai());
        }

    };

    private void updateValueAndTest(final Integer id, final String field, final Object value, boolean wasSuccessful, final CrudActions<Localizacao> crudActions) {
        crudActions.resetInstance(id);
        crudActions.setEntityValue(field, value);
        assertEquals("updated", wasSuccessful, UPDATED.equals(crudActions.save()));
    }
    
    final InternalRunnableTest<Localizacao> updateFail = new InternalRunnableTest<Localizacao>() {
        @Override
        protected void testComponent() throws Exception {
            final Localizacao entity = getEntity();
            final Integer id = entity.getIdLocalizacao();

            updateValueAndTest(id, "localizacao", fillStr(entity.getLocalizacao()+".changed",LengthConstants.DESCRICAO_PADRAO+1), false, this.crudActions);
            
            updateValueAndTest(id, "localizacao", null, false, this.crudActions);
            
            updateValueAndTest(id, "estrutura", null, false, this.crudActions);
            
            updateValueAndTest(id,"ativo", null, false, this.crudActions);
        }
    };
    
    @Test
    public void updateSuccessTest() throws Exception {
        final Localizacao estruturaEPP = persistSuccessTest(new Localizacao("EPP.updtSuc", Boolean.TRUE, Boolean.TRUE));
        final Localizacao estruturaEmpresa = persistSuccessTest(new Localizacao("Estrutura Empresa.updtSuc", Boolean.TRUE, Boolean.TRUE));
        
        final Localizacao locGerencia = persistSuccessTest(new Localizacao("Gerência.updtSuc", Boolean.FALSE, Boolean.TRUE, estruturaEmpresa, null));
        final Localizacao locSetorPessoal = persistSuccessTest(new Localizacao("Setor Pessoal.updtSuc", Boolean.FALSE, Boolean.TRUE, locGerencia, null));
        final Localizacao locSetorFinanc = persistSuccessTest(new Localizacao("Setor Financeiro.updtSuc", Boolean.FALSE, Boolean.TRUE, locSetorPessoal, null));
        final Localizacao locEmpresaHip = persistSuccessTest(new Localizacao("Empresa Hipotética.updtSuc", Boolean.FALSE, Boolean.TRUE, estruturaEPP, estruturaEmpresa));
        
        updateSuccess.runTest(locGerencia);
        updateSuccess.runTest(locSetorPessoal);
        updateSuccess.runTest(locSetorFinanc);
        updateSuccess.runTest(locEmpresaHip);
    }
    
    
    
    @Test
    public void updateFailTest() throws Exception {
        final Localizacao estruturaEPP = persistSuccessTest(new Localizacao("EPP.updtFail", Boolean.TRUE, Boolean.TRUE));
        final Localizacao estruturaEmpresa = persistSuccessTest(new Localizacao("Estrutura Empresa.updtFail", Boolean.TRUE, Boolean.TRUE));
        
        final Localizacao locGerencia = persistSuccessTest(new Localizacao("Gerência.updtFail", Boolean.FALSE, Boolean.TRUE, estruturaEmpresa, null));
        final Localizacao locSetorPessoal = persistSuccessTest(new Localizacao("Setor Pessoal.updtFail", Boolean.FALSE, Boolean.TRUE, locGerencia, null));
        final Localizacao locSetorFinanc = persistSuccessTest(new Localizacao("Setor Financeiro.updtFail", Boolean.FALSE, Boolean.TRUE, locSetorPessoal, null));
        final Localizacao locEmpresaHip = persistSuccessTest(new Localizacao("Empresa Hipotética.updtFail", Boolean.FALSE, Boolean.TRUE, estruturaEPP, estruturaEmpresa));
        
        updateFail.runTest(locGerencia);
        updateFail.runTest(locSetorPessoal);
        updateFail.runTest(locSetorFinanc);
        updateFail.runTest(locEmpresaHip);
        updateFail.runTest(estruturaEPP);
        updateFail.runTest(estruturaEmpresa);
    }
    
}