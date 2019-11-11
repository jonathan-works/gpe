package br.com.infox.epp.documento;

import java.io.InputStream;
import java.util.UUID;

public interface DocumentoBinDataProvider {

	public byte[] getBytes(UUID uuidDocumentoBin);
	
	public InputStream getInputStream(UUID uuidDocumentoBin);
}
