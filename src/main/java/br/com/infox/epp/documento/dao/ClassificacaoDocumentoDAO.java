package br.com.infox.epp.documento.dao;

import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.ASSINATURA_OBRIGATORIA;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.CLASSIFICACAO_DOCUMENTO_PARAM;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.CODIGO_DOCUMENTO_PARAM;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.FIND_CLASSIFICACAO_DOCUMENTO_BY_DESCRICAO;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.LIST_CLASSIFICACAO_DOCUMENTO_BY_PROCESSO;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.PAPEL_PARAM;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.PARAM_DESCRICAO;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.PARAM_PROCESSO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel_;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento_;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.entity.Processo;

@AutoCreate
@Name(ClassificacaoDocumentoDAO.NAME)
public class ClassificacaoDocumentoDAO extends DAO<ClassificacaoDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "classificacaoDocumentoDAO";
    
    @Override
    public List<ClassificacaoDocumento> findAll() {
    	String hql = "select o from ClassificacaoDocumento o order by o.descricao";
    	return getResultList(hql, null);
    }
    
    public List<ClassificacaoDocumento> getClassificacaoDocumentoListByProcesso(Processo processo) {
    	Map<String, Object> params = new HashMap<>(1);
    	params.put(PARAM_PROCESSO, processo);
    	return getNamedResultList(LIST_CLASSIFICACAO_DOCUMENTO_BY_PROCESSO, params);
    }

    public List<ClassificacaoDocumento> getUseableClassificacaoDocumento(boolean isModelo, Papel papel) {
    	CriteriaQuery<ClassificacaoDocumento> query = createQueryUseableClassificacaoDocumento(isModelo, papel);
    	return getEntityManager().createQuery(query).getResultList();
    }

	protected CriteriaQuery<ClassificacaoDocumento> createQueryUseableClassificacaoDocumento(boolean isModelo, Papel papel) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<ClassificacaoDocumento> query = cb.createQuery(ClassificacaoDocumento.class);
    	Root<ClassificacaoDocumento> from = query.from(ClassificacaoDocumento.class);
    	Join<ClassificacaoDocumento, ClassificacaoDocumentoPapel> join = from.join("classificacaoDocumentoPapelList");
    	Predicate predicate = cb.and(cb.equal(from.get("sistema"), false),
    			cb.equal(join.get("papel"), papel),
    			cb.equal(from.get("ativo"), true));
    	
    	predicate = cb.and(cb.or(cb.equal(from.get("inTipoDocumento"), isModelo ? TipoDocumentoEnum.P : TipoDocumentoEnum.D), 
    			cb.equal(from.get("inTipoDocumento"), TipoDocumentoEnum.T)), predicate);
    	
    	query.where(predicate).orderBy(cb.asc(from.get("descricao")));
		return query;
	}

    public boolean isAssinaturaObrigatoria(ClassificacaoDocumento classificacaoDocumento, Papel papel) {
        HashMap<String, Object> params = new HashMap<String, Object>(0);
        params.put(CLASSIFICACAO_DOCUMENTO_PARAM, classificacaoDocumento);
        params.put(PAPEL_PARAM, papel);
        ClassificacaoDocumentoPapel tpdp = getNamedSingleResult(ASSINATURA_OBRIGATORIA, params);
        if (tpdp != null) {
            return tpdp.getTipoAssinatura() != TipoAssinaturaEnum.F;
        }
        return false;
    }
    
    public ClassificacaoDocumento findByCodigo(String codigo) {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(CODIGO_DOCUMENTO_PARAM, codigo);
        return getNamedSingleResult(FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO, parameters);
    }
    
    public ClassificacaoDocumento findByDescricao(String descricao) {
    	Map<String, Object> params = new HashMap<>(1);
    	params.put(PARAM_DESCRICAO, descricao);
    	return getNamedSingleResult(FIND_CLASSIFICACAO_DOCUMENTO_BY_DESCRICAO, params);
    }
    
    public List<ClassificacaoDocumento> getClassificacoesDocumentoDisponiveisRespostaComunicacao(TipoComunicacao tipoComunicacao, boolean isModelo, Papel papel) {
		return getUseableClassificacaoDocumento(isModelo, papel);
	}

    private CriteriaQuery<ClassificacaoDocumento> createQueryClassificacoesDocumentoCruds(TipoDocumentoEnum tipoDocumento) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ClassificacaoDocumento> query = cb.createQuery(ClassificacaoDocumento.class);
        Root<ClassificacaoDocumento> from = query.from(ClassificacaoDocumento.class);
        Predicate predicate = cb.and(cb.isFalse(from.get(ClassificacaoDocumento_.sistema)), 
                cb.isTrue(from.get(ClassificacaoDocumento_.ativo)));
        
        if (tipoDocumento != null){
            List<TipoDocumentoEnum> tiposDocumento = new ArrayList<>();
            tiposDocumento.add(TipoDocumentoEnum.T);
            switch (tipoDocumento) {
            case T:
                tiposDocumento.add(TipoDocumentoEnum.P);
                tiposDocumento.add(TipoDocumentoEnum.D);
                break;

            default:
                tiposDocumento.add(tipoDocumento);
                break;
            }
            predicate = cb.and(predicate, from.get(ClassificacaoDocumento_.inTipoDocumento).in(tiposDocumento));
        }
        query.where(predicate).orderBy(cb.asc(from.get(ClassificacaoDocumento_.descricao)));
        return query;
    }
    
    public List<ClassificacaoDocumento> getClassificacoesDocumentoCruds(TipoDocumentoEnum tipoDocumento) {
        CriteriaQuery<ClassificacaoDocumento> query = createQueryClassificacoesDocumentoCruds(tipoDocumento);
        return getEntityManager().createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<ClassificacaoDocumento> getClassificacoesDocumentoAnexarDocumento(TipoDocumentoEnum tipoDocumento) {
        CriteriaQuery<ClassificacaoDocumento> query = createQueryClassificacoesDocumentoCruds(tipoDocumento);
        Root<ClassificacaoDocumento> from = (Root<ClassificacaoDocumento>) query.getRoots().iterator().next();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Predicate predicate = query.getRestriction();
        Subquery<Integer> subquery = query.subquery(Integer.class);
	      Root<ClassificacaoDocumentoPapel> classificacaoDocumentoPapel = subquery.from(ClassificacaoDocumentoPapel.class);
	      subquery.select(cb.literal(1));
	      subquery.where(cb.and(cb.isTrue(classificacaoDocumentoPapel.get(ClassificacaoDocumentoPapel_.podeRedigir)),
	      		cb.equal(classificacaoDocumentoPapel.get(ClassificacaoDocumentoPapel_.classificacaoDocumento), from),
	      		cb.equal(classificacaoDocumentoPapel.get(ClassificacaoDocumentoPapel_.papel), Authenticator.getPapelAtual())));
	              
	    predicate = cb.and(predicate, cb.exists(subquery));
	    query.where(predicate);
        return getEntityManager().createQuery(query).getResultList();
    }
}
