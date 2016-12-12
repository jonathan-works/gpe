package br.com.infox.epp.access.component.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions;
import org.richfaces.event.DropEvent;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.system.PropertiesLoader;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.security.SecurityUtil;

/**
 * Monta o menu do usuário baseado nas permissões de acesso às páginas
 * 
 * @author luizruiz
 * 
 */
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(Menu.class);

    private List<MenuItem> bookmark = new ArrayList<MenuItem>();

    private List<MenuItem> dropMenus;
    
    public void drop(DropEvent o) {
        bookmark.add((MenuItem) o.getDragValue());
    }

    /**
     * Cria o menu, verificando para cada ítem se o usuário é admin ou tem um
     * role no formato SecurityUtil.PAGES_PREFIX seguido da url da página a ser
     * acessada
     * 
     * @param items
     */
    public void setItems(List<String> items) {
        SecurityUtil securityUtil = BeanManager.INSTANCE.getReference(SecurityUtil.class);
        try {
            InitialContext ic = new InitialContext();
            PropertiesLoader propertiesLoader = (PropertiesLoader) ic.lookup(PropertiesLoader.JNDI_PORTABLE_NAME);
            items.addAll(propertiesLoader.getMenuItems());
        } catch (NamingException e) {
            LOG.error("", e);
        }
    	
        dropMenus = new ArrayList<MenuItem>();

        for (String key : items) {
            try {
                String[] split = key.split(":");
                key = split[0];
                String url = null;
                if (split.length > 1) {
                    url = split[1];
                }
                String pageRole = SecurityUtil.PAGES_PREFIX + url;
                if (securityUtil.checkPage(pageRole)) {
                    buildItem(key, url);
                }
            } catch (Exception e) {
                LOG.error(".setItems()", e);
                break;
            }
        }
    }

    protected void buildItem(String key, String url) {
        String formatedKey = getFormatedKey(key);
        String[] groups = formatedKey.split("/");
        MenuItem parent = new MenuItem(null);
        for (int i = 0; i < groups.length; i++) {
            String label = groups[i];
            if (!label.startsWith("#{infoxMessages['")) {
                label = InfoxMessages.getInstance().get(label);
            } else {
                label = Expressions.instance().createValueExpression(label, String.class).getValue();
            }
            MenuItem item = new MenuItem(label);
            if (i == 0) {
                int j = dropMenus.indexOf(item);
                if (j != -1) {
                    parent = dropMenus.get(j);
                } else {
                    parent = item;
                    if (groups.length == 1) {
                        parent.setUrl(url);
                    }
                    dropMenus.add(parent);
                }
            } else if (i < (groups.length - 1)) {
                parent = parent.add(item);
            } else {
                item.setUrl(url);
                parent.getItems().add(item);
            }
        }
    }
    
    private String getFormatedKey(String key) {
        if (key.startsWith("/")) {
            return key.substring(1);
        } else {
            return key;
        }
    }

    public List<MenuItem> getBookmark() {
        return bookmark;
    }

    public List<MenuItem> getDropMenus() {
        return dropMenus;
    }

    public void clearMenu() {
        Contexts.removeFromAllContexts("mainMenu");
    }
}
