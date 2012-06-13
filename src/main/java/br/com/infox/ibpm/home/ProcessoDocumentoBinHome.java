/*
  IBPM - Ferramenta de produtividade Java
  Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.
 
  Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
  sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
  Free Software Foundation; vers�o 2 da Licen�a.
  Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
  NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
  ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
  
  Consulte a GNU GPL para mais detalhes.
  Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
  veja em http://www.gnu.org/licenses/  
*/

package br.com.infox.ibpm.home;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.entity.ProcessoDocumentoBin;
import br.com.infox.ibpm.home.api.IProcessoDocumentoBinHome;
import br.com.itx.util.ComponentUtil;

@Install(precedence=Install.FRAMEWORK)
@Name("processoDocumentoBinHome")
@BypassInterceptors
public class ProcessoDocumentoBinHome 
		extends AbstractProcessoDocumentoBinHome<ProcessoDocumentoBin> 
		implements IProcessoDocumentoBinHome {

	private static final long serialVersionUID = 1L;
	private String certChain;
	private String signature;
	
	public static ProcessoDocumentoBinHome instance() {
		return ComponentUtil.getComponent("processoDocumentoBinHome");
	}

	public String getSignature() {
		return signature;
	}

	public void setCertChain(String certChain) {
		getInstance().setCertChain(certChain);
		this.certChain = certChain;
	}
	
	public void setSignature(String signature) {
		getInstance().setSignature(signature);
		this.signature = signature;
	}
	
	public String getCertChain() {
		return certChain;
	}

}