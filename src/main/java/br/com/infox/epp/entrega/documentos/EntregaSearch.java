package br.com.infox.epp.entrega.documentos;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.entrega.modelo.ClassificacaoDocumentoEntrega;
import br.com.infox.epp.entrega.modelo.ClassificacaoDocumentoEntrega_;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Documento_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EntregaSearch extends PersistenceController {

	public List<ClassificacaoDocumentoEntrega> getClassificacoesDisponiveis(Entrega entrega, boolean obrigatorias) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ClassificacaoDocumentoEntrega> query = cb.createQuery(ClassificacaoDocumentoEntrega.class);
		Root<ClassificacaoDocumentoEntrega> classificacaoEntrega = query.from(ClassificacaoDocumentoEntrega.class);
		Join<ClassificacaoDocumentoEntrega, ClassificacaoDocumento> classificacao = classificacaoEntrega.join(ClassificacaoDocumentoEntrega_.classificacaoDocumento, JoinType.INNER);
		
		Subquery<Integer> subquery = query.subquery(Integer.class);
		subquery.select(cb.literal(1));
		Root<Documento> documento = subquery.from(Documento.class);
		subquery.where(
			cb.equal(documento.get(Documento_.pasta), entrega.getPasta()),
			cb.equal(documento.get(Documento_.classificacaoDocumento), classificacao)
		);

		Predicate predicateObrigatorias = obrigatorias ? cb.isTrue(classificacaoEntrega.get(ClassificacaoDocumentoEntrega_.obrigatorio)) 
				: cb.isFalse(classificacaoEntrega.get(ClassificacaoDocumentoEntrega_.obrigatorio));
		
		query.where(
			cb.equal(classificacaoEntrega.get(ClassificacaoDocumentoEntrega_.modeloEntrega), entrega.getModeloEntrega()),
			predicateObrigatorias,
			cb.or(
				cb.isTrue(classificacaoEntrega.get(ClassificacaoDocumentoEntrega_.multiplosDocumentos)),
				cb.exists(subquery).not()
			)
		);
		
		return entityManager.createQuery(query).getResultList();
	}
}
