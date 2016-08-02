package br.com.infox.epp.assinador;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.documento.DocumentoBinDataProvider;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;

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
	private DocumentoBinarioManager documentoBinarioManager;
	
	private byte[] getDadosAssinaveis(DocumentoBin documentoBin) {
		if(documentoBin.isBinario()) {
			return documentoBinarioManager.getData(documentoBin.getId());
		}
		else {
			return documentoBin.getModeloDocumento().getBytes(StandardCharsets.UTF_8);			
		}
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
