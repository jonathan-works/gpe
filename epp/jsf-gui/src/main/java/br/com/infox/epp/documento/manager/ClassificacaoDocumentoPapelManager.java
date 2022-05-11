package br.com.infox.epp.documento.manager;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.dao.ClassificacaoDocumentoPapelDAO;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel_;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.documento.type.TipoMeioAssinaturaEnum;

@AutoCreate
@Stateless
@Name(ClassificacaoDocumentoPapelManager.NAME)
public class ClassificacaoDocumentoPapelManager extends Manager<ClassificacaoDocumentoPapelDAO, ClassificacaoDocumentoPapel> {

    private static final long serialVersionUID = 4455754174682600299L;
    public static final String NAME = "classificacaoDocumentoPapelManager";

    public boolean papelPodeAssinarClassificacao(Papel papel, ClassificacaoDocumento classificacao) {
    	return getDao().papelPodeAssinarClassificacao(papel, classificacao);
    }

	public boolean papelPodeAssinarClassificacao(Integer papel, Integer classificacao) {
		return getDao().papelPodeAssinarClassificacao(papel, classificacao);
	}

    public boolean classificacaoExigeAssinatura(ClassificacaoDocumento classificacaoDocumento) {
    	return getDao().classificacaoExigeAssinatura(classificacaoDocumento);
    }

	public List<ClassificacaoDocumentoPapel> getByClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
		return getDao().getByClassificacaoDocumento(classificacaoDocumento);
	}

	public boolean papelPodeTornarSuficientementeAssinado(Papel papel, ClassificacaoDocumento classificacaoDocumento) {
		CriteriaBuilder cb = getDao().getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
		Root<ClassificacaoDocumentoPapel> cdp = query.from(ClassificacaoDocumentoPapel.class);

		Subquery<Long> subquery1 = query.subquery(Long.class);
		Root<ClassificacaoDocumentoPapel> cdp2 =  subquery1.from(ClassificacaoDocumentoPapel.class);
		subquery1.select(cb.count(cdp2.get(ClassificacaoDocumentoPapel_.id)));
		subquery1.where(cb.equal(cdp2.get(ClassificacaoDocumentoPapel_.tipoAssinatura), TipoAssinaturaEnum.O),
					cb.equal(cdp2.get(ClassificacaoDocumentoPapel_.classificacaoDocumento), cdp.get(ClassificacaoDocumentoPapel_.classificacaoDocumento)));

		Subquery<Integer> subquery2 = query.subquery(Integer.class);
		cdp2 = subquery2.from(ClassificacaoDocumentoPapel.class);
		subquery2.select(cb.literal(1));
		subquery2.where(cb.equal(cdp2.get(ClassificacaoDocumentoPapel_.papel), cdp.get(ClassificacaoDocumentoPapel_.papel)),
					cb.equal(cdp2.get(ClassificacaoDocumentoPapel_.classificacaoDocumento), cdp.get(ClassificacaoDocumentoPapel_.classificacaoDocumento)),
					cdp2.get(ClassificacaoDocumentoPapel_.tipoAssinatura).in(TipoAssinaturaEnum.O, TipoAssinaturaEnum.S));

		query.where(cb.equal(cdp.get(ClassificacaoDocumentoPapel_.classificacaoDocumento), classificacaoDocumento),
					cb.equal(cdp.get(ClassificacaoDocumentoPapel_.papel), papel),
					cb.lt(subquery1, 2),
					cb.exists(subquery2)
				);

		query.select(cb.literal(1));
		List<Integer> result = getDao().getEntityManager().createQuery(query).setMaxResults(1).getResultList();
		return result != null && !result.isEmpty();
	}

	public TipoMeioAssinaturaEnum getTipoMeioAssinaturaUsuarioLogadoByClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
	    ClassificacaoDocumentoPapel classificacaoDocumentoPapel = getByPapelAndClassificacao(Authenticator.getPapelAtual(), classificacaoDocumento);
	    return classificacaoDocumentoPapel.getTipoMeioAssinatura();
	}

	public ClassificacaoDocumentoPapel getByPapelAndClassificacao(Papel papel, ClassificacaoDocumento classificacaoDocumento) {
		return getDao().getByPapelAndClassificacao(papel, classificacaoDocumento);
	}
}
