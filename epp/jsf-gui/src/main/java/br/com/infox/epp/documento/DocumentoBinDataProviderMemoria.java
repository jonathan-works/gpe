package br.com.infox.epp.documento;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.infox.epp.processo.documento.entity.DocumentoBin;

public class DocumentoBinDataProviderMemoria {
	
	Map<UUID, byte[]> mapaDados;
	
	public DocumentoBinDataProviderMemoria(Map<DocumentoBin, byte[]> dadosDocumentoBin) {
		mapaDados = new HashMap<>();
		for(DocumentoBin documentoBin : dadosDocumentoBin.keySet()) {
			mapaDados.put(documentoBin.getUuid(), dadosDocumentoBin.get(documentoBin));
		}
	}
}
