package br.com.infox.epp.test.it.fluxo.crud;

import java.util.HashMap;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.fluxo.crud.CategoriaCrudAction;
import br.com.infox.epp.fluxo.crud.CategoriaItemCrudAction;
import br.com.infox.epp.fluxo.crud.ItemCrudAction;
import br.com.infox.epp.fluxo.dao.CategoriaDAO;
import br.com.infox.epp.fluxo.dao.CategoriaItemDAO;
import br.com.infox.epp.fluxo.dao.ItemDAO;
import br.com.infox.epp.fluxo.entity.CategoriaItem;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.manager.CategoriaItemManager;
import br.com.infox.epp.fluxo.manager.CategoriaManager;
import br.com.infox.epp.fluxo.manager.ItemManager;
import br.com.infox.epp.fluxo.tree.ItemTreeHandler;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.RunnableTest;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

//@RunWith(Arquillian.class)
public class CategoriaItemCrudActionIT extends AbstractCrudTest<CategoriaItem> {

    @Deployment
    @OverProtocol(AbstractCrudTest.SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(
                CategoriaItemCrudAction.class, ItemManager.class,
                ItemTreeHandler.class, ItemCrudAction.class,
                CategoriaCrudAction.class, CategoriaItemManager.class,
                CategoriaItemDAO.class, CategoriaDAO.class,
                CategoriaManager.class, ItemDAO.class, CategoriaItemDAO.class,
                CategoriaItemManager.class).createDeployment();
    }

    @Override
    protected String getComponentName() {
        return null;
    }

    public static final ActionContainer<CategoriaItem> initEntityAction = new ActionContainer<CategoriaItem>() {
        @Override
        public void execute(final CrudActions<CategoriaItem> actions) {
            final CategoriaItem entity = getEntity();
            actions.setComponentValue("categoria", entity.getCategoria());
            actions.setComponentValue("item", entity.getItem());
        }
    };

    @Override
    protected ActionContainer<CategoriaItem> getInitEntityAction() {
        return CategoriaItemCrudActionIT.initEntityAction;
    }

    private final RunnableTest<Item> persistItem = new RunnableTest<Item>(
            null) {
        @Override
        protected void testComponent() throws Exception {
            final Item entity = getEntity();

            newInstance();
            setEntityValue("codigoItem", entity.getCodigoItem());
            setEntityValue("descricaoItem", entity.getDescricaoItem());
            setEntityValue("itemPai", entity.getItemPai());
            setEntityValue("ativo", entity.getAtivo());

            Assert.assertEquals(AbstractAction.PERSISTED,
                    AbstractAction.PERSISTED, save());
            final Integer id = getId();
            Assert.assertNotNull("id not null", id);

            setEntity(resetInstance(id));
        }
    };

    private final HashMap<String, Item> itens = new HashMap<>();

    private CategoriaItemCrudActionIT addToMap(final Item... itens) {
        for (final Item item : itens) {
            this.itens.put(item.getCodigoItem(), item);
        }
        return this;
    }

    private void inicializaItens(final String suffix) throws Exception {
        final Item perifericos = this.persistItem.runTest(new Item("perifComp"
                + suffix, "Periféricos de Computador" + suffix, Boolean.TRUE),
                this.servletContext, this.session);
        final Item logitech = this.persistItem.runTest(new Item("logitech"
                + suffix, "Logitech" + suffix, perifericos, Boolean.TRUE),
                this.servletContext, this.session);
        final Item leadership = this.persistItem.runTest(new Item("leadership"
                + suffix, "Leadership" + suffix, perifericos, Boolean.TRUE),
                this.servletContext, this.session);
        final Item computadores = this.persistItem.runTest(new Item("comp"
                + suffix, "Computadores" + suffix, Boolean.TRUE),
                this.servletContext, this.session);
        final Item notbook = this.persistItem.runTest(new Item("notbook"
                + suffix, "Notebook" + suffix, computadores, Boolean.TRUE),
                this.servletContext, this.session);
        final Item dell = this.persistItem.runTest(new Item("dell" + suffix,
                "Dell" + suffix, notbook, Boolean.TRUE), this.servletContext,
                this.session);

        addToMap(perifericos, logitech, leadership, computadores, notbook,
                dell, this.persistItem.runTest(new Item("microsoft" + suffix,
                        "Microsoft" + suffix, perifericos, Boolean.FALSE),
                        this.servletContext, this.session),
                this.persistItem.runTest(new Item("ultbook" + suffix,
                        "Ultrabook" + suffix, computadores, Boolean.FALSE),
                        this.servletContext, this.session),
                this.persistItem.runTest(new Item("mcbook" + suffix, "Macbook"
                        + suffix, computadores, Boolean.FALSE),
                        this.servletContext, this.session),
                this.persistItem.runTest(new Item("hp" + suffix,
                        "Hewlett Packard" + suffix, notbook, Boolean.FALSE),
                        this.servletContext, this.session),
                this.persistItem.runTest(new Item("clamSh0979" + suffix,
                        "Mouse Clamshell 0979 USB" + suffix, leadership,
                        Boolean.TRUE), this.servletContext, this.session),
                this.persistItem.runTest(new Item("mgc2022" + suffix,
                        "Mouse Magic 2022 USB" + suffix, leadership,
                        Boolean.TRUE), this.servletContext, this.session),
                this.persistItem.runTest(new Item("m600" + suffix,
                        "Mouse Touch M600" + suffix, logitech, Boolean.TRUE),
                        this.servletContext, this.session),
                this.persistItem.runTest(
                        new Item("m187" + suffix, "Mouse Wireless M187"
                                + suffix, logitech, Boolean.TRUE),
                        this.servletContext, this.session),

                this.persistItem.runTest(new Item("inspiron1525" + suffix,
                        "Inspiron 1525" + suffix, dell, Boolean.TRUE),
                        this.servletContext, this.session),
                this.persistItem.runTest(new Item("inspiron14" + suffix,
                        "Inspiron 14" + suffix, dell, Boolean.TRUE),
                        this.servletContext, this.session));
    }

}
