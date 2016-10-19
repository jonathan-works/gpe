package br.com.infox.epp.processo.documento.service;

import java.util.List;

import javax.inject.Inject;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.entity.DocumentoBinario;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.dao.DocumentoBinDAO;
import br.com.infox.epp.processo.documento.dao.DocumentoBinarioDAO;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

public class DocumentoBinService {

	@Inject
	private DocumentoBinDAO documentoBinDao;
	@Inject
	private DocumentoBinarioDAO documentoBinarioDao;
	
	private DocumentoBin documentoBin;
	
	private DocumentoBin carregarDocumentoBin(Integer idDocumentoBin) {
		DocumentoBin documentoBin = documentoBinDao.find(idDocumentoBin);
		if(documentoBin.getTipoDocumentoExterno() != null) {
			throw new UnsupportedOperationException("Tipo de storage não suportado: " + documentoBin.getTipoDocumentoExterno());
		}
		return documentoBin; 
	}
	
	private DocumentoBin getDocumentoBin(Integer idDocumentoBin) {
		if(documentoBin == null || documentoBin.getId() != idDocumentoBin) {
			documentoBin = carregarDocumentoBin(idDocumentoBin);
		}
		return documentoBin;
	}
	
	public List<AssinaturaDocumento> carregarAssinaturas(Integer idDocumentoBin) {
		DocumentoBin documentoBin = getDocumentoBin(idDocumentoBin);
		return documentoBin.getAssinaturasAtributo();			
	}
	public DocumentoBinario carregarDocumentoBinario(Integer idDocumentoBin) {
		getDocumentoBin(idDocumentoBin);
		DocumentoBinario documentoBinario = documentoBinarioDao.find(idDocumentoBin);
		return documentoBinario;			
	}

	public Integer getSize(Integer idDocumentoBin) {
		DocumentoBin documentoBin = getDocumentoBin(idDocumentoBin);
		return documentoBin.getSizeAtributo();
	}

	public boolean existeBinario(Integer idDocumentoBin) {
		getDocumentoBin(idDocumentoBin);
 		return documentoBinarioDao.existeBinario(idDocumentoBin);
	}

	public String getHash(Integer idDocumentoBin) {
		DocumentoBin documentoBin = getDocumentoBin(idDocumentoBin);
		return documentoBin.getMd5DocumentoAtributo();
	}
	
	public static DocumentoBinService createInstance() {
	    return BeanManager.INSTANCE.getReference(DocumentoBinService.class);
	}
}
