package br.com.infox.epp.access.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.dao.PerfilTemplateDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate;

@Name(PerfilTemplateManager.NAME)
@AutoCreate
public class PerfilTemplateManager extends Manager<PerfilTemplateDAO, PerfilTemplate> {
    
    private static final long serialVersionUID = 1L;
    public static final String NAME = "perfilTemplateManager";
    
    @In
    private GenericDAO genericDAO;
    
    @Override
    public PerfilTemplate persist(PerfilTemplate o) throws DAOException {
        if (o.getLocalizacao() == null) {
            genericDAO.lock(o.getPapel());
            if (getDao().existsPerfilTemplate(o)) {
                throw new DAOException(DAOException.MSG_UNIQUE_VIOLATION);
            }
        }
        return super.persist(o);
    }
    
    public List<PerfilTemplate> listPerfisDentroDeEstrutura() {
        return getDao().listPerfisDentroDeEstrutura();
    }

    public PerfilTemplate getByLocalizacaoPapel(Localizacao localizacao, Papel papel) {
        return getDao().getByLocalizacaoPapel(localizacao,papel);
    }
    
    public PerfilTemplate getPerfilTemplateByDescricao(String descricao) {
    	return getDao().getPerfilTemplateByDescricao(descricao);
    }

    public PerfilTemplate getPerfilTemplateByLocalizacaoPaiDescricao(Localizacao localizacaoPai, String descricaoPerfil) {
    	return getDao().getPerfilTemplateByLocalizacaoPaiDescricao(localizacaoPai.getIdLocalizacao(), descricaoPerfil);
    }

}
