package br.com.infox.epp.processo.sigilo.dao;

import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoPermissaoQuery.NAMED_QUERY_INATIVAR_PERMISSOES;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoPermissaoQuery.NAMED_QUERY_PERMISSOES_DO_SIGILO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoPermissaoQuery.NAMED_QUERY_USUARIO_POSSUI_PERMISSAO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoPermissaoQuery.QUERY_PARAM_SIGILO_PROCESSO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoPermissaoQuery.QUERY_PARAM_USUARIO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao;

@Stateless
@Name(SigiloProcessoPermissaoDAO.NAME)
@AutoCreate
public class SigiloProcessoPermissaoDAO extends DAO<SigiloProcessoPermissao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloProcessoPermissaoDAO";

    public boolean usuarioPossuiPermissao(UsuarioLogin usuario,
            SigiloProcesso sigiloProcesso) {
        Map<String, Object> params = new HashMap<>();
        params.put(QUERY_PARAM_SIGILO_PROCESSO, sigiloProcesso);
        params.put(QUERY_PARAM_USUARIO, usuario);
        return getNamedSingleResult(NAMED_QUERY_USUARIO_POSSUI_PERMISSAO, params) != null;
    }

    public void inativarPermissoes(SigiloProcesso sigiloProcesso) throws DAOException {
        Map<String, Object> params = new HashMap<>();
        params.put(QUERY_PARAM_SIGILO_PROCESSO, sigiloProcesso);
        executeNamedQueryUpdate(NAMED_QUERY_INATIVAR_PERMISSOES, params);
    }

    public List<SigiloProcessoPermissao> getPermissoes(
            SigiloProcesso sigiloProcesso) {
        Map<String, Object> params = new HashMap<>();
        params.put(QUERY_PARAM_SIGILO_PROCESSO, sigiloProcesso);
        return getNamedResultList(NAMED_QUERY_PERMISSOES_DO_SIGILO, params);
    }
}
