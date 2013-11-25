package br.com.infox.epp.access.service;

import javax.security.auth.login.LoginException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;

@Name(PasswordService.NAME)
@Scope(ScopeType.EVENT)
public class PasswordService {
    
    public static final String NAME = "passwordService";
    
    @In private UsuarioLoginManager usuarioLoginManager;
    
    public void requisitarNovaSenha(String email, String login) throws LoginException {
        UsuarioLogin usuario;
        if (email.isEmpty() && login.isEmpty()) {
            FacesMessages.instance().add(
                    "É preciso informar o login ou o e-mail do usuário");
            return;
        } else if (!login.isEmpty()) {
            usuario = usuarioLoginManager.getUsuarioLoginByLogin(login);
            recoverUsuario(usuario, "login");
        } else if (!email.isEmpty()) {
            usuario = usuarioLoginManager.getUsuarioLoginByEmail(email);
            recoverUsuario(usuario, "email");
        }
    }
    
    private void recoverUsuario(UsuarioLogin usuario, String tipoParametro){
        
    }

}
