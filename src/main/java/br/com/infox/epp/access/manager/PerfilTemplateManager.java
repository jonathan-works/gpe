package br.com.infox.epp.access.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.dao.PerfilTemplateDAO;
import br.com.infox.epp.access.entity.PerfilTemplate;

@Name(PerfilTemplateManager.NAME)
@AutoCreate
public class PerfilTemplateManager extends Manager<PerfilTemplateDAO, PerfilTemplate> {
    
    private static final long serialVersionUID = 1L;
    public static final String NAME = "perfilTemplateManager";

}
