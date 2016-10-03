package br.com.infox.epp.test.it.fluxo.crud;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;

import br.com.infox.constants.LengthConstants;
import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.fluxo.crud.ItemCrudAction;
import br.com.infox.epp.fluxo.dao.ItemDAO;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.manager.ItemManager;
import br.com.infox.epp.fluxo.tree.ItemTreeHandler;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

//@RunWith(Arquillian.class)
public class ItemCrudActionIT extends AbstractCrudTest<Item> {

    private static final String FIELD_ATIVO = "ativo";
    private static final String FIELD_DESCRICAO_ITEM = "descricaoItem";
    private static final String NM_ITEM_PATT = "codigoItem{0}-{1}";
    private static final String FIELD_CODIGO_ITEM = "codigoItem";

    @Deployment
    @OverProtocol(AbstractCrudTest.SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(ItemCrudAction.class,
                ItemTreeHandler.class, ItemDAO.class, ItemManager.class)
                .createDeployment();
    }

    private static final ActionContainer<Item> initEntityAction = new ActionContainer<Item>() {
        @Override
        public void execute(final CrudActions<Item> crudActions) {
            final Item entity = getEntity();
            crudActions.setEntityValue(ItemCrudActionIT.FIELD_CODIGO_ITEM,
                    entity.getCodigoItem());// *
            crudActions.setEntityValue(ItemCrudActionIT.FIELD_DESCRICAO_ITEM,
                    entity.getDescricaoItem());// *
            crudActions.setEntityValue("itemPai", entity.getItemPai());
            crudActions.setEntityValue(ItemCrudActionIT.FIELD_ATIVO,
                    entity.getAtivo());
        }
    };

    @Override
    protected ActionContainer<Item> getInitEntityAction() {
        return ItemCrudActionIT.initEntityAction;
    }

    @Override
    protected String getComponentName() {
        return ItemCrudAction.NAME;
    }

    //@Test
    public void persistSuccessTest() throws Exception {
        final ArrayList<Item> itens = new ArrayList<>();
        int id = 0;
        for (final Boolean ativo : new Boolean[] { Boolean.TRUE, Boolean.FALSE }) {
            for (int i = 0; i < 10; i++) {
                Item randomPai = null;
                if (itens.size() > 0) {
                    final int index = (int) (itens.size() * Math.random() * 2);
                    if (index < itens.size()) {
                        randomPai = itens.get(index);
                    }
                }
                final String codItem = MessageFormat.format(
                        ItemCrudActionIT.NM_ITEM_PATT, ++id, "per-suc");
                itens.add(this.persistSuccess.runTest(new Item(codItem,
                        codItem, randomPai, ativo), this.servletContext,
                        this.session));
            }
        }
    }

    //@Test
    public void persistFailTest() throws Exception {
        final String baseCode = "codigoItem-per-fail";

        for (final Boolean ativo : new Boolean[] { Boolean.TRUE, Boolean.FALSE }) {
            for (final String codigo : new String[] { "", null,
                    fillStr(baseCode, LengthConstants.DESCRICAO_PEQUENA + 1) }) {
                this.persistFail.runTest(
                        new Item(codigo, baseCode, null, ativo),
                        this.servletContext, this.session);
            }

            for (final String descricao : new String[] { "", null,
                    fillStr(baseCode, LengthConstants.DESCRICAO_PADRAO + 1) }) {
                this.persistFail.runTest(new Item(baseCode, descricao, null,
                        ativo), this.servletContext, this.session);
            }

            this.persistFail.runTest(new Item(baseCode, baseCode, new Item("d",
                    "s", null, ativo), ativo), this.servletContext,
                    this.session);
        }
    }

    //@Test
    public void inactivateSuccessTest() throws Exception {
        final ArrayList<Item> itens = new ArrayList<>();
        int id = 0;
        for (int i = 0; i < 10; i++) {
            Item randomPai = null;
            if (itens.size() > 0) {
                final int index = (int) (itens.size() * Math.random() * 2);
                if (index < itens.size()) {
                    final Item itemPai = itens.get(index);
                    randomPai = itemPai.getAtivo() ? itemPai : null;
                }
            }
            final String codItem = MessageFormat.format(
                    ItemCrudActionIT.NM_ITEM_PATT, ++id, "inac-suc");
            itens.add(this.inactivateSuccess
                    .runTest(
                            new Item(codItem, codItem, randomPai, Boolean.TRUE),
                            this.servletContext, this.session));
        }
    }

