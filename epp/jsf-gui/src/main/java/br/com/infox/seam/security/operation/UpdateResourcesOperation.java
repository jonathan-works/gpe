package br.com.infox.seam.security.operation;

import java.util.List;

import org.jboss.seam.security.Role;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.permission.Permission;
import org.jboss.seam.security.permission.PermissionManager;

public class UpdateResourcesOperation extends RunAsOperation {

    private static final String ACCESS = "access";

    private final String resource;
    private final List<String> rolesToGrant;
    private final List<String> rolesToRevoke;

    public UpdateResourcesOperation(String resource, List<String> rolesToGrant,
            List<String> rolesToRevoke) {
        super(Boolean.TRUE);
        this.resource = resource;
        this.rolesToGrant = rolesToGrant;
        this.rolesToRevoke = rolesToRevoke;
        for (String role : rolesToGrant) {
            rolesToRevoke.remove(role);
        }

    }

    @Override
    public void execute() {
        final PermissionManager permissionManager = PermissionManager.instance();
        for (String role : rolesToRevoke) {
            permissionManager.revokePermission(createPermission(role));
        }
        for (String role : rolesToGrant) {
            permissionManager.grantPermission(createPermission(role));
        }
    }
    
    private Permission createPermission(final String identificador) {
        return new Permission(resource, ACCESS, new Role(identificador));
    }

}
