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

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

import br.com.infox.ibpm.component.suggest.GrupoModeloDocumentoSuggestBean;
import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.itx.util.ComponentUtil;

@Name("tipoModeloDocumentoHome")
@BypassInterceptors
public class TipoModeloDocumentoHome
		extends
			AbstractTipoModeloDocumentoHome<TipoModeloDocumento> {

	private static final long serialVersionUID = 1L;


	@Override
	public void newInstance() {
		Contexts.removeFromAllContexts("grupoModeloDocumentoSuggest");	
		super.newInstance();
	}
	
	@Override
	public void setId(Object id) {
		boolean changed = id != null && !id.equals(getId());
		super.setId(id);
		if (changed) {
			getGrupoModeloDocumentoSuggest().setInstance(getInstance().getGrupoModeloDocumento());
		}
		if (id == null) {
			getGrupoModeloDocumentoSuggest().setInstance(null);
		} 
	}
	
	@Override
	protected boolean beforePersistOrUpdate() {
		getInstance().setGrupoModeloDocumento(getGrupoModeloDocumentoSuggest().getInstance());
		return super.beforePersistOrUpdate();
	}
	 
	private GrupoModeloDocumentoSuggestBean getGrupoModeloDocumentoSuggest() {
		GrupoModeloDocumentoSuggestBean grupoModeloDocumentoSuggest = (GrupoModeloDocumentoSuggestBean) Component.getInstance("grupoModeloDocumentoSuggest");
		return grupoModeloDocumentoSuggest ;
	}
	
	public static TipoModeloDocumentoHome instance() {
		return ComponentUtil.getComponent("tipoModeloDocumentoHome");
	}
	
	
}