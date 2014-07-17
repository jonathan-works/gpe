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
    
    public boolean existePerfil(Localizacao localizacao, Papel papel, Localizacao paiDaEstrutura) {
        Map<String, Object> params = new HashMap<>();
        params.put(LOCALIZACAO_PARAM, localizacao);
        params.put(PAPEL_PARAM, papel);
        Long count;
        if (paiDaEstrutura == null) {
            count = getSingleResult(EXISTE_PERFIL_BASE_QUERY + SEM_ESTRUTURA, params);
        } else {
            params.put(PAI_DA_ESTRUTURA_PARAM, paiDaEstrutura);
            count = getSingleResult(EXISTE_PERFIL_BASE_QUERY + COM_ESTRUTURA, params);
        }
        return count > 0;
    }

}
