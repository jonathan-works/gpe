package br.com.infox.epp.tipoParticipante;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.entity.TipoParte_;
import br.com.infox.epp.tipoParticipante.rest.TipoParticipanteDTO;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TipoParticipanteDTOSearch {

	public List<TipoParticipanteDTO> getTipoParticipanteDTOList() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<TipoParticipanteDTO> cq = cb.createQuery(TipoParticipanteDTO.class);
		Root<TipoParte> tipoParte = cq.from(TipoParte.class);

		Selection<TipoParticipanteDTO> selection = cb.construct(TipoParticipanteDTO.class, tipoParte);
		Order order = cb.asc(tipoParte.get(TipoParte_.descricao));
		
		cq = cq.select(selection).orderBy(order);

		return getEntityManager().createQuery(cq).getResultList();
	}

	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

}
