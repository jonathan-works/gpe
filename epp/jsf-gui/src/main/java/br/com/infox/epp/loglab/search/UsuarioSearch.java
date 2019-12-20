package br.com.infox.epp.loglab.search;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;

@Stateless
public class UsuarioSearch extends PersistenceController {
    
    public boolean isExisteUsuarioByNumeroCpf(String numeroCpf){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        
        Root<UsuarioLogin> usuarioLogin = query.from(UsuarioLogin.class);
        query.where(cb.equal(usuarioLogin.get(UsuarioLogin_.login), numeroCpf));
        
        query.select(cb.count(usuarioLogin));

        return getEntityManager().createQuery(query).getSingleResult() > 0;
    }
}
