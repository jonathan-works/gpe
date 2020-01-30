package br.com.infox.epp.loglab.search;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.loglab.model.Empresa;
import br.com.infox.epp.loglab.model.Empresa_;
import br.com.infox.epp.loglab.vo.EmpresaVO;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EmpresaSearch extends PersistenceController {

    public Empresa findById(Long id) {
        Empresa empresa = getEntityManager().find(Empresa.class, id);
        return empresa;
    }

    public EmpresaVO getEmpresaVOByCnpj(String cnpj) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EmpresaVO> query = cb.createQuery(EmpresaVO.class);
        Root<Empresa> empresa = query.from(Empresa.class);
        query.select(cb.construct(query.getResultType(), empresa.get(Empresa_.id)));

        query.where(cb.equal(empresa.get(Empresa_.cnpj), cnpj));

        try {
            return getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
