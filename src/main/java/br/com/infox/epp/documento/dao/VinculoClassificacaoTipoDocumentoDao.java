package br.com.infox.epp.documento.dao;

import static br.com.infox.epp.documento.query.VinculoClassificacaoTipoDocumentoQuery.FIND_BY_CLASSIFICACAO;
import static br.com.infox.epp.documento.query.VinculoClassificacaoTipoDocumentoQuery.FIND_BY_TIPO_CLASSIFICACAO;
import static br.com.infox.epp.documento.query.VinculoClassificacaoTipoDocumentoQuery.PARAM_CLASSIFICACAO_DOCUMENTO;
import static br.com.infox.epp.documento.query.VinculoClassificacaoTipoDocumentoQuery.PARAM_TIPO_MODELO_DOCUMENTO;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.VinculoClassificacaoTipoDocumento;
/**
 * 
 * @author erikliberal
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class VinculoClassificacaoTipoDocumentoDao extends Dao<VinculoClassificacaoTipoDocumento, Integer> {

    public VinculoClassificacaoTipoDocumentoDao() {
		super(VinculoClassificacaoTipoDocumento.class);
	}

    protected Class<VinculoClassificacaoTipoDocumento> getEntityClass() {
        return VinculoClassificacaoTipoDocumento.class;
    }
    
    public VinculoClassificacaoTipoDocumento findBy(ClassificacaoDocumento classificacaoDocumento,
            TipoModeloDocumento tipoModeloDocumento) {
        TypedQuery<VinculoClassificacaoTipoDocumento> query = getEntityManager().createNamedQuery(FIND_BY_TIPO_CLASSIFICACAO,getEntityClass());
        query = query.setParameter(PARAM_TIPO_MODELO_DOCUMENTO, tipoModeloDocumento);
        query = query.setParameter(PARAM_CLASSIFICACAO_DOCUMENTO, classificacaoDocumento);
        query = query.setFirstResult(0);
        query = query.setMaxResults(1);
        List<VinculoClassificacaoTipoDocumento> resultList = query.getResultList();
        return resultList == null || resultList.isEmpty() ? null : resultList.get(0);
    }
    
    
    public VinculoClassificacaoTipoDocumento findBy(ClassificacaoDocumento classificacaoDocumento) {
    	TypedQuery<VinculoClassificacaoTipoDocumento> query = getEntityManager().createNamedQuery(FIND_BY_CLASSIFICACAO,getEntityClass());
    	query = query.setParameter(PARAM_CLASSIFICACAO_DOCUMENTO, classificacaoDocumento);
    	query = query.setFirstResult(0);
    	query = query.setMaxResults(1);
    	List<VinculoClassificacaoTipoDocumento> resultList = query.getResultList();
    	return resultList == null || resultList.isEmpty() ? null : resultList.get(0);
    }
}
