package br.com.infox.epp.access.entity;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Role;
import org.jboss.seam.security.permission.Permission;
import org.jboss.seam.security.permission.PermissionManager;
import org.jboss.seam.security.permission.PermissionStore;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.manager.RecursoManager;
import br.com.infox.epp.access.util.SecurityUtil;
import br.com.itx.component.Util;
import br.com.itx.util.ComponentUtil;

class RecursoCreator extends SimpleFileVisitor<Path> {
	private static final String PAGE_XML_EXTENSION = ".page.xml";
	private static final String XHTML_EXTENSION = ".xhtml";
	private static final String SEAM_EXTENSION = ".seam";
	private static final String ADMIN_ROLE = "admin";
	
	private static final LogProvider LOG = Logging.getLogProvider(RecursoCreator.class);
	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if (!isPageXml(file)) {
			return FileVisitResult.CONTINUE;
		}
		
		Path xhtmlFile;
		try {
			xhtmlFile = file.resolveSibling(file.getFileName().toString().replace(PAGE_XML_EXTENSION, XHTML_EXTENSION));
		} catch (InvalidPathException e) {
			LOG.warn(".visitFile(file, attrs)", e);
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
        RecursoManager recursoManager = ComponentUtil.getComponent(RecursoManager.NAME);
        if (recursoManager.existsRecurso(pageRole)){
            return;
        }
        Recurso recurso = new Recurso(pageRole, pageRole);
        try {
            recursoManager.persist(recurso);
        } catch (DAOException e) {
            LOG.error(pageRole, e);
        }
        Permission permission = new Permission(pageRole, "access", new Role(ADMIN_ROLE));
        PermissionStore ps = PermissionManager.instance().getPermissionStore();
        ps.grantPermission(permission);
    }
}