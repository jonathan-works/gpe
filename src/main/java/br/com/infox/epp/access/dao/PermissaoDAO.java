package br.com.infox.epp.access.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Permissao;
import br.com.infox.epp.access.entity.Permissao_;

@Name(PermissaoDAO.NAME)
@AutoCreate
public class PermissaoDAO extends DAO<Permissao> {
    public static final String NAME = "PermissaoDAO";
    private static final long serialVersionUID = 1L;

    public List<Permissao> getByAlvo(String alvo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Permissao> cq = cb.createQuery(Permissao.class);
        Root<Permissao> from = cq.from(Permissao.class);
        Predicate where = cb.equal(from.get(Permissao_.alvo), alvo);
        cq.select(from);
        cq.where(where);
        return getEntityManager().createQuery(cq).getResultList();
    }
}
