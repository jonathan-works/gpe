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