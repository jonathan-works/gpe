package br.com.infox.epp.system.dao;

import static br.com.infox.epp.system.query.ParametroQuery.EXISTE_PARAMETRO;
import static br.com.infox.epp.system.query.ParametroQuery.LIST_PARAMETROS_ATIVOS;
import static br.com.infox.epp.system.query.ParametroQuery.PARAMETRO_BY_NOME;
import static br.com.infox.epp.system.query.ParametroQuery.PARAMETRO_BY_VALOR;
import static br.com.infox.epp.system.query.ParametroQuery.PARAM_NOME;
import static br.com.infox.epp.system.query.ParametroQuery.PARAM_VALOR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.system.entity.Parametro;

@AutoCreate
@Name(ParametroDAO.NAME)
@Stateless
public class ParametroDAO extends DAO<Parametro> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "parametroDAO";

    public Parametro getParametroByNomeVariavel(String nomeVariavel) {
        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put(PARAM_NOME, nomeVariavel);
        return getNamedSingleResult(PARAMETRO_BY_NOME, parameters);
    }

    public Parametro getParametroByValorVariavel(String valorVariavel) {
        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put(PARAM_VALOR, valorVariavel);
        return getNamedSingleResult(PARAMETRO_BY_VALOR, parameters);
    }
    
    public List<Parametro> listParametrosAtivos() {
        return getNamedResultList(LIST_PARAMETROS_ATIVOS);
    }

    public boolean existeParametro(String nome) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_NOME, nome);
        return getNamedSingleResult(EXISTE_PARAMETRO, params) != null;
    }

    public Parametro removeByValue(String value) throws DAOException {
        Parametro p = getParametroByValorVariavel(value);
        return remove(p);
    }
}
