package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.UsuarioLoginQuery.ACTORID_TAREFA_ATUAL_BY_PROCESSO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.ID_PROCESSO_PARAM;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.INATIVAR_USUARIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.NOME_USUARIO_BY_ID_TASK_INSTANCE;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_EMAIL;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_ID;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_ID_TASK_INSTANCE;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_LOGIN;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_NR_CPF;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_LOCALIZACAO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_PAPEIS;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PARAM_PESSOA_FISICA;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_EMAIL;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_ID_TASK_INSTANCE;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_LOGIN_TASK_INSTANCE;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_PESSOA;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_FETCH_PF_BY_NUMERO_CPF;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_LOGIN_NAME;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_LOGIN_LOCALIZACAO_PAPEL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

@Name(UsuarioLoginDAO.NAME)
@AutoCreate
public class UsuarioLoginDAO extends DAO<UsuarioLogin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioLoginDAO";

    public UsuarioLogin getUsuarioLoginByEmail(String email) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_EMAIL, email);
        return getNamedSingleResult(USUARIO_BY_EMAIL, parameters);
    }

    public UsuarioLogin getUsuarioByLoginTaskInstance(Long idTaskInstance,
            String actorId) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_LOGIN, actorId);
        parameters.put(PARAM_ID_TASK_INSTANCE, idTaskInstance);
        return getNamedSingleResult(USUARIO_BY_LOGIN_TASK_INSTANCE, parameters);
    }

    public void inativarUsuario(UsuarioLogin usuario) throws DAOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID, usuario.getIdUsuarioLogin());
        executeNamedQueryUpdate(INATIVAR_USUARIO, parameters);
    }

    public UsuarioLogin getUsuarioLoginByLogin(String login) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_LOGIN, login);
        return getNamedSingleResult(USUARIO_LOGIN_NAME, parameters);
    }

    public String getActorIdTarefaAtual(Integer idProcesso) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ID_PROCESSO_PARAM, idProcesso);
        return getNamedSingleResult(ACTORID_TAREFA_ATUAL_BY_PROCESSO, parameters);
    }

    public String getUsuarioByTarefa(TaskInstance taskInstance) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_TASK_INSTANCE, taskInstance.getId());
        return getNamedSingleResult(USUARIO_BY_ID_TASK_INSTANCE, parameters);
    }
    
    public String getNomeUsuarioByTarefa(TaskInstance taskInstance) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_TASK_INSTANCE, taskInstance.getId());
        return getNamedSingleResult(NOME_USUARIO_BY_ID_TASK_INSTANCE, parameters);
    }

    public UsuarioLogin getUsuarioLoginByPessoaFisica(
            final PessoaFisica pessoaFisica) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_PESSOA_FISICA, pessoaFisica);
        return getNamedSingleResult(USUARIO_BY_PESSOA, parameters);
    }
    
    public UsuarioLogin getUsuarioFetchPessoaFisicaByCpf(String cpf){
    	Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_NR_CPF, cpf);
        return getNamedSingleResult(USUARIO_FETCH_PF_BY_NUMERO_CPF, parameters);
    }
    
    public List<UsuarioLogin> getUsuariosLoginLocalizacaoPapeis(Localizacao localizacao, String... papeis){
    	Map<String, Object> parameters = new HashMap<>();
    	parameters.put(PARAM_LOCALIZACAO, localizacao);
    	parameters.put(PARAM_PAPEIS, StringUtils.join(papeis, ","));
    	return getNamedResultList(USUARIO_LOGIN_LOCALIZACAO_PAPEL, parameters);
    }

}
