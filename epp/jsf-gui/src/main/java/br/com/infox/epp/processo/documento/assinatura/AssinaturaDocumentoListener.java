package br.com.infox.epp.processo.documento.assinatura;

import br.com.infox.epp.processo.documento.entity.Documento;

public interface AssinaturaDocumentoListener {
	void postSignDocument(Documento documento);
}
