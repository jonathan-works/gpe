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

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.ibpm.entity.GrupoModeloDocumento;
import br.com.infox.list.GrupoModeloDocumentoList;

@Name(GrupoModeloDocumentoHome.NAME)
@BypassInterceptors
public class GrupoModeloDocumentoHome extends
		AbstractGrupoModeloDocumentoHome<GrupoModeloDocumento> {

	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/GrupoModeloDocumento/grupoModeloDocumentoTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "GruposModeloDocumento.xls";

	public static final String NAME = "grupoModeloDocumentoHome";

	@Override
	public String getDownloadXlsName() {
		return DOWNLOAD_XLS_NAME;
	}

	@Override
	public String getTemplate() {
		return TEMPLATE;
	}

	@Override
	public EntityList<GrupoModeloDocumento> getBeanList() {
		return GrupoModeloDocumentoList.instance();
	}
}