package br.com.infox.epp.test.it.fluxo.crud;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.shrinkwrap.api.spec.WebArchive;

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
        return null;//FluxoPapelAction.NAME;
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

}
