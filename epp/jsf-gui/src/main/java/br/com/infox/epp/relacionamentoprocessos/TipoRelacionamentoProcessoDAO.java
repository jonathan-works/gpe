package br.com.infox.epp.relacionamentoprocessos;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.entity.TipoRelacionamentoProcesso;
import br.com.infox.epp.processo.entity.TipoRelacionamentoProcesso_;

@Stateless
@AutoCreate
@Name(TipoRelacionamentoProcessoDAO.NAME)
public class TipoRelacionamentoProcessoDAO extends DAO<TipoRelacionamentoProcesso> {
    
    public static final String NAME = "tipoRelacionamentoProcessoDAO";
    private static final long serialVersionUID = 1L;
    
    public EntityManager getEntityManager() {
    	return EntityManagerProducer.getEntityManager();
    }
    
    public TipoRelacionamentoProcesso findByCodigo(String codigo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<TipoRelacionamentoProcesso> cq = cb.createQuery(TipoRelacionamentoProcesso.class);
        Root<TipoRelacionamentoProcesso> tipoRelacionamentoProcesso = cq.from(TipoRelacionamentoProcesso.class);
        cq.select(tipoRelacionamentoProcesso);        
        cq.where(
        		cb.equal(tipoRelacionamentoProcesso.get(TipoRelacionamentoProcesso_.codigo), codigo)
        );
        return getEntityManager().createQuery(cq).getSingleResult();
    	
    }    

}
