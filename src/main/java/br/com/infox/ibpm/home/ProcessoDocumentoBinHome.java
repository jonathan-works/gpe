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

import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.entity.ProcessoDocumentoBin;
import br.com.infox.ibpm.home.api.IProcessoDocumentoBinHome;
import br.com.itx.util.ComponentUtil;

@Name(ProcessoDocumentoBinHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoDocumentoBinHome 
		extends AbstractProcessoDocumentoBinHome<ProcessoDocumentoBin> 
		implements IProcessoDocumentoBinHome {

	private static final long serialVersionUID = 1L;
	private String certChain;
	private String signature;
	
	public static final String NAME = "processoDocumentoBinHome";
	
	public static ProcessoDocumentoBinHome instance() {
		return ComponentUtil.getComponent("processoDocumentoBinHome");
	}

	public String getSignature() {
		return signature;
	}

	public void setCertChain(String certChain) {
		this.certChain = certChain;
	}
	
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String getCertChain() {
		return certChain;
	}

	private boolean isValidSignature() {
	    if (signature == null) {
	        return false;
	    }
	    if (certChain == null) {
	        return false;
	    }
	    return !"".equals(signature.trim()) && !"".equals(certChain.trim());
	}
	
	public void assinarDocumento(final ProcessoDocumento processoDocumento) {
	    if (isValidSignature()) {
	        setId(processoDocumento.getProcessoDocumentoBin().getIdProcessoDocumentoBin());
            processoDocumento.setLocalizacao(Authenticator.getLocalizacaoAtual());
            processoDocumento.setPapel(Authenticator.getPapelAtual());
            instance.setUsuarioUltimoAssinar(Authenticator.getUsuarioLogado().getNome());
            instance.setSignature(signature);
            instance.setCertChain(certChain);
            instance.setDataInclusao(new Date());
            update();
	    }
	}
	
}