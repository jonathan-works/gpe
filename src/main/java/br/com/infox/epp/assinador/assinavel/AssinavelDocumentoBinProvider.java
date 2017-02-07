package br.com.infox.epp.assinador.assinavel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import br.com.infox.epp.assinador.DocumentoBinAssinavelService;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.DocumentoBinDataProvider;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

public class AssinavelDocumentoBinProvider implements AssinavelProvider {
	
	private List<AssinavelSource> assinaveis;
	private List<DocumentoBin> documentos;
	private DocumentoBinDataProvider documentoBinDataProvider;
	
	public AssinavelDocumentoBinProvider(List<DocumentoBin> documentos, DocumentoBinDataProvider documentoBinDataProvider) {
		super();
		this.documentos = documentos;
		this.documentoBinDataProvider = documentoBinDataProvider;		
	}
	
	public AssinavelDocumentoBinProvider(List<DocumentoBin> documentos) {
		this(documentos, BeanManager.INSTANCE.getReference(DocumentoBinAssinavelService.class));
		this.documentos = documentos;
	}

	public AssinavelDocumentoBinProvider(DocumentoBin documentoBin) {
		this(Arrays.asList(documentoBin));
	}
	
	private byte[] getBinario(DocumentoBin documentoBin) {
		return documentoBinDataProvider.getBytes(documentoBin.getUuid());
	}
	
	public class AssinavelDocumentoBinSourceImpl implements AssinavelDocumentoBinSource {
		
		private DocumentoBin documentoBin;
		
		public AssinavelDocumentoBinSourceImpl(DocumentoBin documentoBin) {
			super();
			this.documentoBin = documentoBin;
		}
        @Override
        public UUID getUUIDAssinavel() {
            return documentoBin.getUuid();
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
