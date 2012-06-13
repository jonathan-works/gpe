/*
  IBPM - Ferramenta de produtividade Java
  Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.
 
  Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
  sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
  Free Software Foundation; versão 2 da Licença.
  Este programa é distribuído na expectativa de que seja útil, porém, SEM 
  NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
  ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
  
  Consulte a GNU GPL para mais detalhes.
  Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
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