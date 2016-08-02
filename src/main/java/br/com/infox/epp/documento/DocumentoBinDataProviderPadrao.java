package br.com.infox.epp.documento;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.enterprise.inject.Alternative;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;

@Alternative
public class DocumentoBinDataProviderPadrao implements DocumentoBinDataProvider {
	
	private DocumentoBinManager documentoBinManager;
	private DocumentoBinarioManager documentoBinarioManager;
	
	public DocumentoBinDataProviderPadrao() {
		documentoBinManager = BeanManager.INSTANCE.getReference(DocumentoBinManager.class);
		documentoBinarioManager = BeanManager.INSTANCE.getReference(DocumentoBinarioManager.class);
	}

	@Override
	public byte[] getBytes(UUID uuidDocumentoBin) {
		DocumentoBin documentoBin = documentoBinManager.getByUUID(uuidDocumentoBin);
		return documentoBinarioManager.getData(documentoBin.getId());
	}

	@Override
	public InputStream getInputStream(UUID uuidDocumentoBin) {
		return new ByteArrayInputStream(getBytes(uuidDocumentoBin));
	}

}
