package br.com.infox.epp.assinador.assinavel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import br.com.infox.epp.assinador.DocumentoBinAssinavelService;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.documento.DocumentoBinDataProvider;
import br.com.infox.epp.documento.type.TipoMeioAssinaturaEnum;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public class AssinavelDocumentoBinProvider implements AssinavelProvider {

    @AllArgsConstructor @Data
    public static class DocumentoComRegraAssinatura {
        private final TipoMeioAssinaturaEnum tipoMeioAssinatura;
        private final DocumentoBin documentoBin;
    }

	private List<AssinavelDocumentoBinSourceImpl> assinaveis;
	private List<DocumentoComRegraAssinatura> documentos;
	private DocumentoBinDataProvider documentoBinDataProvider;

	public AssinavelDocumentoBinProvider(List<DocumentoComRegraAssinatura> documentos, DocumentoBinDataProvider documentoBinDataProvider) {
		super();
		this.documentos = documentos;
		this.documentoBinDataProvider = documentoBinDataProvider;
	}

	public AssinavelDocumentoBinProvider(TipoMeioAssinaturaEnum tma, List<DocumentoBin> documentos) {
        this(gerarDocumentoComRegraAssinaturaList(tma, documentos));
	}

	private static List<DocumentoComRegraAssinatura> gerarDocumentoComRegraAssinaturaList(TipoMeioAssinaturaEnum tma, List<DocumentoBin> documentos) {
	    List<AssinavelDocumentoBinProvider.DocumentoComRegraAssinatura> lista = new ArrayList<AssinavelDocumentoBinProvider.DocumentoComRegraAssinatura>();
        for (DocumentoBin docBin : documentos) {
            lista.add(new AssinavelDocumentoBinProvider.DocumentoComRegraAssinatura(
                tma,
                docBin
            ));
        }
        return lista;
	}

	public AssinavelDocumentoBinProvider(List<DocumentoComRegraAssinatura> documentos) {
		this(documentos, Beans.getReference(DocumentoBinAssinavelService.class));
		this.documentos = documentos;
	}

	public AssinavelDocumentoBinProvider(DocumentoComRegraAssinatura documentoBin) {
		this(Arrays.asList(documentoBin));
	}

	private byte[] getBinario(DocumentoBin documentoBin) {
		return documentoBinDataProvider.getBytes(documentoBin.getUuid());
	}

	@AllArgsConstructor @Getter
	private class AssinavelDocumentoBinSourceImpl implements AssinavelDocumentoBinSource {

		private final DocumentoBin documentoBin;
		private TipoMeioAssinaturaEnum tipoMeioAssinatura;

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
	public List<AssinavelDocumentoBinSourceImpl> getAssinaveis() {
		if(assinaveis == null) {
			assinaveis = new ArrayList<>();
			for(DocumentoComRegraAssinatura documento : documentos) {
				assinaveis.add(new AssinavelDocumentoBinSourceImpl(documento.getDocumentoBin(), documento.getTipoMeioAssinatura()));
			}
		}
		return assinaveis;
	}
}
