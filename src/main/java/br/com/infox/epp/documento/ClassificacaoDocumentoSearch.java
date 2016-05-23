package br.com.infox.epp.documento;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento_;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;

@Stateless
public class ClassificacaoDocumentoSearch implements Serializable {

    private static final long serialVersionUID = 1L;

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }

    public List<ClassificacaoDocumento> findClassificacaoDocumentoWithDescricaoLike(String pattern) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ClassificacaoDocumento> cq = cb.createQuery(ClassificacaoDocumento.class);
        Root<ClassificacaoDocumento> classificacao = cq.from(ClassificacaoDocumento.class);

        Predicate like = cb.like(cb.lower(classificacao.get(ClassificacaoDocumento_.descricao)), cb.lower(cb.literal("%" + pattern.toLowerCase() + "%")));
        Predicate anexos = classificacao.get(ClassificacaoDocumento_.inTipoDocumento).in(TipoDocumentoEnum.D,TipoDocumentoEnum.T);
        cq = cq.select(classificacao).where(like, anexos);

        return em.createQuery(cq).getResultList();
    }

}
