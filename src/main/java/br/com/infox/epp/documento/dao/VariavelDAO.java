package br.com.infox.epp.documento.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.infox.epp.documento.entity.VariavelTipoModelo;
import br.com.infox.epp.documento.entity.VariavelTipoModelo_;
import br.com.infox.epp.documento.entity.Variavel_;

@Name(VariavelDAO.NAME)
@AutoCreate
public class VariavelDAO extends DAO<Variavel> {

    public static final String NAME = "variavelDAO";
    private static final long serialVersionUID = 1L;

    public List<Variavel> getVariaveisByTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
    	return getEntityManager().createQuery(createQueryVariaveisByTipoModeloDocumento(tipoModeloDocumento)).getResultList();
    }

    @SuppressWarnings("unchecked")
	public List<Variavel> getVariaveisAtivasByTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
        CriteriaQuery<Variavel> query = createQueryVariaveisByTipoModeloDocumento(tipoModeloDocumento);
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<Variavel> variavel = (Root<Variavel>) query.getRoots().iterator().next();
        query.where(query.getRestriction(), cb.isTrue(variavel.get(Variavel_.ativo)));
        return getEntityManager().createQuery(query).getResultList();
    }
    
    private CriteriaQuery<Variavel> createQueryVariaveisByTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Variavel> query = cb.createQuery(Variavel.class);
        Root<Variavel> variavel = query.from(Variavel.class);
        Join<Variavel, VariavelTipoModelo> variavelTipoModelo = variavel.join(Variavel_.variavelTipoModeloList);
        query.where(cb.equal(variavelTipoModelo.get(VariavelTipoModelo_.tipoModeloDocumento), tipoModeloDocumento));
        query.orderBy(cb.asc(variavel.get(Variavel_.variavel)));
        return query;
    }
}
