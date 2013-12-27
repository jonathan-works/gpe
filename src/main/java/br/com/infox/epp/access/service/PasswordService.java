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

import br.com.infox.core.exception.BusinessException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.mail.service.AccessMailService;
import br.com.infox.epp.system.util.ParametroUtil;

@Name(PasswordService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class PasswordService {
    
    public static final String NAME = "passwordService";
    
    private static final int PASSWORD_LENGTH = 8;
    
    @In private AccessMailService accessMailService;
    @In private UsuarioLoginManager usuarioLoginManager;
    
    public void requisitarNovaSenha(final String email, final String login) throws LoginException, BusinessException, DAOException {
        UsuarioLogin usuario;
        String mode;
        if (!login.isEmpty()) {
            usuario = usuarioLoginManager.getUsuarioLoginByLogin(login);
            mode = "login";
        } else if (!email.isEmpty()) {
            usuario = usuarioLoginManager.getUsuarioLoginByEmail(email);
            mode = "email";
        } else {
            throw new LoginException("É preciso informar o login ou o e-mail do usuário");
        }
        recoverUsuario(usuario, mode);
    }
    
    private void recoverUsuario(UsuarioLogin usuario, String tipoParametro) throws LoginException, BusinessException, DAOException {
        if (usuario == null) {
            throw new LoginException("Usuário não encontrado");
        }
        final String password = gerarNovaSenha(usuario);
        accessMailService.enviarEmailDeMudancaDeSenha(tipoParametro, usuario, password);
    }
    
    private String gerarNovaSenha(final UsuarioLogin usuario) throws DAOException {
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
        usuarioLoginManager.update(usuario);
        return password;
    }

}
