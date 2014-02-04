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
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.access.type.UsuarioEnum;

@Name(UsuarioLoginCrudAction.NAME)
public class UsuarioLoginCrudAction extends AbstractCrudAction<UsuarioLogin, UsuarioLoginManager> {
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(UsuarioLoginCrudAction.class);
    public static final String NAME = "usuarioLoginCrudAction";
    
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
    protected boolean isInstanceValid() {
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
    protected void afterSave(final String ret) {
        final UsuarioLogin usuario = getInstance();
        if (PERSISTED.equals(ret)) {
            final String afterSaveExceptionMsg = "afterSave(ret)";
            try {
                passwordService.requisitarNovaSenha(Boolean.FALSE, usuario.getEmail());
                getMessagesHandler().add("Senha gerada com sucesso.");
            } catch (final BusinessException be){
            	LOG.warn(afterSaveExceptionMsg, be);
                getMessagesHandler().add(be.getLocalizedMessage());
            } catch (final LoginException e) {
                LOG.error(afterSaveExceptionMsg, e);
            } catch (final DAOException e) {
                LOG.error(afterSaveExceptionMsg, e);
            }
        }
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
    
    public UsuarioEnum[] getTiposDeUsuario(){
        return UsuarioEnum.values();
    }
    
}
