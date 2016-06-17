package br.com.infox.epp.documento.modelo;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ModeloDocumentoSearch extends PersistenceController {

    public List<ModeloDocumento> getModeloDocumentoWithTituloLike(String titulo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ModeloDocumento> cq = cb.createQuery(ModeloDocumento.class);
        Root<ModeloDocumento> modelo = cq.from(ModeloDocumento.class);

        cq = cq.select(modelo).where(cb.like(cb.lower(modelo.get(ModeloDocumento_.tituloModeloDocumento)),
                cb.lower(cb.literal("%" + titulo.toLowerCase() + "%"))));
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public ModeloDocumento getModeloDocumentoByCodigo(String codigo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ModeloDocumento> cq = cb.createQuery(ModeloDocumento.class);
		Root<ModeloDocumento> fromModelo = cq.from(ModeloDocumento.class);
		cq.where(cb.equal(fromModelo.get(ModeloDocumento_.codigo), cb.literal(codigo)));
		List<ModeloDocumento> result = getEntityManager().createQuery(cq).getResultList();
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}

}
