package br.com.infox.epp.access.crud;

import java.util.Date;

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
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaManager;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.itx.util.EntityUtil;

@Name(UsuarioLoginCrudAction.NAME)
public class UsuarioLoginCrudAction extends AbstractCrudAction<UsuarioLogin> {
    
    public static final String NAME = "usuarioLoginCrudAction";
    private static final LogProvider LOG = Logging.getLogProvider(UsuarioLoginCrudAction.class);
    
    private BloqueioUsuario novoBloqueio;
    
    @In private UsuarioLoginManager usuarioLoginManager;
    @In private PessoaManager pessoaManager;
    
    @In private PasswordService passwordService;

    private boolean pessoaFisicaCadastrada;
    private String password;
    
    @Override
    public void newInstance() {
        newBloqueioUsuario();
        super.newInstance();
        getInstance().setBloqueio(false);
        getInstance().setProvisorio(false);
    }

    private void newBloqueioUsuario() {
        novoBloqueio = new BloqueioUsuario();
    }
    
    @Override
    public void setId(Object id) {
        super.setId(id);
        newBloqueioUsuario();
    }
    
    @Override
    protected boolean beforeSave() {
        validarPermanencia();
        if (getInstance().getBloqueio()){
            bloquear();
        }
        return super.beforeSave();
    }
    
    private void validarPermanencia() {
        if (!getInstance().getProvisorio()) {
            getInstance().setDataExpiracao(null);
        }
    }
    
    public void bloquear() {
        final UsuarioLogin usuario = getInstance();
        novoBloqueio.setDataBloqueio(new Date());
        novoBloqueio.setUsuario(usuario);
        usuario.getBloqueioUsuarioList().add(novoBloqueio);
        try {
            getGenericManager().persist(novoBloqueio);
        } catch (DAOException e) {
            LOG.error(".bloquear()", e);
        }
        novoBloqueio = new BloqueioUsuario();
    }
    
    @Override
    public String save() {
        String resultado;
        if (!pessoaFisicaCadastrada){
            resultado = super.save();
        } else{
            PessoaFisica pf = EntityUtil.find(PessoaFisica.class, getInstance().getIdPessoa());
            usuarioLoginManager.inserirUsuarioParaPessoaFisicaCadastrada(getInstance());
            EntityUtil.getEntityManager().detach(pf);
            setInstance(usuarioLoginManager.getUsuarioLogin(getInstance()));
            resultado = "persisted";
            afterSave(resultado);
        }
        return resultado;
    }
    
    @Override
    protected void afterSave(String ret) {
        super.afterSave(ret);
        if (getInstance().getSenha() == null || ParametroUtil.LOGIN_USUARIO_EXTERNO.equals(getInstance().getLogin())) {
            try {
                passwordService.requisitarNovaSenha(getInstance().getEmail(), "");
            } catch (BusinessException be){
                FacesMessages.instance().add(Severity.INFO, be.getLocalizedMessage());
            } catch (LoginException e) {
                LOG.error("afterSave()", e);
            }
        }
        if (getInstance() instanceof PessoaFisica){
            try {
                setInstance(EntityUtil.cloneEntity(getInstance(), false));
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.error(".afterSave()", e);
            }
            getInstance().loadDataFromPessoaFisica(getInstance());
        };
    }
    
    @Override
    protected void afterSave() {
        newBloqueioUsuario();
        super.afterSave();
    }
    
    public void searchByCpf(String cpf){
        newInstance();
        UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioLoginByCpf(cpf);
        if (usuarioLogin != null){
            setInstance(usuarioLogin);
        } else{
            feedFromPessoaFisica(cpf);
        }
    }

    /**
     * @param cpf
     */
    private void feedFromPessoaFisica(String cpf) {
        PessoaFisica pessoaFisica = pessoaManager.getPessoaFisicaByCpf(cpf);
        if (pessoaFisica != null){
            pessoaFisicaCadastrada = true;
            setInstance(getInstance().loadDataFromPessoaFisica(pessoaFisica));
        }
        else {
            pessoaFisicaCadastrada = false;
        }
    }
    
    public BloqueioUsuario getNovoBloqueio() {
        return novoBloqueio;
    }

    public void setNovoBloqueio(BloqueioUsuario novoBloqueio) {
        this.novoBloqueio = novoBloqueio;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
