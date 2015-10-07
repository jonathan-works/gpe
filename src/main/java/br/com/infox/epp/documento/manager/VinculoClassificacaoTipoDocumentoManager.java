package br.com.infox.epp.documento.manager;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.dao.VinculoClassificacaoTipoDocumentoDao;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.VinculoClassificacaoTipoDocumento;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class VinculoClassificacaoTipoDocumentoManager implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Inject
    private VinculoClassificacaoTipoDocumentoDao vinculoClassificacaoTipoDocumentoDao;
    
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void vincular(ClassificacaoDocumento classificacaoDocumento, TipoModeloDocumento tipoModeloDocumento) throws DAOException {
        VinculoClassificacaoTipoDocumento vinculo = vinculoClassificacaoTipoDocumentoDao.findBy(classificacaoDocumento);
        if (vinculo == null){
            vinculo = new VinculoClassificacaoTipoDocumento();
            vinculo.setClassificacaoDocumento(classificacaoDocumento);
            vinculo.setTipoModeloDocumento(tipoModeloDocumento);
            vinculoClassificacaoTipoDocumentoDao.persist(vinculo);
        }else{
        	vinculo.setTipoModeloDocumento(tipoModeloDocumento);
        	vinculoClassificacaoTipoDocumentoDao.update(vinculo);
        }
    }
    
    public VinculoClassificacaoTipoDocumento findVinculacaoByClassificacao(ClassificacaoDocumento classificacaoDocumento){
    	return vinculoClassificacaoTipoDocumentoDao.findBy(classificacaoDocumento);
    }

	public void desvincular(ClassificacaoDocumento classificacaoDocumento, TipoModeloDocumento tipoModeloDocumento) throws DAOException {
		VinculoClassificacaoTipoDocumento vinculo = vinculoClassificacaoTipoDocumentoDao.findBy(classificacaoDocumento);
		if (vinculo != null) {
			vinculoClassificacaoTipoDocumentoDao.remove(vinculo);
		}
	}
}
