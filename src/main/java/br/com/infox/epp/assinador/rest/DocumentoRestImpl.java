package br.com.infox.epp.assinador.rest;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;

public class DocumentoRestImpl implements DocumentoRest {

	@Inject
	private DocumentoRestService documentoRestService;
	@Inject
	private DocumentoBinManager documentoBinManager;
	
	private String tokenGrupo;

	public void setTokenGrupo(String tokenGrupo) {
		this.tokenGrupo = tokenGrupo;
	}

	@Override
	public List<Documento> listar() {
		return documentoRestService.listarDocumentos(tokenGrupo);
	}

	@Override
	public DocumentoResource getDocumentoResource(UUID uuid) {
		DocumentoResourceImpl documentoResourceImpl = BeanManager.INSTANCE.getReference(DocumentoResourceImpl.class);
		DocumentoBin documentoBin = documentoBinManager.getByUUID(uuid);
		if(documentoBin == null) {
			throw new NoResultException();
		}
		documentoResourceImpl.setDocumentoBin(documentoBin);
		documentoResourceImpl.setTokenGrupo(tokenGrupo);
		return documentoResourceImpl;
	}
	
	
	
	
	
}
