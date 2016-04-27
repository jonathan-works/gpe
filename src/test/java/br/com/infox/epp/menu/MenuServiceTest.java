package br.com.infox.epp.menu;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.infox.epp.system.PropertiesLoader;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.seam.security.SecurityUtil;

public class MenuServiceTest {
    
    @Test
    public void testLoadingSeamComponent() {
        List<String> items = getMenuService().getItemsFromMainMenuComponentXml();
        Assert.assertNotNull("Resulted in null array of items", items);
        for (String string : items) {
            Assert.assertNotNull("Null string", string);
            Assert.assertFalse("Empty string", string.isEmpty());
            Assert.assertTrue(string + " didn't match pattern", string.matches("(?:.+\\/)*[^/]+:[^:]+$"));
        }
    }
    
    @Test
    public void testLoadingDefaultComponent() {
        List<String> items = getMenuService().getItemsFromNavigationMenuXml();
        Assert.assertNotNull("Resulted in null array of items", items);
        for (String string : items) {
            Assert.assertNotNull("Null string", string);
            Assert.assertFalse("Empty string", string.isEmpty());
            Assert.assertTrue(string + " didn't match pattern", string.matches("(?:.+\\/)*[^/]+:[^:]+$"));
        }
    }

    @Test
    public void testMenuItemConversion() {
        List<MenuItemDTO> menuItemList = getMenuService().getMenuItemList();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(menuItemList);
        Assert.assertNotNull("Json resultante do menu foi nulo", json);
    }
    
    private void injectField(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    private MenuService getMenuService() {
        MenuService menuService = new MenuService();
        injectField(menuService, "propertiesLoader", createMockPropertiesLoader());
        injectField(menuService, "securityUtil", createSecurityUtil());
        injectField(menuService, "pathResolver", getPathResolver());
        return menuService;
    }

    private PathResolver getPathResolver(){
        return new PathResolver(){
            private static final long serialVersionUID = 1L;
            @Override
            public String getContextPath(String url) {
                return url;
            }
        };
    }
    
    private SecurityUtil createSecurityUtil() {
        return new SecurityUtil(){
            private static final long serialVersionUID = 1L;
            @Override
            public boolean checkPage(String page) {
                return true;
            }
        };
    }

    private PropertiesLoader createMockPropertiesLoader() {
        return new PropertiesLoader() {
            @Override
            public List<String> getMenuItems() {
                return new ArrayList<>();
            }
        };
    }

}
