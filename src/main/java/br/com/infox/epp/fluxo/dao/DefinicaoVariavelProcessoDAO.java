package br.com.infox.epp.fluxo.dao;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.DEFINICAO_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.DEFINICAO_BY_ID_PROCESSO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.LIST_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.PARAM_FLUXO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.PARAM_ID_PROCESSO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.PARAM_NOME;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.*;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso_;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DefinicaoVariavelProcessoDAO extends Dao<DefinicaoVariavelProcesso, Long> {

    public DefinicaoVariavelProcessoDAO() {
    	super(DefinicaoVariavelProcesso.class);
	}

    @SuppressWarnings(UNCHECKED)
    public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo) {
        return createQueryVariaveisProcessoByFluxo(fluxo).getResultList();
    }

    @SuppressWarnings(UNCHECKED)
    public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo, int start, int count) {
        return createQueryVariaveisProcessoByFluxo(fluxo).setFirstResult(start).setMaxResults(count).getResultList();
    }

    public Long getTotalVariaveisByFluxo(Fluxo fluxo) {
        return getEntityManager().createNamedQuery(TOTAL_BY_FLUXO, Long.class).setParameter(PARAM_FLUXO, fluxo).setMaxResults(1).getResultList().get(0);
    }

    private Query createQueryVariaveisProcessoByFluxo(Fluxo fluxo) {
        return getEntityManager().createNamedQuery(LIST_BY_FLUXO, DefinicaoVariavelProcesso.class).setParameter(PARAM_FLUXO, fluxo);
    }

    public DefinicaoVariavelProcesso getDefinicao(Fluxo fluxo, String nome) {
        				List<DefinicaoVariavelProcesso> resultList = getEntityManager().createNamedQuery(DEFINICAO_BY_FLUXO, DefinicaoVariavelProcesso.class).setParameter(PARAM_FLUXO, fluxo)
        		.setParameter(PARAM_NOME, nome).setMaxResults(1).getResultList();
        				return resultList == null || resultList.isEmpty() ? null : resultList.get(0);
    }
    
    public List<DefinicaoVariavelProcesso> getDefinicaoVariavelProcessoListByIdProcesso(Integer idProcesso) {
    	return getEntityManager().createNamedQuery(DEFINICAO_BY_ID_PROCESSO, DefinicaoVariavelProcesso.class)
    			.setParameter(PARAM_ID_PROCESSO, idProcesso).getResultList();
    }
    
    public List<DefinicaoVariavelProcesso> getDefinicaoVariavelProcessoVisivelPainel(Integer idFluxo) {
    	return getEntityManager().createNamedQuery(DEFINICAO_VISIVEL_PAINEL_BY_ID_FLUXO, DefinicaoVariavelProcesso.class)
    			.setParameter(PARAM_FLUXO, idFluxo).getResultList();
    }
    
    public Integer getMaiorOrdem(Fluxo fluxo) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
    	Root<DefinicaoVariavelProcesso> root = query.from(DefinicaoVariavelProcesso.class);
    	query.select(cb.coalesce(cb.max(root.get(DefinicaoVariavelProcesso_.ordem)), -1));
    	query.where(cb.equal(root.get(DefinicaoVariavelProcesso_.fluxo), fluxo));
    	return getEntityManager().createQuery(query).getSingleResult();
    }
    
    @Override
    public DefinicaoVariavelProcesso remove(DefinicaoVariavelProcesso object) throws DAOException {
    	Fluxo fluxo = object.getFluxo();
    	DefinicaoVariavelProcesso ret = super.remove(object);
    	String hql = "update DefinicaoVariavelProcesso o set o.ordem = o.ordem - 1 where o.fluxo = :fluxo and o.ordem > :ordem";
    	try {
    		getEntityManager().createQuery(hql).setParameter("fluxo", fluxo).setParameter("ordem", object.getOrdem()).executeUpdate();
    	} catch (Exception e) {
    		throw new DAOException(e);
    	}
    	return ret;
    }
}
