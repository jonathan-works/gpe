package br.com.infox.epp.assinador.rest;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;

public class DocumentoRestImpl implements DocumentoRest {

	@Inject
	private DocumentoRestService documentoRestService;
	@Inject
	private DocumentoBinManager documentoBinManager;
	
	private CertificateSignatureGroup group;

	public void setGroup(CertificateSignatureGroup group) {
		this.group = group;
	}
	

	@Override
	public List<Documento> listar() {
		return documentoRestService.listarAssinaturas(group.getToken());
	}

	@Override
	public Response atualizarDocumentosLote(List<Documento> documentos) {
		documentoRestService.criarOuAtualizarAssinaturasLote(documentos, group.getToken());
		return Response.noContent().build();
	}

	@Override
	public DocumentoResource getDocumentoResource(UUID uuid) {
		DocumentoResourceImpl documentoResourceImpl = BeanManager.INSTANCE.getReference(DocumentoResourceImpl.class);
		DocumentoBin documentoBin = documentoBinManager.getByUUID(uuid);
		if(documentoBin == null) {
			throw new NoResultException();
		}
		documentoResourceImpl.setDocumentoBin(documentoBin);
		documentoResourceImpl.setCertificateSignatureGroup(group);
		return documentoResourceImpl;
	}
	
	
	
	
	
}
