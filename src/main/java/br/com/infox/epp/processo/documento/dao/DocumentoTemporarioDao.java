package br.com.infox.epp.processo.documento.dao;

import static br.com.infox.epp.processo.documento.query.DocumentoTemporarioQuery.LIST_BY_PROCESSO;
import static br.com.infox.epp.processo.documento.query.DocumentoTemporarioQuery.LOAD_BY_ID;
import static br.com.infox.epp.processo.documento.query.DocumentoTemporarioQuery.PARAM_ID_DOCUMENTO_TEMPORARIO;
import static br.com.infox.epp.processo.documento.query.DocumentoTemporarioQuery.PARAM_LOCALIZACAO;
import static br.com.infox.epp.processo.documento.query.DocumentoTemporarioQuery.PARAM_PROCESSO;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.processo.documento.entity.DocumentoTemporario;
import br.com.infox.epp.processo.entity.Processo;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DocumentoTemporarioDao extends Dao<DocumentoTemporario, Integer> {

    public DocumentoTemporarioDao() {
        super(DocumentoTemporario.class);
    }

    public List<DocumentoTemporario> listByProcesso(Processo processo, Localizacao localizacao, String order) {
        TypedQuery<DocumentoTemporario> query = getEntityManager().createQuery(LIST_BY_PROCESSO.concat(order), DocumentoTemporario.class);
        query.setParameter(PARAM_PROCESSO, processo);
        query.setParameter(PARAM_LOCALIZACAO, localizacao);
        return query.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
	public void removeAll(List<DocumentoTemporario> documentoTemporarioList) throws DAOException {
		try {
			for (DocumentoTemporario documentoTemporario : documentoTemporarioList) {
				DocumentoTemporario toRemove = getEntityManager().merge(documentoTemporario);
				getEntityManager().remove(toRemove);
			}
			getEntityManager().flush();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
    
    public DocumentoTemporario loadById(Integer id) {
        TypedQuery<DocumentoTemporario> query = getEntityManager().createNamedQuery(LOAD_BY_ID, DocumentoTemporario.class);
        query.setParameter(PARAM_ID_DOCUMENTO_TEMPORARIO, id);
        return query.getSingleResult();
    }
}