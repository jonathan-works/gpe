package br.com.infox.epp.pessoa.documento.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento_;
import br.com.infox.epp.pessoa.entity.Pessoa;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PessoaDocumentoSearch extends PersistenceController {

	public List<PessoaDocumento> getDocumentosByPessoa(Pessoa pessoa) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<PessoaDocumento> cq = cb.createQuery(PessoaDocumento.class);
		Root<PessoaDocumento> documento = cq.from(PessoaDocumento.class);
		cq.where(cb.equal(documento.get(PessoaDocumento_.pessoa), pessoa));
		return getEntityManager().createQuery(cq).getResultList();
	}
	
}
