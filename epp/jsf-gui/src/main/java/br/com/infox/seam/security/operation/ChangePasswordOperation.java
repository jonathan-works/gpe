package br.com.infox.seam.security.operation;

import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;

public class ChangePasswordOperation extends RunAsOperation {
    private final String senha;
    private final String login;

    public ChangePasswordOperation(final String login, final String senha) {
        super(Boolean.TRUE);
        this.senha = senha;
        this.login = login;
    }

    @Override
    public void execute() {
        IdentityManager.instance().changePassword(login, senha);
    }
}
