package br.com.infox.epp.processo.comunicacao.tipo.crud;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TipoComunicacaoSearch extends PersistenceController {

	public List<TipoComunicacao> getTiposComunicacaoAtivosByUso(TipoUsoComunicacaoEnum tipoUsoComunicacaoEnum) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<TipoComunicacao> cq = cb.createQuery(TipoComunicacao.class);
		Root<TipoComunicacao> from = cq.from(TipoComunicacao.class);
		cq.where(cb.isTrue(from.get(TipoComunicacao_.ativo)),
				cb.or(cb.equal(from.get(TipoComunicacao_.tipoUsoComunicacao), tipoUsoComunicacaoEnum),
					cb.equal(from.get(TipoComunicacao_.tipoUsoComunicacao), TipoUsoComunicacaoEnum.A)));
		cq.orderBy(cb.asc(from.get(TipoComunicacao_.descricao)));
		return getEntityManager().createQuery(cq).getResultList();
	}
	
	public TipoComunicacao getTiposComunicacaoAtivosByCodigo(String codigoTipoComunicacao, TipoUsoComunicacaoEnum tipoUsoComunicacaoEnum) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<TipoComunicacao> cq = cb.createQuery(TipoComunicacao.class);
		Root<TipoComunicacao> from = cq.from(TipoComunicacao.class);
		cq.where(cb.isTrue(from.get(TipoComunicacao_.ativo)),
				cb.equal(from.get(TipoComunicacao_.codigo), codigoTipoComunicacao),
				cb.or(cb.equal(from.get(TipoComunicacao_.tipoUsoComunicacao), tipoUsoComunicacaoEnum),
					cb.equal(from.get(TipoComunicacao_.tipoUsoComunicacao), TipoUsoComunicacaoEnum.A)));
		try {
			return getEntityManager().createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
