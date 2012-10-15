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

import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;
import br.com.itx.component.AbstractHome;


public abstract class AbstractTipoProcessoDocumentoHome<T>
		extends
			AbstractHome<TipoProcessoDocumento> {

	private static final long serialVersionUID = 1L;

	public void setTipoProcessoDocumentoIdTipoProcessoDocumento(Integer id) {
		setId(id);
	}

	public Integer getTipoProcessoDocumentoIdTipoProcessoDocumento() {
		return (Integer) getId();
	}

	@Override
	protected TipoProcessoDocumento createInstance() {
		return new TipoProcessoDocumento();
	}

	@Override
	public String remove(TipoProcessoDocumento obj) {
		setInstance(obj);
		obj.setAtivo(Boolean.FALSE);
		String ret = super.update();
		newInstance();
		return ret;
	}

	public List<ProcessoDocumento> getProcessoDocumentoList() {
		return getInstance() == null ? null : getInstance()
				.getProcessoDocumentoList();
	}

}