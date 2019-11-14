package br.com.infox.epp.documento;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento_;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ClassificacaoDocumentoSearch extends PersistenceController {

    public List<ClassificacaoDocumento> findClassificacaoDocumentoWithDescricaoLike(String pattern) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ClassificacaoDocumento> cq = cb.createQuery(ClassificacaoDocumento.class);
        Root<ClassificacaoDocumento> classificacao = cq.from(ClassificacaoDocumento.class);

        Predicate like = cb.like(cb.lower(classificacao.get(ClassificacaoDocumento_.descricao)), cb.lower(cb.literal("%" + pattern.toLowerCase() + "%")));
        Predicate anexos = classificacao.get(ClassificacaoDocumento_.inTipoDocumento).in(TipoDocumentoEnum.D,TipoDocumentoEnum.T);
        cq = cq.select(classificacao).where(like, anexos);

        return em.createQuery(cq).getResultList();
    }
    
    public List<ClassificacaoDocumento> findClassificacaoDocumentoWithDescricaoLikeSemTipo(String pattern) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ClassificacaoDocumento> cq = cb.createQuery(ClassificacaoDocumento.class);
        Root<ClassificacaoDocumento> classificacao = cq.from(ClassificacaoDocumento.class);

        Predicate like = cb.like(cb.lower(classificacao.get(ClassificacaoDocumento_.descricao)), cb.lower(cb.literal("%" + pattern.toLowerCase() + "%")));
        cq = cq.select(classificacao).where(like);
        cq.orderBy(cb.asc(classificacao.get((ClassificacaoDocumento_.descricao))));
        return em.createQuery(cq).getResultList();
    }
    
    public List<ClassificacaoDocumento> listClassificacoesDocumentoDisponiveisVariavelFluxo(List<String> codigosClassificacoesAdicionadas, 
    		TipoDocumentoEnum tipoDocumento, String nomeClassificacaoDocumento, int start, int max) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ClassificacaoDocumento> cq = cb.createQuery(ClassificacaoDocumento.class);
		Root<ClassificacaoDocumento> from = createQueryClassificacaoDocumentoNotInList(codigosClassificacoesAdicionadas, tipoDocumento,
				nomeClassificacaoDocumento, cb, cq);
		addRestrictions(cb, cq);
    	cq.select(from);
    	return getEntityManager().createQuery(cq).setFirstResult(start).setMaxResults(max).getResultList();
    }
    
    protected void addRestrictions(CriteriaBuilder cb, CriteriaQuery<?> query) {}

	public Long countClassificacoesDocumentoDisponiveisVariavelFluxo(List<String> codigosClassificacoesAdicionadas, TipoDocumentoEnum tipoDocumento,
    		String nomeClassificacaoDocumento) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ClassificacaoDocumento> from = createQueryClassificacaoDocumentoNotInList(codigosClassificacoesAdicionadas, tipoDocumento,
				nomeClassificacaoDocumento, cb, cq);
		cq.select(cb.count(from.get(ClassificacaoDocumento_.id)));
		addRestrictions(cb, cq);
		return getEntityManager().createQuery(cq).getSingleResult();
	}

	private Root<ClassificacaoDocumento> createQueryClassificacaoDocumentoNotInList(List<String> codigosClassificacoesAdicionadas, 
			TipoDocumentoEnum tipoDocumento, String nomeClassificacaoDocumento, CriteriaBuilder cb,	CriteriaQuery<?> cq) {
		Root<ClassificacaoDocumento> from = cq.from(ClassificacaoDocumento.class);
		cq.where(cb.and(cb.isTrue(from.get(ClassificacaoDocumento_.ativo)),
    			cb.isFalse(from.get(ClassificacaoDocumento_.sistema)),
    			from.get(ClassificacaoDocumento_.inTipoDocumento).in(TipoDocumentoEnum.T, tipoDocumento)));
		if (codigosClassificacoesAdicionadas != null && !codigosClassificacoesAdicionadas.isEmpty()) {
			cq.where(cq.getRestriction(), from.get(ClassificacaoDocumento_.codigoDocumento).in(codigosClassificacoesAdicionadas).not());
		}
    	if (nomeClassificacaoDocumento != null) {
    		cq.where(cq.getRestriction(),
    				cb.like(cb.lower(from.get(ClassificacaoDocumento_.descricao)), cb.literal("%" + nomeClassificacaoDocumento.toLowerCase() + "%")));
    	}
		return from;
	}
	
	public List<ClassificacaoDocumento> findByListCodigos(List<String> codigosClassificacoesDocumento) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ClassificacaoDocumento> cq = cb.createQuery(ClassificacaoDocumento.class);
		Root<ClassificacaoDocumento> from = cq.from(ClassificacaoDocumento.class);
		cq.where(from.get(ClassificacaoDocumento_.codigoDocumento).in(codigosClassificacoesDocumento));
		return getEntityManager().createQuery(cq).getResultList();
	}
	
	public Boolean existeClassificacaoByCodigo(String codigo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ClassificacaoDocumento> from = cq.from(ClassificacaoDocumento.class);
		cq.where(cb.equal(from.get(ClassificacaoDocumento_.codigoDocumento), cb.literal(codigo)));
		cq.select(cb.count(from));
		return getEntityManager().createQuery(cq).getSingleResult() > 0;
	}

}
