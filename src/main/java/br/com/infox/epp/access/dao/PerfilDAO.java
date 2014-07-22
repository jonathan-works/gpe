package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.PerfilQuery.CAMINHO_COMPLETO_PARAM;
import static br.com.infox.epp.access.query.PerfilQuery.COM_ESTRUTURA;
import static br.com.infox.epp.access.query.PerfilQuery.COM_ID;
import static br.com.infox.epp.access.query.PerfilQuery.EXISTE_PERFIL_BASE_QUERY;
import static br.com.infox.epp.access.query.PerfilQuery.EXISTE_PERFIL_COM_HIERARQUIA_LOCALIZACAO;
import static br.com.infox.epp.access.query.PerfilQuery.ID_PERFIL_PARAM;
import static br.com.infox.epp.access.query.PerfilQuery.LIST_PERFIS_DENTRO_DE_ESTRUTURA;
import static br.com.infox.epp.access.query.PerfilQuery.LOCALIZACAO_PARAM;
import static br.com.infox.epp.access.query.PerfilQuery.PAI_DA_ESTRUTURA_PARAM;
import static br.com.infox.epp.access.query.PerfilQuery.PAPEL_PARAM;
import static br.com.infox.epp.access.query.PerfilQuery.SEM_ESTRUTURA;

import java.util.HashMap;
import java.util.List;
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

    public boolean existePerfilComHierarquiaLocalizacao(Localizacao localizacao) {
        Map<String, Object> params = new HashMap<>();
        params.put(CAMINHO_COMPLETO_PARAM, localizacao.getCaminhoCompleto());
        return ((Number) getNamedSingleResult(EXISTE_PERFIL_COM_HIERARQUIA_LOCALIZACAO, params)).longValue() > 0;
    }
    
    public List<Perfil> listPerfisDentroDeEstrutura() {
        return getNamedResultList(LIST_PERFIS_DENTRO_DE_ESTRUTURA);
    }
}
