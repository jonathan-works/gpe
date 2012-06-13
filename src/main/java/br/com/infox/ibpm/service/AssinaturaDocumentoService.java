package br.com.infox.ibpm.service;

import org.jboss.seam.util.Strings;

import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.itx.util.EntityUtil;

public class AssinaturaDocumentoService {
	
	public Boolean isDocumentoAssinado(ProcessoDocumento processoDocumento){
		return !Strings.isEmpty(processoDocumento.getProcessoDocumentoBin().getCertChain()) && 
			   !Strings.isEmpty(processoDocumento.getProcessoDocumentoBin().getSignature());
	}
	
	public Boolean isDocumentoAssinado(Integer idDoc){
		ProcessoDocumento processoDocumento = EntityUtil.find(ProcessoDocumento.class, idDoc);
		return processoDocumento != null && isDocumentoAssinado(processoDocumento);
	}
}
