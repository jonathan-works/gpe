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
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.itx.util.EntityUtil;

@Name(PasswordService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class PasswordService {
    
    public static final String NAME = "passwordService";
    
    private static final int PASSWORD_LENGTH = 8;
    
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
//            setId(usuario.getIdPessoa());
            gerarNovaSenha(usuario, tipoParametro);
            enviarEmailDeMudancaDeSenha(tipoParametro);
        }
    }
    
    private void gerarNovaSenha(final UsuarioLogin usuario, String tipoParametro) {
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
        
//        iniciarRequisicao(tipoParametro);
    }
    
    private void enviarEmailDeMudancaDeSenha(String parametro) {
        String nomeParam = resolveTipoDeEmail(parametro);
        String nomeModelo = ParametroUtil.getParametroOrFalse(resolveTipoDeEmail(parametro));

        if (!enviarModeloPorNome(nomeModelo)) {
            throw new BusinessException("Erro no envio do e-mail. O parâmetro de sistema '"
                    + nomeParam
                    + "' não foi definido ou possui um valor inválido");
        }
    }

    /**
     * @param parametro
     * @return
     */
    private String resolveTipoDeEmail(String parametro) {
        String nomeParam = null;
        if ("login".equals(parametro)) {
            nomeParam = "tituloModeloEmailMudancaSenha";
        } else if ("email".equals(parametro)) {
            nomeParam = "tituloModeloEmailMudancaSenhaComLogin";
        }
        return nomeParam;
    }
    
    private boolean enviarModeloPorNome(String nomeModelo){
        return true;
    }

}
