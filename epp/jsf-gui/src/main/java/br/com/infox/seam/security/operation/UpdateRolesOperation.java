package br.com.infox.seam.security.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.seam.security.Role;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.action.RoleAction;
import org.jboss.seam.security.permission.Permission;
import org.jboss.seam.security.permission.PermissionManager;

import br.com.infox.seam.util.ComponentUtil;

public class UpdateRolesOperation extends RunAsOperation {
    private static final String ACCESS = "access";
    private static final String ROLE_ACTION = "org.jboss.seam.security.management.roleAction";

    private final IdentityManager identityManager = IdentityManager.instance();
    private final RoleAction roleaction = ComponentUtil.getComponent(ROLE_ACTION);
    private final List<String> roleGroup;
    private final String role;
    private final Collection<String> rolesToInclude;
    private final Collection<String> rolesToExclude;
    private final Collection<String> availableResourcesList;
    private final Collection<String> selectedResourcesList;

    public UpdateRolesOperation(final List<String> roleGroup,
            final String role, final Collection<String> rolesToInclude,
            final Collection<String> rolesToExclude,
            final Collection<String> availableResourcesList,
            final Collection<String> selectedResourcesList) {
        super(Boolean.TRUE);
        this.roleGroup = roleGroup;
        this.role = role;
        this.rolesToInclude = rolesToInclude;
        this.rolesToExclude = rolesToExclude;
        this.availableResourcesList = availableResourcesList;
        this.selectedResourcesList = selectedResourcesList;
        for (String resource : selectedResourcesList) {
            availableResourcesList.remove(resource);
        }
    }

    @Override
    public void execute() {
        if (roleGroup != null) {
            final ArrayList<String> list = new ArrayList<>(roleGroup);
            for (final String papel : list) {
                this.removePapeisImplicitos(papel, roleGroup);
            }
        }

        roleaction.setRole(role);
        roleaction.setGroups(roleGroup);
        roleaction.save();

        updateRoles();
        updatePermissions();
    }

    private void updateRoles() {
        if (rolesToInclude != null) {
            final ArrayList<String> includeList = new ArrayList<>(rolesToInclude);
            includeList.removeAll(rolesToExclude);
            for (final String membro : includeList) {
                this.identityManager.addRoleToGroup(membro, role);
            }
            final ArrayList<String> excludeList = new ArrayList<>(rolesToExclude);
            excludeList.removeAll(rolesToInclude);
            for (final String membro : excludeList) {
                this.identityManager.removeRoleFromGroup(membro, role);
            }
        }
    }

    /**
     * Remove o papel da lista, recursivamente
     * 
     * @param papel
     */
    private void removePapeisImplicitos(final String papel,
            final Collection<String> list) {
        for (final String p : this.identityManager.getRoleGroups(papel)) {
            list.remove(p);
            removePapeisImplicitos(p, list);
        }
    }

    private void updatePermissions() {
        if (availableResourcesList == null) {
            return;
        }
        final PermissionManager permissionManager = PermissionManager.instance();
        permissionManager.revokePermissions(addPermissions(role, availableResourcesList));
        permissionManager.grantPermissions(addPermissions(role, selectedResourcesList));
    }

    private List<Permission> addPermissions(final String identificador,
            final Collection<String> permissionsToAdd) {
        final List<Permission> result = new ArrayList<>();
        for (final String recurso : permissionsToAdd) {
            result.add(new Permission(recurso, ACCESS, new Role(identificador)));
        }
        return result;
    }
}
