package br.com.infox.epp.system.dao;

import static br.com.infox.epp.system.query.ParametroQuery.EXISTE_PARAMETRO;
import static br.com.infox.epp.system.query.ParametroQuery.LIST_PARAMETROS_ATIVOS;
import static br.com.infox.epp.system.query.ParametroQuery.PARAM_NOME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.system.entity.Parametro;

@Name(ParametroDAO.NAME)
@AutoCreate
public class ParametroDAO extends DAO<Parametro> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "parametroDAO";

    public Parametro getParametroByNomeVariavel(String nomeVariavel) {
        final String hql = "select p from Parametro p where nomeVariavel = :nomeVariavel";
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("nomeVariavel", nomeVariavel);
        return getSingleResult(hql, parameters);
    }

    public List<Parametro> listParametrosAtivos() {
        return getNamedResultList(LIST_PARAMETROS_ATIVOS);
    }

    public boolean existeParametro(String nome) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_NOME, nome);
        return getNamedSingleResult(EXISTE_PARAMETRO, params) != null;
    }
}
