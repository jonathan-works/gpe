package br.com.infox.epp.documento.pasta;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.fluxo.entity.ModeloPasta;
import br.com.infox.epp.fluxo.entity.ModeloPasta_;

@Stateless
public class ModeloPastaSearch implements Serializable {

    private static final long serialVersionUID = 1L;

    public List<ModeloPasta> modeloPastaWithDescricaoLike(String descricao) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ModeloPasta> cq = cb.createQuery(ModeloPasta.class);
        Root<ModeloPasta> modelo = cq.from(ModeloPasta.class);
        cq = cq.select(modelo);
        if (descricao != null && !descricao.trim().isEmpty()) {
            Predicate descricaoLike = cb.like(cb.lower(modelo.get(ModeloPasta_.nome)), "%" + descricao.toLowerCase() + "%");
            cq = cq.where(descricaoLike);
        }
        return getEntityManager().createQuery(cq).getResultList();
    }

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }

}
