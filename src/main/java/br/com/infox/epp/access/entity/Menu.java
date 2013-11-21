package br.com.infox.epp.access.entity;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;
import org.richfaces.event.DropEvent;

import br.com.infox.epp.access.util.SecurityUtil;
import br.com.itx.component.Util;
import br.com.itx.util.EntityUtil;

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

    /**
     * Flag para indicar que as páginas já foram verificadas Isso ocorre apenas
     * uma vez (static)
     */
    private static boolean pagesChecked;

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
        dropMenus = new ArrayList<MenuItem>();
        boolean ok = true;
        
        try {
        	discoverAndCreateRolesIfNeeded();
        } catch (IOException e) {
        	LOG.error("Não foi possível descobrir e criar os recursos", e);
        }
        
        for (String key : items) {
            try {
                String[] split = key.split(":");
                key = split[0];
                String url = null;
                if (split.length > 1) {
                    url = split[1];
                }
                String pageRole = SecurityUtil.PAGES_PREFIX + url;
                if (Identity.instance().hasRole(pageRole)) {
                    buildItem(key, url);
                }
            } catch (Exception e) {
                LOG.error(".setItems()", e);
                ok = false;
                break;
            }
        }
        pagesChecked = ok;
    }

    private void discoverAndCreateRolesIfNeeded() throws IOException {
    	if (pagesChecked || !Identity.instance().isLoggedIn()) {
        	return;
        }
    	
    	RoleCreator roleCreator = new RoleCreator();
    	Files.walkFileTree(new File(new Util().getContextRealPath()).toPath(), roleCreator);
	}

    protected void buildItem(String key, String url) {
        String formatedKey = getFormatedKey(key);
        String[] groups = formatedKey.split("/");
        MenuItem parent = new MenuItem(null);
        for (int i = 0; i < groups.length; i++) {
            String label = groups[i];
            if (!label.startsWith("#{messages['")) {
                label = "#{messages['" + label + "']}";
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
                parent.getChildren().add(item);
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

    private static class RoleCreator extends SimpleFileVisitor<Path> {
    	private static final String PAGE_XML_EXTENSION = ".page.xml";
    	private static final String XHTML_EXTENSION = ".xhtml";
    	private static final String SEAM_EXTENSION = ".seam";
    	private static final String ADMIN_GROUP = "admin";
    	private static final String ADMIN_ROLE = "admin";
    	
    	@Override
    	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    		if (!isPageXml(file)) {
    			return FileVisitResult.CONTINUE;
    		}
    		
    		Path xhtmlFile;
    		try {
    			xhtmlFile = file.resolveSibling(file.getFileName().toString().replace(PAGE_XML_EXTENSION, XHTML_EXTENSION));
    		} catch (InvalidPathException e) {
    			return FileVisitResult.CONTINUE;
    		}
    		
    		Path war = new File(new Util().getContextRealPath()).toPath();
    		
    		String relativeXhtmlFile = xhtmlFile.toString().replace(war.toString(), "").replace(XHTML_EXTENSION, SEAM_EXTENSION);
    		
    		createRoleIfNeeded(SecurityUtil.PAGES_PREFIX + relativeXhtmlFile.replace("\\", "/"));
    		
    		return FileVisitResult.CONTINUE;
    	}

		private boolean isPageXml(Path file) {
			return file.getFileName().toString().endsWith(PAGE_XML_EXTENSION);
		}
		
	    private void createRoleIfNeeded(final String pageRole) {
	        if (IdentityManager.instance().roleExists(pageRole)) {
	        	return;
	        }
	        
            new RunAsOperation(true) {
                @Override
                public void execute() {
                    IdentityManager.instance().createRole(pageRole);
                    EntityUtil.getEntityManager().flush();
                }
            }.run();
            
            addToGroup(pageRole, ADMIN_GROUP);
            addToLoggedUserIfAdmin(pageRole);
	    }

		private void addToLoggedUserIfAdmin(final String pageRole) {
			if (Identity.instance().hasRole(ADMIN_ROLE)) {
                Identity.instance().addRole(pageRole);
            }
		}

		private void addToGroup(final String pageRole, final String group) {
			new RunAsOperation(true) {
                @Override
                public void execute() {
                    IdentityManager.instance().addRoleToGroup(group, pageRole);
                }
            }.run();
		}
    }
}
