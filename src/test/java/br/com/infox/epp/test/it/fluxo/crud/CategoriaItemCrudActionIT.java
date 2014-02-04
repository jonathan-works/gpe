package br.com.infox.epp.test.it.fluxo.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.REMOVED;
import static br.com.infox.core.constants.WarningConstants.UNCHECKED;
import static java.lang.Boolean.TRUE;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.infox.epp.fluxo.crud.CategoriaCrudAction;
import br.com.infox.epp.fluxo.crud.CategoriaItemCrudAction;
import br.com.infox.epp.fluxo.crud.ItemCrudAction;
import br.com.infox.epp.fluxo.dao.CategoriaDAO;
import br.com.infox.epp.fluxo.dao.CategoriaItemDAO;
import br.com.infox.epp.fluxo.dao.ItemDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.CategoriaItem;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.manager.CategoriaItemManager;
import br.com.infox.epp.fluxo.manager.CategoriaManager;
import br.com.infox.epp.fluxo.manager.ItemManager;
import br.com.infox.epp.fluxo.tree.ItemTreeHandler;
import br.com.infox.epp.test.crud.AbstractCrudTest;
import br.com.infox.epp.test.crud.CrudActions;
import br.com.infox.epp.test.infra.ArquillianSeamTestSetup;

@RunWith(Arquillian.class)
public class CategoriaItemCrudActionIT extends AbstractCrudTest<CategoriaItem>{

    @Deployment
    @OverProtocol(SERVLET_3_0)
    public static WebArchive createDeployment() {
        return new ArquillianSeamTestSetup().addClasses(CategoriaItemCrudAction.class,
                ItemManager.class, ItemTreeHandler.class,ItemCrudAction.class,
                CategoriaCrudAction.class,CategoriaItemManager.class,
                CategoriaItemDAO.class,CategoriaDAO.class,CategoriaManager.class,
                ItemDAO.class,CategoriaItemDAO.class,CategoriaItemManager.class)
                .createDeployment();
    }
    
    @Override
    protected String getComponentName() {
        return CategoriaItemCrudAction.NAME;
    }

    @Override
    protected void initEntity(final CategoriaItem entity,final CrudActions<CategoriaItem> crudActions) {
        crudActions.setComponentValue("categoria", entity.getCategoria());
        crudActions.setComponentValue("item", entity.getItem());
    }

    private final RunnableTest<Item> persistItem = new RunnableTest<Item>(ItemCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Item entity = getEntity();

            newInstance();
            setEntityValue("codigoItem", entity.getCodigoItem());
            setEntityValue("descricaoItem", entity.getDescricaoItem());
            setEntityValue("itemPai", entity.getItemPai());
            setEntityValue("ativo", entity.getAtivo());

            assertEquals(PERSISTED, PERSISTED, save());
            final Integer id = getId();
            assertNotNull("id not null", id);

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
        final Item perifericos = persistItem.runTest(new Item("perifComp"
                + suffix, "Periféricos de Computador" + suffix, Boolean.TRUE));
        final Item logitech = persistItem.runTest(new Item("logitech" + suffix, "Logitech"
                + suffix, perifericos, Boolean.TRUE));
        final Item leadership = persistItem.runTest(new Item("leadership"
                + suffix, "Leadership" + suffix, perifericos, Boolean.TRUE));
        final Item computadores = persistItem.runTest(new Item("comp" + suffix, "Computadores"
                + suffix, Boolean.TRUE));
        final Item notbook = persistItem.runTest(new Item("notbook" + suffix, "Notebook"
                + suffix, computadores, Boolean.TRUE));
        final Item dell = persistItem.runTest(new Item("dell" + suffix, "Dell"
                + suffix, notbook, Boolean.TRUE));

        addToMap(perifericos, logitech, leadership, computadores, notbook, dell, persistItem.runTest(new Item("microsoft"
                + suffix, "Microsoft" + suffix, perifericos, Boolean.FALSE)), persistItem.runTest(new Item("ultbook"
                + suffix, "Ultrabook" + suffix, computadores, Boolean.FALSE)), persistItem.runTest(new Item("mcbook"
                + suffix, "Macbook" + suffix, computadores, Boolean.FALSE)), persistItem.runTest(new Item("hp"
                + suffix, "Hewlett Packard" + suffix, notbook, Boolean.FALSE)), persistItem.runTest(new Item("clamSh0979"
                + suffix, "Mouse Clamshell 0979 USB" + suffix, leadership, Boolean.TRUE)), persistItem.runTest(new Item("mgc2022"
                + suffix, "Mouse Magic 2022 USB" + suffix, leadership, Boolean.TRUE)), persistItem.runTest(new Item("m600"
                + suffix, "Mouse Touch M600" + suffix, logitech, Boolean.TRUE)), persistItem.runTest(new Item("m187"
                + suffix, "Mouse Wireless M187" + suffix, logitech, Boolean.TRUE)),

        persistItem.runTest(new Item("inspiron1525" + suffix, "Inspiron 1525"
                + suffix, dell, Boolean.TRUE)), persistItem.runTest(new Item("inspiron14"
                + suffix, "Inspiron 14" + suffix, dell, Boolean.TRUE)));
    }
    
