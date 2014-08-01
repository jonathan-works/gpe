package br.com.infox.epp.access.dao;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.PerfilTemplate;

@Name(PerfilTemplateDAO.NAME)
@AutoCreate
public class PerfilTemplateDAO extends DAO<PerfilTemplate> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "perfiltemplateDAO";
    
    public Boolean existsPerfilTemplate(PerfilTemplate perfilTemplate) {
        String hql = "select count(o) from PerfilTemplate o where papel = :papel and localizacao is null";
        Map<String, Object> param = new HashMap<>();
        param.put("papel", perfilTemplate.getPapel());
        return (Long) getSingleResult(hql, param) > 0;
    }
    

}
