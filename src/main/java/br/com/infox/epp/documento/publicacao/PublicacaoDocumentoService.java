package br.com.infox.epp.documento.publicacao;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;

@Stateless
public class PublicacaoDocumentoService {

	@Inject
	@GenericDao
	private Dao<PublicacaoDocumento, Long> dao;
		
	public void publicarDocumento(PublicacaoDocumento publicacao) {
		dao.persist(publicacao);
	}
	
}
