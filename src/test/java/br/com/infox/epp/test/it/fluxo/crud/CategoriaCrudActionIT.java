package br.com.infox.epp.test.it.fluxo.crud;

import static br.com.infox.core.action.AbstractAction.UPDATED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.text.MessageFormat.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
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
import br.com.infox.epp.fluxo.crud.CategoriaCrudAction;
import br.com.infox.epp.fluxo.dao.CategoriaDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.manager.CategoriaManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.PersistSuccessTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class CategoriaCrudActionIT extends AbstractCrudTest<Categoria> {

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(CategoriaCrudAction.class,CategoriaDAO.class,CategoriaManager.class)
        		.createDeployment();
    }

    private static final ActionContainer<Categoria> initEntityAction = new ActionContainer<Categoria>() {
        @Override
        public void execute(final CrudActions<Categoria> crudActions) {
            final Categoria entity = getEntity();
            crudActions.setEntityValue("categoria", entity.getCategoria());
            crudActions.setEntityValue("ativo", entity.getAtivo());
        }
    };
    
    @Override
    protected void initEntity(final Categoria entity,
            final CrudActions<Categoria> crudActions) {
        initEntityAction.setEntity(entity);
        initEntityAction.execute(crudActions);
    }

    @Override
    protected String getComponentName() {
        return CategoriaCrudAction.NAME;
    }

    public static final List<Categoria> getSuccessfullyPersisted(final ActionContainer<Categoria> action, final String suffix,final ServletContext servletContext, final HttpSession session) throws Exception {
        final ArrayList<Categoria> categorias = new ArrayList<>();
        final PersistSuccessTest<Categoria> persistSuccessTest = new PersistSuccessTest<>(CategoriaCrudAction.NAME, initEntityAction);
        int i=0;
        for(final Boolean ativo : new Boolean[]{TRUE, FALSE}) {
            categorias.add(persistSuccessTest.runTest(action, new Categoria(format("Categoria {0} {1}", ++i, suffix), ativo), servletContext, session));
        }
        return categorias;
    }
    
    @Test
    public void persistSuccessTest() throws Exception {
        getSuccessfullyPersisted(null, "pers-suc", servletContext, session);
    }

    @Test
    public void persistFailTest() throws Exception {
        int i = 0;
        persistFail.runTest(new Categoria(null, null));
        persistFail.runTest(new Categoria(fillStr(format("categoria-pers-fail-{0}", ++i), LengthConstants.DESCRICAO_PEQUENA + 1), FALSE));
        persistFail.runTest(new Categoria("", FALSE));
        persistFail.runTest(new Categoria(null, FALSE));
        persistFail.runTest(new Categoria(fillStr(format("categoria-pers-fail-{0}", ++i), LengthConstants.DESCRICAO_PEQUENA + 1), TRUE));
        persistFail.runTest(new Categoria("", TRUE));
        persistFail.runTest(new Categoria(null, TRUE));
        persistFail.runTest(new Categoria(fillStr(format("categoria-pers-fail-{0}", ++i), LengthConstants.DESCRICAO_PEQUENA + 1), null));
        persistFail.runTest(new Categoria(format("categoria-pers-fail-{0}", ++i), null));
        persistFail.runTest(new Categoria("", null));
    }

    @Test
    public void inactivateSuccessTest() throws Exception {
        for (int i = 0; i < 20; i++) {
            final String categoria = format("categoria-inac-suc-{0}", i);
            inactivateSuccess.runTest(new Categoria(categoria, TRUE));
        }
    }

    @Test
    public void updateSuccessTest() throws Exception {
        final int i = 0;
        final ActionContainer<Categoria> action = new ActionContainer<Categoria>(new Categoria(format("categoria-upd-suc-{0}", i), TRUE)) {
            public void execute(final CrudActions<Categoria> crudActions) {
                final Integer id = crudActions.getId();
                assertNotNull("id not null", id);
                final Categoria oldEntity = getEntity();
                {
                    crudActions.resetInstance(id);
                    crudActions.setEntityValue("categoria", crudActions.getEntityValue("categoria")
                            + ".changed");
                    assertEquals("updated", UPDATED, crudActions.save());
                    final Categoria newEntity = crudActions.resetInstance(id);
                    assertEquals("categoria differs", false, oldEntity.getCategoria().equals(newEntity.getCategoria()));
                    assertEquals("categoria endsWith .changed", true, newEntity.getCategoria().endsWith(".changed"));
                }
                for (int i = 0; i < 2; i++) {
                    crudActions.setEntityValue("ativo", !oldEntity.getAtivo());
                    assertEquals("updated", UPDATED, crudActions.save());
                    final Categoria newEntity = crudActions.resetInstance(id);
                    assertEquals("ativo differs", false, oldEntity.getAtivo().equals(newEntity.getAtivo()));
                }
            };
        };
        getSuccessfullyPersisted(action, "upd-suc", servletContext, session);
    }
}
