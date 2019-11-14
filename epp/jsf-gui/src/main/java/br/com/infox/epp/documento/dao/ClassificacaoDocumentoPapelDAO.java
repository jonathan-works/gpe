package br.com.infox.epp.documento.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel_;
import br.com.infox.epp.documento.query.ClassificacaoDocumentoPapelQuery;

@Stateless
@AutoCreate
@Name(ClassificacaoDocumentoPapelDAO.NAME)
public class ClassificacaoDocumentoPapelDAO extends DAO<ClassificacaoDocumentoPapel> {

    public static final String NAME = "classificacaoDocumentoPapelDAO";
    private static final long serialVersionUID = 1L;
    
    public boolean papelPodeAssinarClassificacao(Papel papel, ClassificacaoDocumento classificacao) {
    	Map<String, Object> params = new HashMap<>();
    	params.put(ClassificacaoDocumentoPapelQuery.PARAM_CLASSIFICACAO_DOCUMENTO, classificacao);
    	params.put(ClassificacaoDocumentoPapelQuery.PARAM_PAPEL, papel);
    	return getNamedSingleResult(ClassificacaoDocumentoPapelQuery.PAPEL_PODE_ASSINAR_CLASSIFICACAO, params) != null;
    }

	public boolean classificacaoExigeAssinatura(ClassificacaoDocumento classificacaoDocumento) {
		Map<String, Object> params = new HashMap<>();
		params.put(ClassificacaoDocumentoPapelQuery.PARAM_CLASSIFICACAO_DOCUMENTO, classificacaoDocumento);
		return getNamedSingleResult(ClassificacaoDocumentoPapelQuery.CLASSIFICACAO_EXIGE_ASSINATURA, params) != null;
	}

	public List<ClassificacaoDocumentoPapel> getByClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ClassificacaoDocumentoPapel> cq = cb.createQuery(ClassificacaoDocumentoPapel.class);
		Root<ClassificacaoDocumentoPapel> from = cq.from(ClassificacaoDocumentoPapel.class);
		cq.select(from);
		cq.where(cb.equal(from.get(ClassificacaoDocumentoPapel_.classificacaoDocumento), classificacaoDocumento));

		return getEntityManager().createQuery(cq).getResultList();
	}

	public ClassificacaoDocumentoPapel getByPapelAndClassificacao(Papel papel, ClassificacaoDocumento classificacaoDocumento) {
		Map<String, Object> params = new HashMap<>();
		params.put(ClassificacaoDocumentoPapelQuery.PARAM_CLASSIFICACAO_DOCUMENTO, classificacaoDocumento);
		params.put(ClassificacaoDocumentoPapelQuery.PARAM_PAPEL, papel);
		return getNamedSingleResult(ClassificacaoDocumentoPapelQuery.GET_BY_PAPEL_AND_CLASSIFICACAO, params);
	}
}
