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

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.Papel;
import br.com.infox.epp.manager.PapelManager;
import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.infox.ibpm.entity.TipoModeloDocumentoPapel;
import br.com.itx.util.ComponentUtil;


@Name(TipoModeloDocumentoPapelHome.NAME)
public class TipoModeloDocumentoPapelHome extends AbstractTipoModeloDocumentoPapelHome<TipoModeloDocumentoPapel> {

	public static final String NAME = "tipoModeloDocumentoPapelHome";
	private static final long serialVersionUID = 1L;

	@In private PapelManager papelManager;

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
		return msg;
	}
	
	@Override
	public String update() {
		instance.setTipoModeloDocumento(TipoModeloDocumentoHome.instance().getInstance());
		return super.update();
	}

	@Override
	public String remove(TipoModeloDocumentoPapel obj) {
		String msg = super.remove(obj);
		newInstance();
		return msg;
	}
	
	public List<Papel> getPapeisNaoAssociadosATipoModeloDocumentoAtual(){
		return papelManager.getPapeisNaoAssociadosATipoModeloDocumento(instance.getTipoModeloDocumento());
	}
	
}