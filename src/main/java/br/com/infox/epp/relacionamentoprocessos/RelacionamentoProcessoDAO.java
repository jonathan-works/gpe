package br.com.infox.epp.relacionamentoprocessos;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoExterno;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno_;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso_;
import br.com.infox.epp.processo.entity.Relacionamento_;

@Stateless
@AutoCreate
@Name(RelacionamentoProcessoDAO.NAME)
public class RelacionamentoProcessoDAO extends DAO<RelacionamentoProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "relacionamentoProcessoDAO";
    
    public boolean existeRelacionamento(RelacionamentoProcessoInterno rel1, RelacionamentoProcesso rel2) {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("processo1", rel1.getProcesso());
        
        String query = "select r.idRelacionamento from RelacionamentoProcessoInterno rp inner join rp.relacionamento r, "
        		+ rel2.getClass().getSimpleName() + " rp2 inner join rp2.relacionamento r2 "
        		+ "where r.idRelacionamento = r2.idRelacionamento "        		
        		+ "and rp.processo=:processo1 ";
        		if(rel2 instanceof RelacionamentoProcessoInterno) {
        			query += "and rp2.processo=:processo2 ";
        	        parameters.put("processo2", ((RelacionamentoProcessoInterno) rel2).getProcesso());
        		}
        		else if(rel2 instanceof RelacionamentoProcessoExterno)
        		{
        			query += "and rp2.numeroProcesso=:processo2 ";
        	        parameters.put("processo2", ((RelacionamentoProcessoExterno) rel2).getNumeroProcesso());        			
        		}
        		else
        		{
        			throw new UnsupportedOperationException();
        		}
        		query += "group by r having count(r)>0";
        final Integer result = getSingleResult(query, parameters);
        return result != null;
    }
    
    @SuppressWarnings("unchecked")
	public <T extends RelacionamentoProcesso> List<T> getListProcessosRelacionados(Processo processo, Class<T> classe) {
    	EntityManager entityManager = getEntityManager();
    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    	CriteriaQuery<T> query = (CriteriaQuery<T>) cb.createQuery(classe == null ? RelacionamentoProcesso.class : classe);
    	Root<RelacionamentoProcessoInterno> root = query.from(RelacionamentoProcessoInterno.class);
    	Join<RelacionamentoProcessoInterno, Relacionamento> relacionamento = root.join(RelacionamentoProcesso_.relacionamento);
		Join<Relacionamento, T> processosRelacionados = (Join<Relacionamento, T>)relacionamento.join(Relacionamento_.relacionamentosProcessos);
    	
    	query.where(
				cb.equal(root.get(RelacionamentoProcessoInterno_.processo), processo),
				cb.notEqual(root.get(RelacionamentoProcesso_.idRelacionamentoProcesso), processosRelacionados.get(RelacionamentoProcesso_.idRelacionamentoProcesso)),
				cb.equal(processosRelacionados.type(), classe)
    	);
    	
    	query.select(processosRelacionados);
    	return entityManager.createQuery(query).getResultList();
    }
    
    public List<RelacionamentoProcessoInterno> getListProcessosEletronicosRelacionados(Processo processo) {
    	return getListProcessosRelacionados(processo, RelacionamentoProcessoInterno.class);    	
    }
}
