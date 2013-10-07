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
package br.com.infox.cliente.home;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.home.AbstractProcessoDocumentoHome;
import br.com.infox.ibpm.home.api.IProcessoDocumentoHome;
import br.com.itx.util.ComponentUtil;

@Name(ProcessoDocumentoHome.NAME)
@BypassInterceptors
public class ProcessoDocumentoHome
	extends AbstractProcessoDocumentoHome<ProcessoDocumento> 
	implements IProcessoDocumentoHome{

	public static final String NAME = "processoDocumentoHome";

	private static final long serialVersionUID = 1L;

	public static IProcessoDocumentoHome instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
	@Override
	public String persist() {
		String ret = super.persist();
		newInstance();
		return ret;
	}
	
	@Override
	public void newInstance() {
		setModeloDocumentoCombo(null);
		super.newInstance();
	}
	
	public boolean liberaCertificacao(){
		return true;
	}
}
