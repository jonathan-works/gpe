package br.com.infox.epp.assinador;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.epp.documento.DocumentoBinDataProvider;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;

/**
 * Serviço utilizado para carregar os dados assinaveis de um {@link DocumentoBin}
 * @author paulo
 *
 */
@Stateless
@LocalBean
public class DocumentoBinAssinavelService implements DocumentoBinDataProvider {

	@Inject
	private DocumentoBinManager documentoBinManager;
	@Inject
	private FileDownloader fileDownloader;
	
	private byte[] getDadosAssinaveis(DocumentoBin documentoBin) {
	    return fileDownloader.getData(documentoBin, false);
	}

	@Override
	public byte[] getBytes(UUID uuidDocumentoBin) {
		DocumentoBin documentoBin = documentoBinManager.getByUUID(uuidDocumentoBin);
		return getDadosAssinaveis(documentoBin);
	}

	@Override
	public InputStream getInputStream(UUID uuidDocumentoBin) {
		return new ByteArrayInputStream(getBytes(uuidDocumentoBin));
	}
}
