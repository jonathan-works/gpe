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

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import br.com.infox.core.action.list.EntityList;
import br.com.infox.epp.list.TipoModeloDocumentoList;
import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.itx.util.ComponentUtil;

@Name(TipoModeloDocumentoHome.NAME)
@BypassInterceptors
public class TipoModeloDocumentoHome 
		extends	AbstractTipoModeloDocumentoHome<TipoModeloDocumento> {
	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/TipoModeloDocumento/tipoModeloDocumentoTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "TiposModeloDocumento.xls";
	
	public static final String NAME = "tipoModeloDocumentoHome";

	public static final TipoModeloDocumentoHome instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
	@Override
	public EntityList<TipoModeloDocumento> getBeanList() {
		return TipoModeloDocumentoList.instance();
	}
	
	@Override
	public String getTemplate() {
		return TEMPLATE;
	}
	
	@Override
	public String getDownloadXlsName() {
		return DOWNLOAD_XLS_NAME;
	}
	
}