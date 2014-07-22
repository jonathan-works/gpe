package br.com.infox.epp.fluxo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Perfil;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.RaiaPerfil;
import br.com.infox.epp.fluxo.query.RaiaPerfilQuery;

@Name(RaiaPerfilDAO.NAME)
@AutoCreate
public class RaiaPerfilDAO extends DAO<RaiaPerfil> {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "raiaPerfilDAO";

    public List<RaiaPerfil> listByPerfil(Perfil perfil) {
        Map<String, Object> params = new HashMap<>();
        params.put(RaiaPerfilQuery.QUERY_PARAM_PERFIL, perfil);
        return getNamedResultList(RaiaPerfilQuery.LIST_BY_PERFIL, params);
    }

    public List<RaiaPerfil> listByLocalizacao(Localizacao localizacao) {
        Map<String, Object> params = new HashMap<>();
        params.put(RaiaPerfilQuery.QUERY_PARAM_LOCALIZACAO, localizacao);
        return getNamedResultList(RaiaPerfilQuery.LIST_BY_LOCALIZACAO, params);
    }

    public void removerRaiaPerfisDoFluxo(Fluxo fluxo) throws DAOException {
        Map<String, Object> params = new HashMap<>();
        params.put(RaiaPerfilQuery.QUERY_PARAM_FLUXO, fluxo);
        executeNamedQueryUpdate(RaiaPerfilQuery.REMOVER_RAIAS_PERFIS_POR_FLUXO, params);
    }
    
    public boolean existeRaiaPerfilComHierarquiaLocalizacao(Localizacao localizacao) {
        Map<String, Object> params = new HashMap<>();
        params.put(RaiaPerfilQuery.QUERY_PARAM_CAMINHO_COMPLETO, localizacao.getCaminhoCompleto());
        return ((Number) getNamedSingleResult(RaiaPerfilQuery.EXISTE_RAIA_PERFIL_COM_HIERARQUIA_LOCALIZACAO, params)).longValue() > 0;
    }
}
