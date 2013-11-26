package br.com.infox.epp.access.service;

import javax.security.auth.login.LoginException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.util.RandomStringUtils;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.mail.service.AccessMailService;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.itx.util.EntityUtil;

@Name(PasswordService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class PasswordService {
    
    public static final String NAME = "passwordService";
    
    private static final int PASSWORD_LENGTH = 8;
    
    @In private AccessMailService accessMailService;
    @In private UsuarioLoginManager usuarioLoginManager;
    
    public void requisitarNovaSenha(String email, String login) throws LoginException {
        UsuarioLogin usuario;
        if (email.isEmpty() && login.isEmpty()) {
            throw new LoginException("É preciso informar o login ou o e-mail do usuário");
        } else if (!login.isEmpty()) {
            usuario = usuarioLoginManager.getUsuarioLoginByLogin(login);
            recoverUsuario(usuario, "login");
        } else if (!email.isEmpty()) {
            usuario = usuarioLoginManager.getUsuarioLoginByEmail(email);
            recoverUsuario(usuario, "email");
        }
    }
    
    private void recoverUsuario(UsuarioLogin usuario, String tipoParametro) throws LoginException {
        if (usuario == null) {
            throw new LoginException("Usuário não encontrado");
        } else {
            String password = gerarNovaSenha(usuario);
            accessMailService.enviarEmailDeMudancaDeSenha(tipoParametro, usuario, password);
        }
    }
    
    private String gerarNovaSenha(final UsuarioLogin usuario) {
        final String password;
        if (ParametroUtil.LOGIN_USUARIO_EXTERNO.equals(usuario.getLogin())) {
            password = "";
        } else {
            password = RandomStringUtils.randomAlphabetic(PASSWORD_LENGTH);
        }
        usuario.setSenha(password);
        new RunAsOperation(true) {
            @Override
            public void execute() {
                IdentityManager.instance().changePassword(usuario.getLogin(), password);
            }
        }.run();
        
        EntityUtil.getEntityManager().flush();
        return password;
    }

}
