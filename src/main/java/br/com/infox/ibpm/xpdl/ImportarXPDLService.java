package br.com.infox.ibpm.xpdl;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name(ImportarXPDLService.NAME)
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
@AutoCreate
public class ImportarXPDLService implements Serializable {

	private static final long serialVersionUID = -9217493640837308552L;
	public static final String NAME = "importarXPDLService";

	public String importarXPDLToJPDL(byte[] bytes, String cdFluxo) throws ImportarXPDLServiceException {
		try {
			FluxoXPDL fluxoXPDL = FluxoXPDL.createInstance(bytes);
			return fluxoXPDL.toJPDL(cdFluxo);
		} catch (IllegalXPDLException e) {
			throw new ImportarXPDLServiceException("Erro ao importar para JPDL", e);
		}
	}

}