    private final RunnableTest<CategoriaItem> addCategoriaItemSuccess = new RunnableTest<CategoriaItem>(CategoriaItemCrudAction.NAME) {
        @Override
        @SuppressWarnings(UNCHECKED)
        protected void testComponent() throws Exception {
            final CategoriaItem entity = getEntity(); 
            final Categoria categoria = entity.getCategoria();
            assertNotNull("categoria not null", categoria);
            final int idCategoria = categoria.getIdCategoria();
            assertNotNull("id categoria not null", idCategoria);
            final Item item = entity.getItem();
            assertNotNull("item not null", item);
            assertNotNull("id item not null", item.getIdItem());
            initEntity(entity, this);
            
            assertEquals("categoriaItem persisted", PERSISTED, save());
            
            final Set<Item> itemArray = invokeMethod("itemManager", "getFolhas", Set.class, new Class[]{Item.class}, item);
   
            final List<CategoriaItem> categoriaItemList = invokeMethod("categoriaItemManager","listByCategoria", List.class, new Class[]{Categoria.class}, categoria);
            for (final Item it : itemArray) {
                Boolean itemExists = Boolean.FALSE;
                
                final Boolean ativo = it.getAtivo();
                if (!ativo) {
                    continue;
                }
                for(final CategoriaItem categoriaItem : categoriaItemList) {
                    assertEquals("categoria", Boolean.TRUE, categoria.equals(categoriaItem.getCategoria()));
                    final Item categoriaItem_Item = categoriaItem.getItem();
                    assertNotNull("item not null", categoriaItem_Item);
                    
                    if (it.getCodigoItem().equals(categoriaItem_Item.getCodigoItem())
                            && it.getDescricaoItem().equals(categoriaItem_Item.getDescricaoItem())
                            && ativo.equals(categoriaItem_Item.getAtivo())) {
                        itemExists = Boolean.TRUE;
                        break;
                    }
                }
                assertEquals("item exists", Boolean.TRUE, itemExists);
            }
        }
    };

    private final RunnableTest<CategoriaItem> removeCategoriaItemSuccess = new RunnableTest<CategoriaItem>(CategoriaItemCrudAction.NAME) {
        @Override
        @SuppressWarnings(UNCHECKED)
        protected void testComponent() throws Exception {
            final CategoriaItem entity = getEntity();
            assertNotNull("categoriaItem not null", entity);
            final Categoria categoria = entity.getCategoria();
            
            assertNotNull("categoria not null", categoria);
            assertNotNull("id categoria not null", categoria.getIdCategoria());
            initEntity(entity, this);
            for (final CategoriaItem categoriaItem : (List<CategoriaItem>)invokeMethod("categoriaItemManager","listByCategoria", List.class, new Class[]{Categoria.class}, categoria)) {
                assertEquals(REMOVED, REMOVED, remove(categoriaItem));
            }
            assertEquals("list empty", 0, invokeMethod("categoriaItemManager","listByCategoria", List.class, new Class[]{Categoria.class}, categoria).size());
        }
    };
    
    private final RunnableTest<Categoria> persistCategoria = new RunnableTest<Categoria>(CategoriaCrudAction.NAME) {
        @Override
        protected void testComponent() throws Exception {
            final Categoria entity = getEntity(); 
            newInstance();
            final String categoria = entity.getCategoria();
            crudActions.setEntityValue("categoria", categoria);
            final Boolean ativo = entity.getAtivo();
            crudActions.setEntityValue("ativo", ativo);
            assertEquals("persisted", PERSISTED, save());

            final Integer id = getId();
            assertNotNull("id", id);
            newInstance();
            assertNull("nullId", getId());
            setId(id);
            final Categoria instance = getInstance();
            assertEquals("Compare", true, instance.getCategoria().equals(categoria) && instance.getAtivo().equals(ativo));
            setEntity(getInstance());
        }
    };
    
    @Test
    public void removeCategoriaItemSuccessTest() throws Exception {
        inicializaItens("removeSuccess");
        final Categoria categoria = persistCategoria.runTest(new Categoria("categoriaRemItemSucc1Test", TRUE));
        final Item item = itens.get("perifCompremoveSuccess");
        final CategoriaItem entity = new CategoriaItem(categoria, item);
        
        addCategoriaItemSuccess.runTest(entity);
        removeCategoriaItemSuccess.runTest(entity);
    }
    
    @Test
    public void addCategoriaItemSuccessTest() throws Exception {
        inicializaItens("addSuccess");
        final Categoria categoria = persistCategoria.runTest(new Categoria("categoriaAddItemSucc1Test", TRUE));
        final Item item = itens.get("perifCompaddSuccess");
        final CategoriaItem entity = new CategoriaItem(categoria, item);
        addCategoriaItemSuccess.runTest(entity);
    }

}
