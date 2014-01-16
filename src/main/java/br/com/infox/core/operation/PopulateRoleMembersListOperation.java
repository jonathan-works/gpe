package br.com.infox.core.operation;

import java.security.Principal;
import java.util.List;

import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;

public class PopulateRoleMembersListOperation extends RunAsOperation {
    private final String role;
    private final List<Principal> membersList;

    public PopulateRoleMembersListOperation(final String role,
            final List<Principal> membersList) {
        super(true);
        this.role = role;
        this.membersList = membersList;
    }

    @Override
    public void execute() {
        membersList.addAll(IdentityManager.instance().listMembers(role));
    }
}
