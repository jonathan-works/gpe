package br.com.infox.epp.test.it.fluxo.crud;

import static br.com.infox.core.action.AbstractAction.UPDATED;
import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PEQUENA;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.text.MessageFormat.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.epp.fluxo.crud.ItemCrudAction;
import br.com.infox.epp.fluxo.dao.ItemDAO;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.manager.ItemManager;
import br.com.infox.epp.fluxo.tree.ItemTreeHandler;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class ItemCrudActionIT extends AbstractCrudTest<Item> {

    private static final String FIELD_ATIVO = "ativo";
    private static final String FIELD_DESCRICAO_ITEM = "descricaoItem";
    private static final String NM_ITEM_PATT = "codigoItem{0}-{1}";
    private static final String FIELD_CODIGO_ITEM = "codigoItem";

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(ItemCrudAction.class,ItemTreeHandler.class,ItemDAO.class,ItemManager.class)
        		.createDeployment();
    }

    @Override
    protected void initEntity(final Item entity,
            final CrudActions<Item> crudActions) {
        crudActions.setEntityValue(FIELD_CODIGO_ITEM, entity.getCodigoItem());// *
        crudActions.setEntityValue(FIELD_DESCRICAO_ITEM, entity.getDescricaoItem());// *
        crudActions.setEntityValue("itemPai", entity.getItemPai());
        crudActions.setEntityValue(FIELD_ATIVO, entity.getAtivo());
    }

    @Override
    protected String getComponentName() {
        return ItemCrudAction.NAME;
    }

    @Test
    public void persistSuccessTest() throws Exception {
        final ArrayList<Item> itens = new ArrayList<>();
        int id = 0;
        for (final Boolean ativo : new Boolean[]{TRUE, FALSE} ) {
            for (int i = 0; i < 10; i++) {
                Item randomPai = null;
                if (itens.size() > 0) {
                    final int index = (int) (itens.size() * Math.random() * 2);
                    if (index < itens.size()) {
                        randomPai = itens.get(index);
                    }
                }
                final String codItem = format(NM_ITEM_PATT, ++id, "per-suc");
                itens.add(persistSuccess.runTest(new Item(codItem, codItem, randomPai, ativo)));
            }
        }
    }
    
    @Test
    public void persistFailTest() throws Exception {
        final String baseCode = "codigoItem-per-fail";
        
        for (final Boolean ativo : new Boolean[]{TRUE, FALSE} ) {
            for (final String codigo : new String[]{"", null, fillStr(baseCode, DESCRICAO_PEQUENA+1)}) {
                persistFail.runTest(new Item(codigo, baseCode, null, ativo));
            }
            
            for (final String descricao: new String[]{"", null, fillStr(baseCode, DESCRICAO_PADRAO+1)}) {
                persistFail.runTest(new Item(baseCode, descricao, null, ativo));
            }
        
            persistFail.runTest(new Item(baseCode, baseCode, new Item("d","s",null,ativo), ativo));
        }
    }
    
    @Test
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
            final String codItem = format(NM_ITEM_PATT, ++id, "inac-suc");
            itens.add(inactivateSuccess.runTest(new Item(codItem, codItem, randomPai, TRUE)));
        }
    }
    
    @Test
    public void updateFailTest() throws Exception {
        final ActionContainer<Item> updateFailAction = new ActionContainer<Item>() {
            @Override
            public void execute(final CrudActions<Item> crudActions) {
                final Object id = crudActions.getId();
                assertNotNull("id not null",id);

                final Item entity = getEntity();
                for (final String codigo : new String[]{"", null, fillStr(crudActions.getEntityValue(FIELD_CODIGO_ITEM)+".updated", DESCRICAO_PEQUENA+1)}) {
                    final String codigoItem = entity.getCodigoItem();
                    assertEquals("codigo equals", TRUE, Boolean.valueOf(codigoItem.equals(crudActions.resetInstance(id).getCodigoItem())));
                    crudActions.setEntityValue(FIELD_CODIGO_ITEM, codigo);
                    assertEquals(UPDATED, FALSE, UPDATED.equals(crudActions.save()));
                    assertEquals("codigo not differs", TRUE, Boolean.valueOf(codigoItem.equals(crudActions.resetInstance(id).getCodigoItem())));
                }
                
                for (final String descricao: new String[]{"", null, fillStr(crudActions.getEntityValue(FIELD_DESCRICAO_ITEM)+".updated", DESCRICAO_PADRAO+1)}) {
                    final String descricaoItem = entity.getDescricaoItem();
                    assertEquals("descricao equals", TRUE, Boolean.valueOf(descricaoItem.equals(crudActions.resetInstance(id).getDescricaoItem())));
                    crudActions.setEntityValue(FIELD_DESCRICAO_ITEM, descricao);
                    assertEquals(UPDATED, FALSE, UPDATED.equals(crudActions.save()));
                    assertEquals("descricao not differs", TRUE, Boolean.valueOf(descricaoItem.equals(crudActions.resetInstance(id).getDescricaoItem())));
                }
            }
        };
        final ArrayList<Item> itens = new ArrayList<>();
        int id = 0;
        for (final Boolean ativo : new Boolean[]{TRUE, FALSE} ) {
            for (int i = 0; i < 10; i++) {
                Item randomPai = null;
                if (itens.size() > 0) {
                    final int index = (int) (itens.size() * Math.random() * 2);
                    if (index < itens.size()) {
                        randomPai = itens.get(index);
                    }
                }
                final String codItem = format(NM_ITEM_PATT, ++id, "upd-fail");
                itens.add(persistSuccess.runTest(updateFailAction, new Item(codItem, codItem, randomPai, ativo)));
            }
        }
    }
    
    @Test
    public void updateSuccessTest() throws Exception {
        final ActionContainer<Item> updateAction = new ActionContainer<Item>() {
            @Override
            public void execute(final CrudActions<Item> crudActions) {
                final Object id = crudActions.getId();
                assertNotNull("id not null",id);
                Item instance = crudActions.resetInstance(id);

                final Item entity = getEntity();
                final String codigoItem = entity.getCodigoItem();
                assertEquals("codigo equals", TRUE, Boolean.valueOf(codigoItem.equals(instance.getCodigoItem())));
                
                crudActions.setEntityValue(FIELD_CODIGO_ITEM, crudActions.getEntityValue(FIELD_CODIGO_ITEM)+".updated");
                
                assertEquals(UPDATED, UPDATED, crudActions.save());
                instance = crudActions.resetInstance(id);
                assertEquals("codigo updated", TRUE, ((String)crudActions.getEntityValue(FIELD_CODIGO_ITEM)).endsWith(".updated"));
                assertEquals("codigo differs", FALSE, Boolean.valueOf(codigoItem.equals(instance.getCodigoItem())));
                //-----                
                final String descricaoItem = entity.getDescricaoItem();
                assertEquals("descricao equals", TRUE, Boolean.valueOf(descricaoItem.equals(instance.getDescricaoItem())));
                
                crudActions.setEntityValue(FIELD_DESCRICAO_ITEM, crudActions.getEntityValue(FIELD_DESCRICAO_ITEM)+".updated");
                
                assertEquals(UPDATED, UPDATED, crudActions.save());
                instance = crudActions.resetInstance(id);
                assertEquals("descricao updated", TRUE, ((String)crudActions.getEntityValue(FIELD_DESCRICAO_ITEM)).endsWith(".updated"));
                assertEquals("descricao differs", FALSE, Boolean.valueOf(descricaoItem.equals(instance.getDescricaoItem())));
            }
        };
        final ArrayList<Item> itens = new ArrayList<>();
        int id = 0;
        for (final Boolean ativo : new Boolean[]{TRUE, FALSE} ) {
            for (int i = 0; i < 10; i++) {
                Item randomPai = null;
                if (itens.size() > 0) {
                    final int index = (int) (itens.size() * Math.random() * 2);
                    if (index < itens.size()) {
                        randomPai = itens.get(index);
                    }
                }
                final String codItem = format(NM_ITEM_PATT, ++id, "upd-suc");
                itens.add(persistSuccess.runTest(updateAction, new Item(codItem, codItem, randomPai, ativo)));
            }
        }
    }

}
