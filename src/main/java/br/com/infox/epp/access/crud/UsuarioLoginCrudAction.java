package br.com.infox.epp.access.crud;

import javax.security.auth.login.LoginException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.exception.BusinessException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.access.type.UsuarioEnum;

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
        usuarioLogin.setBloqueio(Boolean.FALSE);
        usuarioLogin.setProvisorio(Boolean.FALSE);
    }

    @Override
    protected boolean beforeSave() {
        validarPermanencia();
        return Boolean.TRUE;
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
        if (PERSISTED.equals(ret)) {
            final String afterSaveExceptionMsg = "afterSave(ret)";
            try {
                passwordService.requisitarNovaSenha(Boolean.FALSE, usuario.getEmail());
            } catch (BusinessException be){
            	LOG.warn(afterSaveExceptionMsg, be);
                getMessagesHandler().add(be.getLocalizedMessage());
            } catch (LoginException e) {
                LOG.error(afterSaveExceptionMsg, e);
            } catch (DAOException e) {
                LOG.error(afterSaveExceptionMsg, e);
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
