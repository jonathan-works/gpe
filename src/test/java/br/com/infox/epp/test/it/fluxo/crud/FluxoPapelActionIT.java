package br.com.infox.epp.test.it.fluxo.crud;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.access.api.RolesMap;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.crud.PapelCrudAction;
import br.com.infox.epp.access.dao.PapelDAO;
import br.com.infox.epp.access.dao.RecursoDAO;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.RecursoManager;
import br.com.infox.epp.fluxo.crud.FluxoCrudAction;
import br.com.infox.epp.fluxo.crud.FluxoPapelAction;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.dao.FluxoPapelDAO;
import br.com.infox.epp.fluxo.entity.FluxoPapel;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.FluxoPapelManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.RunnableTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

//@RunWith(Arquillian.class)
public class FluxoPapelActionIT extends AbstractCrudTest<FluxoPapel> {

    @Deployment
    @OverProtocol(AbstractCrudTest.SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(FluxoPapelAction.class,
                PapelTreeHandler.class, FluxoPapelManager.class,
                FluxoPapelDAO.class, PapelCrudAction.class, RolesMap.class,
                PapelManager.class, RecursoManager.class, PapelDAO.class,
                RecursoDAO.class, FluxoCrudAction.class, FluxoManager.class,
                FluxoDAO.class, FluxoCrudActionIT.class).createDeployment();
    }

    @Override
    protected String getComponentName() {
        return FluxoPapelAction.NAME;
    }

    public static final ActionContainer<FluxoPapel> initEntityAction = new ActionContainer<FluxoPapel>() {
        @Override
        public void execute(final CrudActions<FluxoPapel> crud) {
            final FluxoPapel entity = getEntity();
            crud.invokeMethod("init", Void.class, entity.getFluxo());
            crud.setEntityValue("papel", entity.getPapel());
        }
    };

    @Override
    protected ActionContainer<FluxoPapel> getInitEntityAction() {
        return FluxoPapelActionIT.initEntityAction;
    }

    private static final RunnableTest<FluxoPapel> persistSuccess = new RunnableTest<FluxoPapel>(
            FluxoPapelAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final FluxoPapel entity = getEntity();
            newInstance();
            FluxoPapelActionIT.initEntityAction.execute(entity, this);
            Assert.assertEquals("persist failed", AbstractAction.PERSISTED,
                    save());

            final Integer id = getId();
            Assert.assertNull("id not null", id);
        }
    };

    private final RunnableTest<FluxoPapel> removeSuccess = new RunnableTest<FluxoPapel>(
            FluxoPapelAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final FluxoPapel entity = getEntity();
            invokeMethod("init", Void.class, entity.getFluxo());
            List<FluxoPapel> list = getComponentValue("fluxoPapelList");
            final List<FluxoPapel> staticList = new ArrayList<>(list);
            for (final FluxoPapel fluxoPapel : staticList) {
                final String remove = remove(fluxoPapel);
                Assert.assertEquals("failed at remove(obj)",
                        AbstractAction.REMOVED, remove);
            }
            list = getComponentValue("fluxoPapelList");
            for (final FluxoPapel fluxoPapel : staticList) {
                Assert.assertFalse("object still exists in list",
                        list.contains(fluxoPapel));
            }
        }
    };

    private final RunnableTest<FluxoPapel> removeFail = new RunnableTest<FluxoPapel>(
            FluxoPapelAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final FluxoPapel entity = getEntity();
            invokeMethod("init", Void.class, entity.getFluxo());
            List<FluxoPapel> list = getComponentValue("fluxoPapelList");
            final List<FluxoPapel> staticList = new ArrayList<>(list);
            for (final FluxoPapel fluxoPapel : staticList) {
                final String remove = remove(fluxoPapel);
                Assert.assertFalse("succeeded at remove(obj)",
                        AbstractAction.REMOVED.equals(remove));
            }
            list = getComponentValue("fluxoPapelList");
            for (final FluxoPapel fluxoPapel : staticList) {
                Assert.assertTrue("object doesn't exist in list",
                        list.contains(fluxoPapel));
            }
        }
    };
}
