package br.com.infox.epp.test.it.fluxo.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static junit.framework.Assert.assertEquals;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.crud.LocalizacaoCrudAction;
import br.com.infox.epp.access.dao.LocalizacaoDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.fluxo.crud.CategoriaCrudAction;
import br.com.infox.epp.fluxo.crud.FluxoCrudAction;
import br.com.infox.epp.fluxo.crud.NatCatFluxoLocalizacaoCrudAction;
import br.com.infox.epp.fluxo.crud.NaturezaCategoriaFluxoCrudAction;
import br.com.infox.epp.fluxo.crud.NaturezaCrudAction;
import br.com.infox.epp.fluxo.dao.CategoriaDAO;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.dao.NatCatFluxoLocalizacaoDAO;
import br.com.infox.epp.fluxo.dao.NaturezaCategoriaFluxoDAO;
import br.com.infox.epp.fluxo.dao.NaturezaDAO;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.CategoriaManager;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.NatCatFluxoLocalizacaoManager;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.epp.fluxo.manager.NaturezaManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.RunnableTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;
import br.com.infox.epp.test.it.access.crud.LocalizacaoCrudActionIT;

@RunWith(Arquillian.class)
public class NatCatFluxoLocalizacaoCrudActionIT extends AbstractCrudTest<NatCatFluxoLocalizacao>{
    
    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
            .addClasses(
                    LocalizacaoCrudActionIT.class,LocalizacaoCrudAction.class, LocalizacaoManager.class, 
                    LocalizacaoTreeHandler.class, LocalizacaoDAO.class, NaturezaCategoriaFluxoCrudActionIT.class,
                    NaturezaCategoriaFluxoCrudAction.class, FluxoCrudAction.class, 
                    FluxoManager.class, FluxoDAO.class,NaturezaCrudAction.class,
                    CategoriaCrudAction.class,FluxoCrudActionIT.class,
                    NaturezaCrudActionIT.class, NaturezaManager.class,
                    NaturezaDAO.class,CategoriaManager.class,CategoriaDAO.class,
                    NaturezaCategoriaFluxoManager.class, NaturezaCategoriaFluxoDAO.class,
                    CategoriaCrudActionIT.class, ParteProcessoEnum.class,NatCatFluxoLocalizacaoCrudAction.class,
                    NatCatFluxoLocalizacaoManager.class, NatCatFluxoLocalizacaoDAO.class
                    )
        .createDeployment();
    }
    
    public static final List<NatCatFluxoLocalizacao> getSuccessfullyPersisted(final ActionContainer<NatCatFluxoLocalizacao> action, final String suffix,final ServletContext servletContext, final HttpSession session) throws Exception {
        return null;
    }
    
    @Override
    protected String getComponentName() {
        return NatCatFluxoLocalizacaoCrudAction.NAME;
    }

    @Override
    protected void initEntity(final NatCatFluxoLocalizacao entity,
            final CrudActions<NatCatFluxoLocalizacao> crudActions) {
        initEntityAction.setEntity(entity);
        initEntityAction.execute(crudActions);
    }

    private static final ActionContainer<NatCatFluxoLocalizacao> initEntityAction = new ActionContainer<NatCatFluxoLocalizacao>() {
        @Override
        public void execute(CrudActions<NatCatFluxoLocalizacao> crudActions) {
            NatCatFluxoLocalizacao entity = getEntity();
            crudActions.setComponentValue("naturezaCategoriaFluxo", entity.getNaturezaCategoriaFluxo());
            crudActions.setEntityValue("localizacao", entity.getLocalizacao());
            crudActions.setEntityValue("heranca", entity.getHeranca());    
        }
    };
    
    private static final RunnableTest<NatCatFluxoLocalizacao> persistSuccess = new RunnableTest<NatCatFluxoLocalizacao>(NatCatFluxoLocalizacaoCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final NatCatFluxoLocalizacao entity = getEntity(); 
            newInstance();
            initEntityAction.setEntity(entity);
            initEntityAction.execute(this);
            assertEquals("persisted", PERSISTED, save());
        }
    };
    
    @Test
    public void persistSuccessTest() throws Exception {
        final String suffix = "per-suc";
		final List<Localizacao> localizacoes = LocalizacaoCrudActionIT.getSuccessfullyPersisted(null, suffix, servletContext, session);
        final List<NaturezaCategoriaFluxo> natCatFluxoList = NaturezaCategoriaFluxoCrudActionIT.getSuccessfullyPersisted(null, suffix, servletContext, session);
        for (NaturezaCategoriaFluxo naturezaCategoriaFluxo : natCatFluxoList) {
            for (Localizacao localizacao : localizacoes) {
                persistSuccess.runTest(new NatCatFluxoLocalizacao(naturezaCategoriaFluxo, localizacao, Boolean.TRUE), servletContext, session);
            }            
        }
    }
    
    private static final RunnableTest<Localizacao> removeSuccess = new RunnableTest<Localizacao>(NatCatFluxoLocalizacaoCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            
        }
    };
    
    @Test
    public void removeSuccessTest() throws Exception {
        final String suffix = "rem-suc";
		final List<Localizacao> localizacoes = LocalizacaoCrudActionIT.getSuccessfullyPersisted(null, suffix, servletContext, session);
        final List<NaturezaCategoriaFluxo> natCatFluxoList = NaturezaCategoriaFluxoCrudActionIT.getSuccessfullyPersisted(null, suffix, servletContext, session);
        for (NaturezaCategoriaFluxo naturezaCategoriaFluxo : natCatFluxoList) {
            for (Localizacao localizacao : localizacoes) {
                persistSuccess.runTest(new NatCatFluxoLocalizacao(naturezaCategoriaFluxo, localizacao, Boolean.TRUE), servletContext, session);
            }            
        }
        removeSuccess.runTest(servletContext, session);
    }
    
}