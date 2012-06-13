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

import br.com.infox.ibpm.entity.GrupoModeloDocumento;
import br.com.infox.ibpm.entity.ItemTipoDocumento;
import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.itx.component.AbstractHome;


public abstract class AbstractGrupoModeloDocumentoHome<T>
		extends
			AbstractHome<GrupoModeloDocumento> {

	private static final long serialVersionUID = 1L;

	@Override
	protected GrupoModeloDocumento createInstance() {
		GrupoModeloDocumento grupoModeloDocumento = new GrupoModeloDocumento();
		return grupoModeloDocumento;
	}

	public String remove(GrupoModeloDocumento obj) {
		setInstance(obj);
		getInstance().setAtivo(Boolean.FALSE);
		String ret = super.update();
		newInstance();
		return ret;
	}

	public List<ItemTipoDocumento> getItemTipoDocumentoList() {
		return getInstance() == null ? null : getInstance()
				.getItemTipoDocumentoList();
	}

	public List<TipoModeloDocumento> getTipoModeloDocumentoList() {
		return getInstance() == null ? null : getInstance()
				.getTipoModeloDocumentoList();
	}

}