    //@Test
    public void updateFailTest() throws Exception {
        final ActionContainer<Item> updateFailAction = new ActionContainer<Item>() {
            @Override
            public void execute(final CrudActions<Item> crudActions) {
                final Object id = crudActions.getId();
                Assert.assertNotNull("id not null", id);

                final Item entity = getEntity();
                for (final String codigo : new String[] {
                        "",
                        null,
                        fillStr(crudActions.getEntityValue(ItemCrudActionIT.FIELD_CODIGO_ITEM)
                                + ".updated",
                                LengthConstants.DESCRICAO_PEQUENA + 1) }) {
                    final String codigoItem = entity.getCodigoItem();
                    Assert.assertEquals("codigo equals", Boolean.TRUE, Boolean
                            .valueOf(codigoItem.equals(crudActions
                                    .resetInstance(id).getCodigoItem())));
                    crudActions.setEntityValue(
                            ItemCrudActionIT.FIELD_CODIGO_ITEM, codigo);
                    Assert.assertEquals(AbstractAction.UPDATED, Boolean.FALSE,
                            AbstractAction.UPDATED.equals(crudActions.save()));
                    Assert.assertEquals("codigo not differs", Boolean.TRUE,
                            Boolean.valueOf(codigoItem.equals(crudActions
                                    .resetInstance(id).getCodigoItem())));
                }

                for (final String descricao : new String[] {
                        "",
                        null,
                        fillStr(crudActions.getEntityValue(ItemCrudActionIT.FIELD_DESCRICAO_ITEM)
                                + ".updated",
                                LengthConstants.DESCRICAO_PADRAO + 1) }) {
                    final String descricaoItem = entity.getDescricaoItem();
                    Assert.assertEquals("descricao equals", Boolean.TRUE,
                            Boolean.valueOf(descricaoItem.equals(crudActions
                                    .resetInstance(id).getDescricaoItem())));
                    crudActions.setEntityValue(
                            ItemCrudActionIT.FIELD_DESCRICAO_ITEM, descricao);
                    Assert.assertEquals(AbstractAction.UPDATED, Boolean.FALSE,
                            AbstractAction.UPDATED.equals(crudActions.save()));
                    Assert.assertEquals("descricao not differs", Boolean.TRUE,
                            Boolean.valueOf(descricaoItem.equals(crudActions
                                    .resetInstance(id).getDescricaoItem())));
                }
            }
        };
        final ArrayList<Item> itens = new ArrayList<>();
        int id = 0;
        for (final Boolean ativo : new Boolean[] { Boolean.TRUE, Boolean.FALSE }) {
            for (int i = 0; i < 10; i++) {
                Item randomPai = null;
                if (itens.size() > 0) {
                    final int index = (int) (itens.size() * Math.random() * 2);
                    if (index < itens.size()) {
                        randomPai = itens.get(index);
                    }
                }
                final String codItem = MessageFormat.format(
                        ItemCrudActionIT.NM_ITEM_PATT, ++id, "upd-fail");
                itens.add(this.persistSuccess.runTest(updateFailAction,
                        new Item(codItem, codItem, randomPai, ativo),
                        this.servletContext, this.session));
            }
        }
    }

    //@Test
    public void updateSuccessTest() throws Exception {
        final ActionContainer<Item> updateAction = new ActionContainer<Item>() {
            @Override
            public void execute(final CrudActions<Item> crudActions) {
                final Object id = crudActions.getId();
                Assert.assertNotNull("id not null", id);
                Item instance = crudActions.resetInstance(id);

                final Item entity = getEntity();
                final String codigoItem = entity.getCodigoItem();
                Assert.assertEquals("codigo equals", Boolean.TRUE, Boolean
                        .valueOf(codigoItem.equals(instance.getCodigoItem())));

                crudActions
                        .setEntityValue(
                                ItemCrudActionIT.FIELD_CODIGO_ITEM,
                                crudActions
                                        .getEntityValue(ItemCrudActionIT.FIELD_CODIGO_ITEM)
                                        + ".updated");

                Assert.assertEquals(AbstractAction.UPDATED,
                        AbstractAction.UPDATED, crudActions.save());
                instance = crudActions.resetInstance(id);
                Assert.assertEquals(
                        "codigo updated",
                        Boolean.TRUE,
                        ((String) crudActions
                                .getEntityValue(ItemCrudActionIT.FIELD_CODIGO_ITEM))
                                .endsWith(".updated"));
                Assert.assertEquals("codigo differs", Boolean.FALSE, Boolean
                        .valueOf(codigoItem.equals(instance.getCodigoItem())));
                // -----
                final String descricaoItem = entity.getDescricaoItem();
                Assert.assertEquals("descricao equals", Boolean.TRUE, Boolean
                        .valueOf(descricaoItem.equals(instance
                                .getDescricaoItem())));

                crudActions
                        .setEntityValue(
                                ItemCrudActionIT.FIELD_DESCRICAO_ITEM,
                                crudActions
                                        .getEntityValue(ItemCrudActionIT.FIELD_DESCRICAO_ITEM)
                                        + ".updated");

                Assert.assertEquals(AbstractAction.UPDATED,
                        AbstractAction.UPDATED, crudActions.save());
                instance = crudActions.resetInstance(id);
                Assert.assertEquals(
                        "descricao updated",
                        Boolean.TRUE,
                        ((String) crudActions
                                .getEntityValue(ItemCrudActionIT.FIELD_DESCRICAO_ITEM))
                                .endsWith(".updated"));
                Assert.assertEquals("descricao differs", Boolean.FALSE, Boolean
                        .valueOf(descricaoItem.equals(instance
                                .getDescricaoItem())));
            }
        };
        final ArrayList<Item> itens = new ArrayList<>();
        int id = 0;
        for (final Boolean ativo : new Boolean[] { Boolean.TRUE, Boolean.FALSE }) {
            for (int i = 0; i < 10; i++) {
                Item randomPai = null;
                if (itens.size() > 0) {
                    final int index = (int) (itens.size() * Math.random() * 2);
                    if (index < itens.size()) {
                        randomPai = itens.get(index);
                    }
                }
                final String codItem = MessageFormat.format(
                        ItemCrudActionIT.NM_ITEM_PATT, ++id, "upd-suc");
                itens.add(this.persistSuccess.runTest(updateAction, new Item(
                        codItem, codItem, randomPai, ativo),
                        this.servletContext, this.session));
            }
        }
    }

}
