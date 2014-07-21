package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.PerfilQuery.*;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.Perfil;

@Name(PerfilDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class PerfilDAO extends DAO<Perfil> {
    
    private static final long serialVersionUID = 1L;
    public static final String NAME = "perfilDAO";
    
    public boolean existePerfil(Integer idPerfil, Localizacao localizacao, Papel papel, Localizacao paiDaEstrutura) {
        Map<String, Object> params = new HashMap<>();
        params.put(LOCALIZACAO_PARAM, localizacao);
        params.put(PAPEL_PARAM, papel);
        String query = EXISTE_PERFIL_BASE_QUERY;
        if (paiDaEstrutura == null) {
            query += SEM_ESTRUTURA;
        } else {
            params.put(PAI_DA_ESTRUTURA_PARAM, paiDaEstrutura);
            query += COM_ESTRUTURA;
        }
        if (idPerfil != null) {
            params.put(ID_PERFIL_PARAM, idPerfil);
            query += COM_ID;
        }
        return (Long) getSingleResult(query, params) > 0;
    }

}
