package br.com.infox.epp.documento.publicacao;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.epp.processo.documento.entity.Documento;

@Stateless
public class PublicacaoDocumentoService {

	@Inject
	@GenericDao
	private Dao<PublicacaoDocumento, Long> dao;
	@Inject
	private PublicacaoDocumentoSearch publicacaoDocumentoSearch;
		
	public void publicarDocumento(PublicacaoDocumento publicacao) {
		dao.persist(publicacao);
	}
	
	public List<PublicacaoDocumento> getByDocumento(Documento documento) {
		return publicacaoDocumentoSearch.getByDocumento(documento);
	}
	
}
