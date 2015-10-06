package br.com.infox.epp.processo.sigilo.dao;

import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.NAMED_QUERY_SIGILO_PROCESSO_ATIVO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.NAMED_QUERY_SIGILO_PROCESSO_USUARIO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.QUERY_PARAM_PROCESSO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.QUERY_PARAM_USUARIO_LOGIN;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;

@AutoCreate
@Name(SigiloProcessoDAO.NAME)
public class SigiloProcessoDAO extends DAO<SigiloProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloProcessoDAO";

    public SigiloProcesso getSigiloProcessoAtivo(Processo processo) {
        Map<String, Object> params = new HashMap<>();
        params.put(QUERY_PARAM_PROCESSO, processo);
        return getNamedSingleResult(NAMED_QUERY_SIGILO_PROCESSO_ATIVO, params);
    }
    
    public SigiloProcesso getSigiloProcessoUsuario(Processo processo, UsuarioLogin usuarioLogin) {
    	Map<String, Object> params = new HashMap<>();
    	params.put(QUERY_PARAM_PROCESSO, processo);
    	params.put(QUERY_PARAM_USUARIO_LOGIN, usuarioLogin);
    	return getNamedSingleResult(NAMED_QUERY_SIGILO_PROCESSO_USUARIO, params);
    }
}
