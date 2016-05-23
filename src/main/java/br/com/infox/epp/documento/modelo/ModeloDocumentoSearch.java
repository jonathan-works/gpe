package br.com.infox.epp.documento.modelo;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento_;

@Stateless
public class ModeloDocumentoSearch implements Serializable{

    private static final long serialVersionUID = 1L;

    public List<ModeloDocumento> getModeloDocumentoWithTituloLike(String titulo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ModeloDocumento> cq = cb.createQuery(ModeloDocumento.class);
        Root<ModeloDocumento> modelo = cq.from(ModeloDocumento.class);

        cq = cq.select(modelo).where(cb.like(cb.lower(modelo.get(ModeloDocumento_.tituloModeloDocumento)),
                cb.lower(cb.literal("%" + titulo.toLowerCase() + "%"))));
        return getEntityManager().createQuery(cq).getResultList();
    }

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
    
    

}
