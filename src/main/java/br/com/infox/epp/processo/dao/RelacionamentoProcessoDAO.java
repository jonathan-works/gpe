package br.com.infox.epp.processo.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso.TipoProcesso;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso_;
import br.com.infox.epp.processo.entity.Relacionamento_;

@AutoCreate
@Name(RelacionamentoProcessoDAO.NAME)
@Stateless
public class RelacionamentoProcessoDAO extends DAO<RelacionamentoProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "relacionamentoProcessoDAO";

    public boolean existeRelacionamento(String processo1, TipoProcesso tipoProcesso1, String processo2, TipoProcesso tipoProcesso2) {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("processo1", processo1);
        parameters.put("processo2", processo2);
        parameters.put("tipoProcesso1", tipoProcesso1);
        parameters.put("tipoProcesso2", tipoProcesso2);
        final String query = "select r.idRelacionamento from RelacionamentoProcesso rp inner join rp.relacionamento r, RelacionamentoProcesso rp2 inner join rp2.relacionamento r2 "
        		+ "where rp.numeroProcesso=:processo1 and rp.tipoProcesso=:tipoProcesso1 "
        		+ "and rp2.numeroProcesso=:processo2 and rp2.tipoProcesso=:tipoProcesso2 "
        		+ "and r.idRelacionamento = r2.idRelacionamento group by r having count(r)>0";
        final Integer result = getSingleResult(query, parameters);
        return result != null;
    }
    
    /**
     * Retorna uma lista contendo todos os processos relacionados ao processo informado 
     * @param tipoProcesso Retorna apenas processos do tipo informado (caso seja nulo todos os processos relacionados são retornados)
     */
    protected List<RelacionamentoProcesso> getListProcessosRelacionados(Processo processo, TipoProcesso tipoProcesso) {
    	EntityManager entityManager = getEntityManager();
    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    	CriteriaQuery<RelacionamentoProcesso> query = cb.createQuery(RelacionamentoProcesso.class);
    	Root<RelacionamentoProcesso> root = query.from(RelacionamentoProcesso.class);
    	Join<RelacionamentoProcesso, Relacionamento> relacionamento = root.join(RelacionamentoProcesso_.relacionamento);
    	Join<Relacionamento, RelacionamentoProcesso> processosRelacionados = relacionamento.join(Relacionamento_.relacionamentosProcessos);
    	
    	List<Predicate> condicoes = new ArrayList<>();
    	condicoes.add(
    			cb.and(
    					cb.equal(root.get(RelacionamentoProcesso_.processo), processo),
    					cb.equal(root.get(RelacionamentoProcesso_.tipoProcesso), TipoProcesso.ELE)
    			)
    	);
    	condicoes.add(cb.notEqual(root.get(RelacionamentoProcesso_.idRelacionamentoProcesso), processosRelacionados.get(RelacionamentoProcesso_.idRelacionamentoProcesso)));
    	if(tipoProcesso != null) {
        	condicoes.add(cb.notEqual(root.get(RelacionamentoProcesso_.idRelacionamentoProcesso), processosRelacionados.get(RelacionamentoProcesso_.idRelacionamentoProcesso)));    		
    	}    	
    	query.where(condicoes.toArray(new Predicate[0]));
    	
    	query.select(processosRelacionados);
    	return entityManager.createQuery(query).getResultList();
    }
    
    public List<RelacionamentoProcesso> getListProcessosRelacionados(Processo processo) {
    	return getListProcessosRelacionados(processo, null);    	
    }
    
    public List<RelacionamentoProcesso> getListProcessosFisicosRelacionados(Processo processo) {
    	return getListProcessosRelacionados(processo, TipoProcesso.FIS);
    }
    
    public List<RelacionamentoProcesso> getListProcessosEletronicosRelacionados(Processo processo) {
    	return getListProcessosRelacionados(processo, TipoProcesso.ELE);    	
    }
}
