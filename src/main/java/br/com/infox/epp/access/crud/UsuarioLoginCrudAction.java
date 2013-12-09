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
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.PostgreSQLErrorCode;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaManager;
import br.com.infox.epp.system.util.ParametroUtil;

@Name(UsuarioLoginCrudAction.NAME)
public class UsuarioLoginCrudAction extends AbstractCrudAction<UsuarioLogin> {
    
    public static final String NAME = "usuarioLoginCrudAction";
    private static final LogProvider LOG = Logging.getLogProvider(UsuarioLoginCrudAction.class);
    
    @In private UsuarioLoginManager usuarioLoginManager;
    @In private PessoaManager pessoaManager;
    
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
    public void setId(Object id) {
        super.setId(id);
    }
    
    @Override
    protected boolean beforeSave() {
        validarPermanencia();
        return super.beforeSave();
    }
    
    private void validarPermanencia() {
        final UsuarioLogin usuario = getInstance();
        if (!usuario.getProvisorio()) {
            usuario.setDataExpiracao(null);
        }
    }
    
    @Override
    public String save() {
        String resultado;
        savePessoa();
        resultado = super.save();
        if (resultado.equals(PostgreSQLErrorCode.UNIQUE_VIOLATION.toString())){
            getInstance().setPessoaFisica(new PessoaFisica());
        }
        return resultado;
    }
    
    @Override
    protected void afterSave(String ret) {
        super.afterSave(ret);
        if (PERSISTED.equals(ret) || UPDATED.equals(ret)) {
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
    }
    
    @Override
    protected void afterSave() {
        super.afterSave();
    }
    
    public void searchByCpf(String cpf){
        if (getInstance() == null){
            newInstance();
        }
        PessoaFisica pf = pessoaManager.getPessoaFisicaByCpf(cpf);
        if (pf != null){
            getInstance().setPessoaFisica(pf);
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCpf() {
        if (getInstance().getPessoaFisica() != null){
            return getInstance().getPessoaFisica().getCpf();
        }
        return "";
    }

    public void setCpf(String cpf) {
        if (getInstance().getPessoaFisica() == null){
           getInstance().setPessoaFisica(new PessoaFisica()); 
        }
        getInstance().getPessoaFisica().setCpf(cpf);
    }
    
    private void savePessoa(){
        PessoaFisica pessoaAssociada = getInstance().getPessoaFisica();
        if (pessoaAssociada != null && pessoaAssociada.getIdPessoa() == null) {
            try {
                pessoaAssociada.setAtivo(true);
                getGenericManager().persist(pessoaAssociada);
                getInstance().setPessoaFisica(pessoaAssociada);
            } catch (DAOException e) {
                e.printStackTrace();
            }
        }
    }

}
