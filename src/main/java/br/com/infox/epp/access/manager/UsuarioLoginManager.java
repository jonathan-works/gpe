package br.com.infox.epp.access.manager;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.util.RandomStringUtils;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.mail.service.AccessMailService;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.system.manager.ParametroManager;

@Name(UsuarioLoginManager.NAME)
@AutoCreate
public class UsuarioLoginManager extends Manager<UsuarioLoginDAO, UsuarioLogin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioLoginManager";
    
    @In private PasswordService passwordService;
    @In private AccessMailService accessMailService;
    @In private PapelManager papelManager;
    @In private LocalizacaoManager localizacaoManager;
    @In private UsuarioLocalizacaoManager usuarioLocalizacaoManager;
    @In private ParametroManager parametroManager;

    public boolean usuarioExpirou(final UsuarioLogin usuarioLogin) {
        boolean result = Boolean.FALSE;
        if (usuarioLogin != null) {
            final Date dataExpiracao = usuarioLogin.getDataExpiracao();
            result = usuarioLogin.getProvisorio() && dataExpiracao != null
                    && dataExpiracao.before(new Date());
        }
        return result;
    }

    public void inativarUsuario(final UsuarioLogin usuario) {
        getDao().inativarUsuario(usuario);
    }

    public UsuarioLogin getUsuarioLoginByEmail(final String email) {
        return getDao().getUsuarioLoginByEmail(email);
    }

    public UsuarioLogin getUsuarioLoginByLogin(final String login) {
        return getDao().getUsuarioLoginByLogin(login);
    }
    
    public String getActorIdTarefaAtual(Integer idProcesso){
        return getDao().getActorIdTarefaAtual(idProcesso);
    }
    
    public String getUsuarioByTarefa(TaskInstance taskInstance) {
        return getDao().getUsuarioByTarefa(taskInstance);
    }
    
    public UsuarioLogin getUsuarioLoginByPessoaFisica(final PessoaFisica pessoaFisica) {
        return getDao().getUsuarioLoginByPessoaFisica(pessoaFisica);
    }

    private void validarPermanencia(final UsuarioLogin usuario) {
        if (!usuario.getProvisorio()) {
            usuario.setDataExpiracao(null);
        }
        if (!usuario.isHumano()){
            usuario.setPessoaFisica(null);
        }
        if (usuario.getSenha() == null || "".equals(usuario.getSenha())) {
            String senha = RandomStringUtils.randomAlphabetic(8);
            usuario.setSenha(senha);
        }
        if (usuario.getSalt() == null) {
            usuario.setSalt("");
        }
    }
    
    public UsuarioLogin createLDAPUser(final UsuarioLogin usuario) throws DAOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        validarPermanencia(usuario);
        final String password = usuario.getSenha();
        final Object id = EntityUtil.getIdValue(super.persist(usuario));
        final UsuarioLogin persisted = find(id);
        passwordService.changePassword(persisted, password);
        // ADICIONAR LOCALIZAÇÃO, PAPEL E ESTRUTURA PADRÃO
        return persisted;
    }
    
    @Override
    public UsuarioLogin persist(final UsuarioLogin usuario) throws DAOException {
        validarPermanencia(usuario);
        try {
            final Object id = EntityUtil.getIdValue(getDao().persist(usuario));
            final UsuarioLogin persisted = find(id);
            final String password = usuario.getSenha();
            passwordService.changePassword(persisted, password);
            accessMailService.enviarEmailDeMudancaDeSenha("email", persisted, password);
            return persisted;
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new DAOException(e);
        }
    }
    
}
