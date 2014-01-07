package br.com.infox.epp.access.service;

import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;

import br.com.infox.epp.access.entity.UsuarioLogin;

public class ChangePasswordOperation extends RunAsOperation {

    private final UsuarioLogin usuario;
    private final String password;
    
    public ChangePasswordOperation(final UsuarioLogin usuario, final String password) {
        super(true);
        this.usuario = usuario;
        this.password = password;
    }
    
    @Override
    public void execute() {
        IdentityManager.instance().changePassword(usuario.getLogin(), password);
    }

}
