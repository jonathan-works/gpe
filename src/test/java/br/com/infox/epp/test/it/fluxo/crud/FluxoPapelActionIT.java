package br.com.infox.epp.test.it.fluxo.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.REMOVED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.epp.access.api.RolesMap;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.crud.PapelCrudAction;
import br.com.infox.epp.access.dao.PapelDAO;
import br.com.infox.epp.access.dao.RecursoDAO;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.RecursoManager;
import br.com.infox.epp.fluxo.crud.FluxoCrudAction;
import br.com.infox.epp.fluxo.crud.FluxoPapelAction;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.dao.FluxoPapelDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.FluxoPapel;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.FluxoPapelManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.RunnableTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;
import br.com.infox.epp.test.it.access.crud.PapelCrudActionIT;

@RunWith(Arquillian.class)
public class FluxoPapelActionIT extends AbstractCrudTest<FluxoPapel> {
    
    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup()
        .addClasses(FluxoPapelAction.class, PapelTreeHandler.class, FluxoPapelManager.class, FluxoPapelDAO.class,
                PapelCrudAction.class,RolesMap.class,PapelManager.class,RecursoManager.class,PapelDAO.class,
                RecursoDAO.class,FluxoCrudAction.class, FluxoManager.class, FluxoDAO.class, PapelCrudActionIT.class,
                FluxoCrudActionIT.class)
        .createDeployment();
    }
    
    @Override
    protected String getComponentName() {
        return FluxoPapelAction.NAME;
    }

    public static final ActionContainer<FluxoPapel> initEntityAction = new ActionContainer<FluxoPapel>() {
        @Override
        public void execute(CrudActions<FluxoPapel> crud) {
            final FluxoPapel entity = getEntity();
            crud.invokeMethod("init",Void.class, entity.getFluxo());
            crud.setEntityValue("papel", entity.getPapel());
        }
    };
    
    @Override
    protected ActionContainer<FluxoPapel> getInitEntityAction() {
        return initEntityAction;
    }
    
    private static final RunnableTest<FluxoPapel> persistSuccess = new RunnableTest<FluxoPapel>(FluxoPapelAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final FluxoPapel entity= getEntity();
            newInstance();
            initEntityAction.execute(entity, this);
            assertEquals("persist failed", PERSISTED, save());

            final Integer id = getId();
            assertNull("id not null", id);
        }
    };
    
    @Test
    public void persistSuccessTest() throws Exception {
        final String suffix = "perSuccess";
        final List<Papel> papeis = PapelCrudActionIT.getSuccessFullyPersisted(null, suffix, servletContext, session);
        final List<Fluxo> fluxos = FluxoCrudActionIT.getSuccessfullyPersisted(null, suffix, servletContext, session);
        
        for (final Fluxo fluxo : fluxos) {
            for (final Papel papel : papeis) {
                persistSuccess.runTest(new FluxoPapel(fluxo, papel), servletContext, session);        
            }
        }
    }
    
    @Test
    public void persistFailTest() throws Exception {
        final String suffix = "perFail";
        final List<Papel> papeis = PapelCrudActionIT.getSuccessFullyPersisted(null, suffix, servletContext, session);
        final List<Fluxo> fluxos = FluxoCrudActionIT.getSuccessfullyPersisted(null, suffix, servletContext, session);
        
        for (final Fluxo fluxo : fluxos) {
            for (final Papel papel : papeis) {
                persistSuccess.runTest(new FluxoPapel(fluxo, papel), servletContext, session);
                persistFail.runTest(new FluxoPapel(fluxo, papel), servletContext, session);
            }
        }
    }
    
    @Test
    public void removeSuccessTest() throws Exception {
        final String suffix = "removeSucc";
        final List<Papel> papeis = PapelCrudActionIT.getSuccessFullyPersisted(null, suffix, servletContext, session);
        final List<Fluxo> fluxos = FluxoCrudActionIT.getSuccessfullyPersisted(null, suffix, servletContext, session);
        
        for (final Fluxo fluxo : fluxos) {
            for (final Papel papel : papeis) {
                persistSuccess.runTest(new FluxoPapel(fluxo, papel), servletContext, session);
            }
            removeSuccess.runTest(new FluxoPapel(fluxo, null), servletContext, session);
        }
    }
    
    @Test
    public void removeFailTest() throws Exception {
        final String suffix = "removeFail";
        
        final List<Papel> papeis = PapelCrudActionIT.getSuccessFullyPersisted(null, suffix, servletContext, session);
        final List<Fluxo> fluxos = FluxoCrudActionIT.getSuccessfullyPersisted(null, suffix, servletContext, session);
        
        for (final Fluxo fluxo : fluxos) {
            for (final Papel papel : papeis) {
                persistSuccess.runTest(new FluxoPapel(fluxo, papel), servletContext, session);
            }
            final FluxoPapel entity = new FluxoPapel(fluxo, null);
            removeSuccess.runTest(entity, servletContext, session);
            removeFail.runTest(entity, servletContext, session);
        }
    }
    
    private final RunnableTest<FluxoPapel> removeSuccess = new RunnableTest<FluxoPapel>(FluxoPapelAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final FluxoPapel entity = getEntity();
            invokeMethod("init",Void.class, entity.getFluxo());
            List<FluxoPapel> list = getComponentValue("fluxoPapelList");
            final List<FluxoPapel> staticList = new ArrayList<>(list);
            for (final FluxoPapel fluxoPapel : staticList) {
                final String remove = remove(fluxoPapel);
                assertEquals("failed at remove(obj)", REMOVED, remove);
            }
            list = getComponentValue("fluxoPapelList");
            for (FluxoPapel fluxoPapel : staticList) {
                assertFalse("object still exists in list",list.contains(fluxoPapel));
            }
        }
    };
    
    private final RunnableTest<FluxoPapel> removeFail = new RunnableTest<FluxoPapel>(FluxoPapelAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final FluxoPapel entity = getEntity();
            invokeMethod("init",Void.class, entity.getFluxo());
            List<FluxoPapel> list = getComponentValue("fluxoPapelList");
            final List<FluxoPapel> staticList = new ArrayList<>(list);
            for (final FluxoPapel fluxoPapel : staticList) {
                final String remove = remove(fluxoPapel);
                assertFalse("succeeded at remove(obj)", REMOVED.equals(remove));
            }
            list = getComponentValue("fluxoPapelList");
            for (FluxoPapel fluxoPapel : staticList) {
                assertTrue("object doesn't exist in list",list.contains(fluxoPapel));
            }
        }
    };
}
