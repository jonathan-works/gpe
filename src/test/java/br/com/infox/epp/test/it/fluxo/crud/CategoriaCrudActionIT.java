package br.com.infox.epp.test.it.fluxo.crud;

import static br.com.infox.core.action.AbstractAction.UPDATED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.text.MessageFormat.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class CategoriaCrudActionIT extends AbstractCrudTest<Categoria> {

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(CategoriaCrudAction.class,CategoriaDAO.class,CategoriaManager.class)
        		.createDeployment();
    }

    @Override
    protected void initEntity(final Categoria entity,
            final CrudActions<Categoria> crudActions) {
        crudActions.setEntityValue("categoria", entity.getCategoria());
        crudActions.setEntityValue("ativo", entity.getAtivo());
    }

    @Override
    protected String getComponentName() {
        return CategoriaCrudAction.NAME;
    }

    @Test
    public void persistSuccessTest() throws Exception {
        for (int i = 0; i < 20; i++) {
            final String categoria = format("categoria-pers-suc-{0}", i);
            persistSuccess.runTest(new Categoria(categoria, i % 2 == 0 ? TRUE : FALSE));
        }
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
        persistSuccess.runTest(new ActionContainer<Categoria>(new Categoria(format("categoria-upd-suc-{0}", i), TRUE)) {
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
        });
    }
}
