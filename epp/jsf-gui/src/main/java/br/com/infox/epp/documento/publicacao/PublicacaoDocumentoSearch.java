package br.com.infox.epp.documento.publicacao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.documento.entity.Documento;

public class PublicacaoDocumentoSearch extends PersistenceController {
	
	
	public List<PublicacaoDocumento> getByDocumento(Documento documento) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<PublicacaoDocumento> cq = cb.createQuery(PublicacaoDocumento.class);
		Root<PublicacaoDocumento> from = cq.from(PublicacaoDocumento.class);
		
		cq.where(cb.equal(from.get(PublicacaoDocumento_.documento), documento));
		return getEntityManager().createQuery(cq).getResultList();
	}
	
	public PublicacaoDocumento findByDocumento(Documento documento) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<PublicacaoDocumento> query = cb.createQuery(PublicacaoDocumento.class);
		Root<PublicacaoDocumento> publicacao = query.from(PublicacaoDocumento.class);
		query.where(cb.equal(publicacao.get(PublicacaoDocumento_.documento), documento));
		return getEntityManager().createQuery(query).getSingleResult();		
	}	
	
	

}
