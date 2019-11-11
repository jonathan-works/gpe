package br.com.infox.epp.menu;

import static br.com.infox.core.util.ObjectUtil.is;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.XmlUtil;
import br.com.infox.epp.menu.api.Menu;
import br.com.infox.epp.system.PropertiesLoader;
import br.com.infox.jsf.function.ElFunctions;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.seam.security.SecurityUtil;

@Stateless
public class MenuService {

    @Inject private PathResolver pathResolver;
    @Inject private PropertiesLoader propertiesLoader;
    @Inject private SecurityUtil securityUtil;
    @Inject private InfoxMessages infoxMessages;

    private Menu join(Menu primary, Menu extension){
        if (primary != null && extension != null)
            primary.addAll(extension.getItems());
        return primary;
    }
    
    boolean isVisible(MenuItemDTO menuItemDTO){
        boolean result = false;
        if (menuItemDTO.getPermission() != null){
            result = ElFunctions.evaluateExpression(menuItemDTO.getPermission(), boolean.class);
        } else if (is(menuItemDTO.getItems()).notEmpty()){
            result = resolveVisibility(menuItemDTO.getItems());
        } else if (is(menuItemDTO.getUrl()).notEmpty()) {
            result = securityUtil.checkPage(SecurityUtil.PAGES_PREFIX + menuItemDTO.getUrl().replaceFirst("xhtml$", "seam"));
        }
        return result;
    }
    
    boolean resolveVisibility(Iterable<MenuItemDTO> iterable){
        boolean isVisible = false;
        for (Iterator<MenuItemDTO> it = iterable.iterator(); it.hasNext();) {
            MenuItemDTO menuItemDTO = it.next();
            boolean isInternalVisible = isVisible(menuItemDTO);
            if (!isInternalVisible){
                it.remove();
            }
            isVisible |= isInternalVisible;
        }
        return isVisible;
    }

    String resolveLabel(String label) {
        String result = label.trim();
        if (result.startsWith("#{") && result.endsWith("}")) {
            result = ElFunctions.evaluateExpression(result, String.class);
        } else {
            result = infoxMessages.get(result);
        }
        return result;
    }
    void resolveLabel(MenuItemDTO menuItemDTO){
        if (is(menuItemDTO.getLabel()).notEmpty())
            menuItemDTO.setLabel(resolveLabel(menuItemDTO.getLabel()));
    }
    void resolveURL(MenuItemDTO menuItemDTO){
        if (is(menuItemDTO.getUrl()).notEmpty())
            menuItemDTO.setUrl(pathResolver.getContextPath(menuItemDTO.getUrl()));
    }
    void transform(Iterable<MenuItemDTO> iterable){
        for (Iterator<MenuItemDTO> it = iterable.iterator(); it.hasNext();) {
            transform(it.next());
        }
    }
    void transform(MenuItemDTO menuItemDTO){
        resolveLabel(menuItemDTO);
        if (is(menuItemDTO.getItems()).notEmpty()){
            transform(menuItemDTO.getItems());
        }
    }
    
    public List<MenuItemDTO> getMenuItemList() {
        Menu menu = join(XmlUtil.loadFromXml(MenuImpl.class, getResource("/META-INF/menu/navigationMenu.xml")), propertiesLoader.getMenu());
        List<MenuItemDTO> dropMenus = MenuItemDTO.convert(menu);
        resolveVisibility(dropMenus);
        transform(dropMenus);
        return dropMenus;
    }

    private InputStream getResource(String relativePath) {
        return MenuNavigation.class.getResourceAsStream(relativePath);
    }


}
