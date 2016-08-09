package br.com.infox.epp.localizacao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Localizacao_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LocalizacaoSearch {

	public Localizacao getLocalizacaoByCodigo(String codigoLocalizacao) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Localizacao> cq = cb.createQuery(Localizacao.class);
		Root<Localizacao> estrutura = cq.from(Localizacao.class);
		Predicate codigoIgual = cb.equal(estrutura.get(Localizacao_.codigo), codigoLocalizacao);
		Predicate ativo = cb.isTrue(estrutura.get(Localizacao_.ativo));
		cq = cq.select(estrutura).where(cb.and(codigoIgual, ativo));
		return getEntityManager().createQuery(cq).getSingleResult();
	}

    public List<Localizacao> getLocalizacoesExternasWithDescricaoLike(Localizacao localizacaoRaiz, String descricao) {
        CriteriaQuery<Localizacao> query = createQueryLocalizacaoExternaByRaizDescricao(localizacaoRaiz, descricao);
        return getEntityManager().createQuery(query).getResultList();
    }
    
    private CriteriaQuery<Localizacao> createQueryLocalizacaoExternaByRaizDescricao(Localizacao localizacaoRaiz, String descricao) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<Localizacao> query = cb.createQuery(Localizacao.class);
    	Root<Localizacao> from = query.from(Localizacao.class);
    	query.where(cb.isNull(from.get(Localizacao_.estruturaPai)),
    			cb.isTrue(from.get(Localizacao_.ativo)),
    			cb.like(cb.lower(from.get(Localizacao_.localizacao)), "%"+descricao.toLowerCase()+"%"));
    	if (localizacaoRaiz != null) {
    		query.where(query.getRestriction(),
    				cb.like(from.get(Localizacao_.caminhoCompleto), localizacaoRaiz.getCaminhoCompleto() + "%"));
    	}
    	query.orderBy(cb.asc(from.get(Localizacao_.caminhoCompleto)));
    	return query;
    }
    
    @SuppressWarnings("unchecked")
	public List<Localizacao> getLocalizacoesByRaizWithDescricaoLike(Localizacao localizacaoRaiz, String descricao, Integer maxResults) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<Localizacao> query = createQueryLocalizacaoExternaByRaizDescricao(localizacaoRaiz, descricao);
        Root<Localizacao> localizacao = (Root<Localizacao>) query.getRoots().iterator().next();
        query.where(query.getRestriction(),
        		cb.isNotNull(localizacao.get(Localizacao_.estruturaFilho)));
        return getEntityManager().createQuery(query).setMaxResults(maxResults).getResultList();
    }

	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

}
