package br.com.infox.epp.assinador;

import java.nio.charset.StandardCharsets;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.processo.documento.dao.DocumentoBinDAO;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;

/**
 * Serviço utilizado para carregar os dados assinaveis de um {@link DocumentoBin}
 * @author paulo
 *
 */
@Stateless
public class DocumentoBinAssinavelService {

	@Inject
	private DocumentoBinarioManager documentoBinarioManager;
	@Inject
	private DocumentoBinDAO documentoBinDAO;
	
	public byte[] getDadosAssinaveis(Integer idDocumentoBin) {
		DocumentoBin documentoBin = documentoBinDAO.find(idDocumentoBin);
		if(documentoBin.isBinario()) {
			return documentoBinarioManager.getData(documentoBin.getId());
		}
		else {
			return documentoBin.getModeloDocumento().getBytes(StandardCharsets.UTF_8);			
		}
	}
}
