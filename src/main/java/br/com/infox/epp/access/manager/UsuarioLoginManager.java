package br.com.infox.epp.access.manager;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.util.RandomStringUtils;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.mail.service.AccessMailService;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Name(UsuarioLoginManager.NAME)
@Stateless
@AutoCreate
public class UsuarioLoginManager extends Manager<UsuarioLoginDAO, UsuarioLogin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioLoginManager";
    private static final LogProvider LOG = Logging.getLogProvider(UsuarioLoginManager.class);

    @In
    private PasswordService passwordService;
    @In
    private AccessMailService accessMailService;

    public boolean usuarioExpirou(final UsuarioLogin usuarioLogin) {
        boolean result = Boolean.FALSE;
        if (usuarioLogin != null) {
            final Date dataExpiracao = usuarioLogin.getDataExpiracao();
            result = usuarioLogin.getProvisorio() && dataExpiracao != null
                    && dataExpiracao.before(new Date());
        }
        return result;
    }
    
    public void inativarUsuario(final UsuarioLogin usuario) throws DAOException {
        getDao().inativarUsuario(usuario);
    }

    public UsuarioLogin getUsuarioLoginByEmail(final String email) {
        return getDao().getUsuarioLoginByEmail(email);
    }

    public UsuarioLogin getUsuarioLoginByLogin(final String login) {
        return getDao().getUsuarioLoginByLogin(login);
    }

    public String getActorIdTarefaAtual(Integer idProcesso) {
        return getDao().getActorIdTarefaAtual(idProcesso);
    }

    public String getUsuarioByTarefa(TaskInstance taskInstance) {
        return getDao().getUsuarioByTarefa(taskInstance);
    }
    
    public String getNomeUsuarioByTarefa(TaskInstance taskInstance) {
        return getDao().getNomeUsuarioByTarefa(taskInstance);
    }

    public UsuarioLogin getUsuarioLoginByPessoaFisica(
            final PessoaFisica pessoaFisica) {
        return getDao().getUsuarioLoginByPessoaFisica(pessoaFisica);
    }
    
    public UsuarioLogin getUsuarioFetchPessoaFisicaByNrCpf(String nrCpf){
    	return getDao().getUsuarioFetchPessoaFisicaByCpf(nrCpf);
    }

    private void validarPermanencia(final UsuarioLogin usuario) {
        if (usuario.getProvisorio() == null || !usuario.getProvisorio()) {
            usuario.setDataExpiracao(null);
        }
        if (!usuario.isHumano()) {
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

    /**
     * @throws IllegalArgumentException
     * */
    public UsuarioLogin createLDAPUser(final UsuarioLogin usuario) throws DAOException, IllegalAccessException, InvocationTargetException {
        validarPermanencia(usuario);
        final String password = usuario.getSenha();
        final Object id = EntityUtil.getIdValue(super.persist(usuario));
        final UsuarioLogin persisted = find(id);
        passwordService.changePassword(persisted, password);
        //TODO ADICIONAR LOCALIZAÇÃO, PAPEL E ESTRUTURA PADRÃO?
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
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new DAOException(e);
        }
    }
    
    public UsuarioLogin getUsuarioDeProcessosDoSistema() {
    	String idUsuarioSistema = ParametroUtil.getParametroOrFalse(Parametros.ID_USUARIO_PROCESSO_SISTEMA.getLabel());
    	if (idUsuarioSistema == null || "false".equals(idUsuarioSistema)) {
    		String mensagem = "Não foi configurado o usuário de processos do sistema";
			LOG.error(mensagem);
    		throw new BusinessException(mensagem);
    	} else {
    		UsuarioLogin usuario = find(Integer.parseInt(idUsuarioSistema));
    		if (!usuario.isHumano()) {
    			return usuario;
    		} else {
    			String mensagem = "Usuario " + usuario + "não é um usuário de sistema";
				LOG.error(mensagem);
    			throw new BusinessException(mensagem);
    		}
    	}
    }
    
    
    public List<UsuarioLogin> getUsuariosLogin(Localizacao localizacao, String... papeis){
    	return getDao().getUsuariosLoginLocalizacaoPapeis(localizacao, papeis);
    }

}
