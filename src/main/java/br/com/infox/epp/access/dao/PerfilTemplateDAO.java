package br.com.infox.epp.access.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.PerfilTemplate;

@Name(PerfilTemplateDAO.NAME)
@AutoCreate
public class PerfilTemplateDAO extends DAO<PerfilTemplate> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "perfiltemplateDAO";
    

}
