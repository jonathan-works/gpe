package br.com.infox.epp.test.it.fluxo.crud;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;

import br.com.infox.constants.LengthConstants;
import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.fluxo.crud.CategoriaCrudAction;
import br.com.infox.epp.fluxo.dao.CategoriaDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.manager.CategoriaManager;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.PersistSuccessTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

//@RunWith(Arquillian.class)
public class CategoriaCrudActionIT extends AbstractCrudTest<Categoria> {

    @Deployment
    @OverProtocol(AbstractCrudTest.SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(
                CategoriaCrudAction.class, CategoriaDAO.class,
                CategoriaManager.class).createDeployment();
    }

    public static final ActionContainer<Categoria> initEntityAction = new ActionContainer<Categoria>() {
        @Override
        public void execute(final CrudActions<Categoria> crudActions) {
            final Categoria entity = getEntity();
            crudActions.setEntityValue("categoria", entity.getCategoria());
            crudActions.setEntityValue("ativo", entity.getAtivo());
        }
    };

    @Override
    protected ActionContainer<Categoria> getInitEntityAction() {
        return CategoriaCrudActionIT.initEntityAction;
    }

    @Override
    protected String getComponentName() {
        return CategoriaCrudAction.NAME;
    }

    public static final List<Categoria> getSuccessfullyPersisted(
            final ActionContainer<Categoria> action, final String suffix,
            final ServletContext servletContext, final HttpSession session)
            throws Exception {
        final ArrayList<Categoria> categorias = new ArrayList<>();
        final PersistSuccessTest<Categoria> persistSuccessTest = new PersistSuccessTest<>(
                CategoriaCrudAction.NAME,
                CategoriaCrudActionIT.initEntityAction);
        int i = 0;
        for (final Boolean ativo : new Boolean[] { Boolean.TRUE, Boolean.FALSE }) {
            categorias.add(persistSuccessTest.runTest(action, new Categoria(
                    MessageFormat.format("Categoria {0} {1}", ++i, suffix),
                    ativo), servletContext, session));
        }
        return categorias;
    }

    //@Test
    public void persistSuccessTest() throws Exception {
        CategoriaCrudActionIT.getSuccessfullyPersisted(null, "pers-suc",
                this.servletContext, this.session);
    }

    //@Test
    public void persistFailTest() throws Exception {
        int i = 0;
        this.persistFail.runTest(new Categoria(null, null),
                this.servletContext, this.session);
        this.persistFail.runTest(
                new Categoria(fillStr(
                        MessageFormat.format("categoria-pers-fail-{0}", ++i),
                        LengthConstants.DESCRICAO_PEQUENA + 1), Boolean.FALSE),
                this.servletContext, this.session);
        this.persistFail.runTest(new Categoria("", Boolean.FALSE),
                this.servletContext, this.session);
        this.persistFail.runTest(new Categoria(null, Boolean.FALSE),
                this.servletContext, this.session);
        this.persistFail.runTest(
                new Categoria(fillStr(
                        MessageFormat.format("categoria-pers-fail-{0}", ++i),
                        LengthConstants.DESCRICAO_PEQUENA + 1), Boolean.TRUE),
                this.servletContext, this.session);
        this.persistFail.runTest(new Categoria("", Boolean.TRUE),
                this.servletContext, this.session);
        this.persistFail.runTest(new Categoria(null, Boolean.TRUE),
                this.servletContext, this.session);
        this.persistFail.runTest(
                new Categoria(fillStr(
                        MessageFormat.format("categoria-pers-fail-{0}", ++i),
                        LengthConstants.DESCRICAO_PEQUENA + 1), null),
                this.servletContext, this.session);
        this.persistFail.runTest(
                new Categoria(MessageFormat.format("categoria-pers-fail-{0}",
                        ++i), null), this.servletContext, this.session);
        this.persistFail.runTest(new Categoria("", null), this.servletContext,
                this.session);
    }

    //@Test
    public void inactivateSuccessTest() throws Exception {
        for (int i = 0; i < 20; i++) {
            final String categoria = MessageFormat.format(
                    "categoria-inac-suc-{0}", i);
            this.inactivateSuccess.runTest(new Categoria(categoria,
                    Boolean.TRUE), this.servletContext, this.session);
        }
    }

    //@Test
    public void updateSuccessTest() throws Exception {
        final int i = 0;
        final ActionContainer<Categoria> action = new ActionContainer<Categoria>(
                new Categoria(MessageFormat.format("categoria-upd-suc-{0}", i),
                        Boolean.TRUE)) {
            @Override
            public void execute(final CrudActions<Categoria> crudActions) {
                final Integer id = crudActions.getId();
                Assert.assertNotNull("id not null", id);
                final Categoria oldEntity = getEntity();
                {
                    crudActions.resetInstance(id);
                    crudActions.setEntityValue("categoria",
                            crudActions.getEntityValue("categoria")
                                    + ".changed");
                    Assert.assertEquals("updated", AbstractAction.UPDATED,
                            crudActions.save());
                    final Categoria newEntity = crudActions.resetInstance(id);
                    Assert.assertEquals("categoria differs", false, oldEntity
                            .getCategoria().equals(newEntity.getCategoria()));
                    Assert.assertEquals("categoria endsWith .changed", true,
                            newEntity.getCategoria().endsWith(".changed"));
                }
                for (int i = 0; i < 2; i++) {
                    crudActions.setEntityValue("ativo", !oldEntity.getAtivo());
                    Assert.assertEquals("updated", AbstractAction.UPDATED,
                            crudActions.save());
                    final Categoria newEntity = crudActions.resetInstance(id);
                    Assert.assertEquals("ativo differs", false, oldEntity
                            .getAtivo().equals(newEntity.getAtivo()));
                }
            };
        };
        CategoriaCrudActionIT.getSuccessfullyPersisted(action, "upd-suc",
                this.servletContext, this.session);
    }
}
