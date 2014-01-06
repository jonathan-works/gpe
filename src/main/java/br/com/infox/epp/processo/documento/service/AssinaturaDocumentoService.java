package br.com.infox.epp.processo.documento.service;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.Strings;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;

@Name(AssinaturaDocumentoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class AssinaturaDocumentoService extends GenericManager{
	
    private static final long serialVersionUID = 1L;
    public static final String NAME = "assinaturaDocumentoService";
	
	public Boolean isDocumentoAssinado(ProcessoDocumento processoDocumento){
		return !Strings.isEmpty(processoDocumento.getProcessoDocumentoBin().getCertChain()) && 
			   !Strings.isEmpty(processoDocumento.getProcessoDocumentoBin().getSignature());
	}
	
	public Boolean isDocumentoAssinado(Integer idDoc){
		ProcessoDocumento processoDocumento = find(ProcessoDocumento.class, idDoc);
		return processoDocumento != null && isDocumentoAssinado(processoDocumento);
	}
}
