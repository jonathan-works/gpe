package br.com.infox.epp.processo.documento.service;

import java.util.List;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import br.com.infox.epp.documento.entity.DocumentoBinario;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.dao.DocumentoBinDAO;
import br.com.infox.epp.processo.documento.dao.DocumentoBinarioDAO;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin.TipoStorage;

@Default
public class DocumentoBinServiceEpp implements DocumentoBinService {

	@Inject
	private DocumentoBinDAO documentoBinDao;
	
	@Inject
	private DocumentoBinarioDAO documentoBinarioDao;
	
	private DocumentoBin carregarDocumentoBin(Integer idDocumentoBin) {
		DocumentoBin documentoBin = documentoBinDao.find(idDocumentoBin);
		if(documentoBin.getTipoStorage() != TipoStorage.DB) {
			throw new UnsupportedOperationException("Tipo de storage inválido: " + documentoBin.getTipoStorage());			
		}
		return documentoBin; 
	}
	
	public List<AssinaturaDocumento> carregarAssinaturas(Integer idDocumentoBin) {
		DocumentoBin documentoBin = carregarDocumentoBin(idDocumentoBin);
		return documentoBin.getAssinaturasBanco();			
	}
	public DocumentoBinario carregarDocumentoBinario(Integer idDocumentoBin) {
		carregarDocumentoBin(idDocumentoBin);
		DocumentoBinario documentoBinario = documentoBinarioDao.find(idDocumentoBin);
		return documentoBinario;			
	}

	@Override
	public Integer getSize(Integer idDocumentoBin) {
		DocumentoBin documentoBin = carregarDocumentoBin(idDocumentoBin);
		return documentoBin.getSizeBanco();
	}

	@Override
	public boolean existeBinario(Integer idDocumentoBin) {
 		carregarDocumentoBin(idDocumentoBin);
 		return documentoBinarioDao.existeBinario(idDocumentoBin);
	}
}
