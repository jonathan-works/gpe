package br.com.infox.ibpm.service;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.Strings;

import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.itx.util.EntityUtil;

@Name(AssinaturaDocumentoService.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class AssinaturaDocumentoService {
	
	public static final String NAME = "assinaturaDocumentoService";
	
	public Boolean isDocumentoAssinado(ProcessoDocumento processoDocumento){
		return !Strings.isEmpty(processoDocumento.getProcessoDocumentoBin().getCertChain()) && 
			   !Strings.isEmpty(processoDocumento.getProcessoDocumentoBin().getSignature());
	}
	
	public Boolean isDocumentoAssinado(Integer idDoc){
		ProcessoDocumento processoDocumento = EntityUtil.find(ProcessoDocumento.class, idDoc);
		return processoDocumento != null && isDocumentoAssinado(processoDocumento);
	}
}
