package br.com.infox.epp.processo.prioridade.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso_;
import br.com.infox.epp.processo.prioridade.query.PrioridadeProcessoQuery;

@Stateless
@AutoCreate
@Name(PrioridadeProcessoDAO.NAME)
public class PrioridadeProcessoDAO extends DAO<PrioridadeProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "prioridadeProcessoDAO";
    
    public List<PrioridadeProcesso> listPrioridadesAtivas() {
        return getNamedResultList(PrioridadeProcessoQuery.NAMED_QUERY_PRIORIDADES_ATIVAS);
    }
    
    public List<PrioridadeProcesso> getPrioridadesAtivasWithDescricao(String descricao, Integer maxResult) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PrioridadeProcesso> query = cb.createQuery(PrioridadeProcesso.class);
        Root<PrioridadeProcesso> from = query.from(PrioridadeProcesso.class);
        query.where(cb.isTrue(from.get(PrioridadeProcesso_.ativo)));
        if (descricao != null && !descricao.isEmpty()) {
            query.where(query.getRestriction(), 
                    cb.like(cb.lower(from.get(PrioridadeProcesso_.descricaoPrioridade)), cb.literal("%" + descricao.toLowerCase() + "%")));
        }
        return getEntityManager().createQuery(query).setMaxResults(maxResult).getResultList();
    }
}
