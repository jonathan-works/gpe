package br.com.infox.epp.assinador.assinavel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.infox.epp.assinador.DocumentoBinAssinavelService;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

public class AssinavelDocumentoBinProvider implements AssinavelProvider {
	
	private List<AssinavelSource> assinaveis;
	private List<DocumentoBin> documentos;
	private DocumentoBinAssinavelService documentoBinAssinavelService;
	
	public AssinavelDocumentoBinProvider(List<DocumentoBin> documentos) {
		super();
		this.documentos = documentos;
	}

	public AssinavelDocumentoBinProvider(DocumentoBin documentoBin) {
		this(Arrays.asList(documentoBin));
	}
	
	private byte[] getBinario(DocumentoBin documentoBin) {
		if(documentoBinAssinavelService == null) {
			documentoBinAssinavelService = BeanManager.INSTANCE.getReference(DocumentoBinAssinavelService.class);
		}
		return  documentoBinAssinavelService.getDadosAssinaveis(documentoBin.getId());
	}
	
	public class AssinavelDocumentoBinSourceImpl implements AssinavelDocumentoBinSource {
		
		private DocumentoBin documentoBin;
		
		public AssinavelDocumentoBinSourceImpl(DocumentoBin documentoBin) {
			super();
			this.documentoBin = documentoBin;
		}

		@Override
		public byte[] dataToSign(TipoSignedData tipoHash) {
				byte[] data = getBinario(documentoBin);
				return tipoHash.dataToSign(data); 
		}

		@Override
		public Integer getIdDocumentoBin() {
			return documentoBin.getId();
		}
		
	}
	
	@Override
	public List<AssinavelSource> getAssinaveis() {
		if(assinaveis == null) {
			assinaveis = new ArrayList<>();
			for(DocumentoBin documento : documentos) {
				assinaveis.add(new AssinavelDocumentoBinSourceImpl(documento));
			}
		}
		return assinaveis;
	}	
}
