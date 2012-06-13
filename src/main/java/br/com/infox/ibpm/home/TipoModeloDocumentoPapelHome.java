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
import org.jboss.seam.contexts.Contexts;

import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.infox.ibpm.entity.TipoModeloDocumentoPapel;
import br.com.itx.util.ComponentUtil;


@Name(TipoModeloDocumentoPapelHome.NAME)
@BypassInterceptors
public class TipoModeloDocumentoPapelHome extends AbstractTipoModeloDocumentoPapelHome<TipoModeloDocumentoPapel> {

	public static final String NAME = "tipoModeloDocumentoPapelHome";
	private static final long serialVersionUID = 1L;


	@Override
	public void newInstance() {
		TipoModeloDocumento tipoModeloDocumento = TipoModeloDocumentoHome.instance().getInstance();
		super.newInstance();
		getInstance().setTipoModeloDocumento(tipoModeloDocumento);
	}

	public static TipoModeloDocumentoPapelHome instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
	@Override
	public String persist() {
		String msg = super.persist();
		newInstance();
		refresh();
		return msg;
	}

	private void refresh() {
		refreshGrid("tipoModeloDocumentoPapelGrid");
		Contexts.removeFromAllContexts("papelItems");
	}
	
	@Override
	public String remove(TipoModeloDocumentoPapel obj) {
		String msg = super.remove(obj);
		newInstance();
		refresh();
		return msg;
	}
	
	@Override
	public String remove() {
		String msg = super.remove();
		refresh();
		return msg;
	}
	
}