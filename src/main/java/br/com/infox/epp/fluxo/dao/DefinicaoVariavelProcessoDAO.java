package br.com.infox.epp.fluxo.dao;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.DEFINICAO_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.DEFINICAO_BY_ID_PROCESSO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.DEFINICAO_VISIVEL_PAINEL_BY_ID_PROCESSO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.LIST_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.PARAM_FLUXO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.PARAM_ID_PROCESSO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.PARAM_NOME;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.TOTAL_BY_FLUXO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso_;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Scope(ScopeType.EVENT)
@AutoCreate
@Name(DefinicaoVariavelProcessoDAO.NAME)
public class DefinicaoVariavelProcessoDAO extends DAO<DefinicaoVariavelProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "definicaoVariavelProcessoDAO";

    @SuppressWarnings(UNCHECKED)
    public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo) {
        return createQueryVariaveisProcessoByFluxo(fluxo).getResultList();
    }

    @SuppressWarnings(UNCHECKED)
    public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo,
            int start, int count) {
        return createQueryVariaveisProcessoByFluxo(fluxo).setFirstResult(start).setMaxResults(count).getResultList();
    }

    public Long getTotalVariaveisByFluxo(Fluxo fluxo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_FLUXO, fluxo);
        return getNamedSingleResult(TOTAL_BY_FLUXO, parameters);
    }

    private Query createQueryVariaveisProcessoByFluxo(Fluxo fluxo) {
        return getEntityManager().createNamedQuery(LIST_BY_FLUXO, DefinicaoVariavelProcesso.class).setParameter(PARAM_FLUXO, fluxo);
    }

    public DefinicaoVariavelProcesso getDefinicao(Fluxo fluxo, String nome) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_FLUXO, fluxo);
        parameters.put(PARAM_NOME, nome);
        return getNamedSingleResult(DEFINICAO_BY_FLUXO, parameters);
    }
    
    public List<DefinicaoVariavelProcesso> getDefinicaoVariavelProcessoListByIdProcesso(Integer idProcesso) {
    	Map<String, Object> parameters = new HashMap<>(1);
        parameters.put(PARAM_ID_PROCESSO, idProcesso);
        return getNamedResultList(DEFINICAO_BY_ID_PROCESSO, parameters);
    }
    
    public List<DefinicaoVariavelProcesso> getDefinicaoVariavelProcessoVisivelPainel(Integer idProcesso) {
        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put(PARAM_ID_PROCESSO, idProcesso);
        return getNamedResultList(DEFINICAO_VISIVEL_PAINEL_BY_ID_PROCESSO, parameters);
    }
    
    public Integer getMaiorOrdem(Fluxo fluxo) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
    	Root<DefinicaoVariavelProcesso> root = query.from(DefinicaoVariavelProcesso.class);
    	query.select(cb.coalesce(cb.max(root.get(DefinicaoVariavelProcesso_.ordem)), 0));
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
