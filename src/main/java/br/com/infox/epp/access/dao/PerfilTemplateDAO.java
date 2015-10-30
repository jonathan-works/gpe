package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.PerfilTemplateQuery.LIST_PERFIS_DENTRO_DE_ESTRUTURA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.query.PerfilTemplateQuery;

@Name(PerfilTemplateDAO.NAME)
@AutoCreate
public class PerfilTemplateDAO extends DAO<PerfilTemplate> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "perfilTemplateDAO";

    public Boolean existsPerfilTemplate(PerfilTemplate perfilTemplate) {
        String hql = "select count(o) from PerfilTemplate o where papel = :papel and localizacao is null";
        Map<String, Object> param = new HashMap<>();
        param.put("papel", perfilTemplate.getPapel());
        return (Long) getSingleResult(hql, param) > 0;
    }
    
    public PerfilTemplate getPerfilTemplateByDescricao(String descricao) {
    	Map<String, Object> param = new HashMap<>(1);
        param.put(PerfilTemplateQuery.PARAM_DESCRICAO, descricao);
        return getNamedSingleResult(PerfilTemplateQuery.GET_BY_DESCRICAO, param);
    }

    public List<PerfilTemplate> listPerfisDentroDeEstrutura() {
        return getNamedResultList(LIST_PERFIS_DENTRO_DE_ESTRUTURA);
    }

    public PerfilTemplate getByLocalizacaoPapel(Localizacao localizacao,
            Papel papel) {
        final Map<String, Object> param = new HashMap<>();
        param.put(PerfilTemplateQuery.PARAM_LOCALIZACAO, localizacao);
        param.put(PerfilTemplateQuery.PARAM_PAPEL, papel);
        return getNamedSingleResult(
                PerfilTemplateQuery.GET_BY_LOCALIZACAO_PAPEL, param);
    }
    
    public PerfilTemplate getPerfilTemplateByLocalizacaoPaiDescricao(Integer idLocalizacao, String descricaoPerfil) {
    	Map<String, Object> param = new HashMap<>(1);
        param.put(PerfilTemplateQuery.PARAM_DESCRICAO, descricaoPerfil);
        param.put(PerfilTemplateQuery.PARAM_LOCALIZACAO, idLocalizacao);
        return getNamedSingleResult(PerfilTemplateQuery.GET_BY_LOCALIZACAO_PAI_DESCRICAO, param);
    }    

}
