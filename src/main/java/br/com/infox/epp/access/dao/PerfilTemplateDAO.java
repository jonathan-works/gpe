package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.PerfilTemplateQuery.LIST_PERFIS_DENTRO_DE_ESTRUTURA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.PerfilTemplate;

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
    
    public List<PerfilTemplate> listPerfisDentroDeEstrutura() {
        return getNamedResultList(LIST_PERFIS_DENTRO_DE_ESTRUTURA);
    }
    

}
