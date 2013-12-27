package br.com.infox.epp.access.crud;

import javax.security.auth.login.LoginException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.exception.BusinessException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.system.util.ParametroUtil;

@Name(UsuarioLoginCrudAction.NAME)
public class UsuarioLoginCrudAction extends AbstractCrudAction<UsuarioLogin> {
    public static final String NAME = "usuarioLoginCrudAction";
    private static final LogProvider LOG = Logging.getLogProvider(UsuarioLoginCrudAction.class);
    
    @In private PasswordService passwordService;

    private String password;
    
    @Override
    public void newInstance() {
        super.newInstance();
        final UsuarioLogin usuarioLogin = getInstance();
        usuarioLogin.setBloqueio(false);
        usuarioLogin.setProvisorio(false);
    }

    @Override
    protected boolean beforeSave() {
        validarPermanencia();
        return true;
    }
    
    //TODO: Não é validação. definir outro nome e local para existir
    private void validarPermanencia() {
        final UsuarioLogin usuario = getInstance();
        if (!usuario.getProvisorio()) {
            usuario.setDataExpiracao(null);
        }
        if (!usuario.isHumano()){
            usuario.setPessoaFisica(null);
        }
    }
    
    @Override
    protected void afterSave(String ret) {
        final UsuarioLogin usuario = getInstance();
        if (usuario.getSenha() == null || ParametroUtil.LOGIN_USUARIO_EXTERNO.equals(usuario.getLogin())) {
            try {
                passwordService.requisitarNovaSenha(usuario.getEmail(), "");
            } catch (BusinessException be){
            	LOG.warn("afterSave(ret)", be);
                FacesMessages.instance().add(Severity.INFO, be.getLocalizedMessage());
            } catch (LoginException e) {
                LOG.error("afterSave(ret)", e);
            }
        }
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public UsuarioEnum[] getTiposDeUsuario(){
        return UsuarioEnum.values();
    }
    
}
