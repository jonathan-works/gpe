package br.com.infox.epp.access.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioPerfil;

@Name(UsuarioPerfilDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class UsuarioPerfilDAO extends DAO<UsuarioPerfil> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioPerfilDAO";
    
    public List<PerfilTemplate> getPerfisPermitidos(Localizacao localizacao) {
        String hql;
        Map<String, Object> params = new HashMap<>();
        if (localizacao.getEstruturaFilho() != null) {
            hql = "select o from PerfilTemplate o where o.localizacao.estruturaPai = :estruturaFilho"; 
            params.put("estruturaFilho", localizacao.getEstruturaFilho());
        } else {
            hql = "select o from PerfilTemplate o where o.localizacao is null";
        }
        return getResultList(hql, params);
    }

}
