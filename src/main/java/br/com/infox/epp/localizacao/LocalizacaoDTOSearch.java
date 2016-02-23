package br.com.infox.epp.localizacao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Estrutura_;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.localizacao.rest.LocalizacaoDTO;


@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LocalizacaoDTOSearch {

	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

	public List<LocalizacaoDTO> getLocalizacaoDTOList() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<LocalizacaoDTO> cq = cb.createQuery(LocalizacaoDTO.class);

		Root<Localizacao> localizacao = cq.from(Localizacao.class);
		Join<Localizacao, Localizacao> localizacaoPai = localizacao.join(Localizacao_.localizacaoPai, JoinType.LEFT);
		Join<Localizacao, Estrutura> estrutura = localizacao.join(Localizacao_.estruturaFilho, JoinType.LEFT);

		Selection<LocalizacaoDTO> select = cb.construct(LocalizacaoDTO.class, localizacao.get(Localizacao_.localizacao),
				localizacao.get(Localizacao_.codigo), localizacaoPai.get(Localizacao_.codigo),
				estrutura.get(Estrutura_.nome));

		Predicate localizacaoAtiva = cb.isTrue(localizacao.get(Localizacao_.ativo));

		Predicate predicate = cb.and(localizacaoAtiva);
		Order order = cb.asc(localizacao.get(Localizacao_.localizacao));

		return getEntityManager().createQuery(cq.select(select).where(predicate).orderBy(order)).getResultList();
	}

}
