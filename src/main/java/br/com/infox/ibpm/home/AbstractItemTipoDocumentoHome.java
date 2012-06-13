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

import br.com.infox.ibpm.entity.ItemTipoDocumento;
import br.com.itx.component.AbstractHome;


public abstract class AbstractItemTipoDocumentoHome<T>
		extends
			AbstractHome<ItemTipoDocumento> {

	private static final long serialVersionUID = 1L;

	public void setItemTipoDocumentoIdItemTipoDocumento(Integer id) {
		setId(id);
	}

	public Integer getItemTipoDocumentoIdItemTipoDocumento() {
		return (Integer) getId();
	}

	@Override
	protected ItemTipoDocumento createInstance() {
		ItemTipoDocumento itemTipoDocumento = new ItemTipoDocumento();
		return itemTipoDocumento;
	}

	@Override
	public String persist() {
		String action = super.persist();
		newInstance();
		return action;
	}

}