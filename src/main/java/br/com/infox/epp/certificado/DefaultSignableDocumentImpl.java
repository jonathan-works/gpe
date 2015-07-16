package br.com.infox.epp.certificado;

import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

public class DefaultSignableDocumentImpl implements SignableDocument{

	private String md5;
	
	public DefaultSignableDocumentImpl(Documento documento) {
		this.md5 = documento.getDocumentoBin().getMd5Documento();
	}
	
	public DefaultSignableDocumentImpl(DocumentoBin documentoBin) {
		this.md5 = documentoBin.getMd5Documento();
	}
	
	
	@Override
	public String getMD5() {
		return this.md5;
	}

}
