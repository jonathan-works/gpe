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

import org.jboss.seam.Component;

import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.itx.component.AbstractHome;


public abstract class AbstractModeloDocumentoHome<T>
		extends
			AbstractHome<ModeloDocumento> {

	private static final long serialVersionUID = 1L;

	@Override
	protected ModeloDocumento createInstance() {
		ModeloDocumento modeloDocumento = new ModeloDocumento();
		TipoModeloDocumentoHome tipoModeloDocumentoHome = (TipoModeloDocumentoHome) Component
				.getInstance("tipoModeloDocumentoHome", false);
		if (tipoModeloDocumentoHome != null) {
			modeloDocumento.setTipoModeloDocumento(tipoModeloDocumentoHome
					.getDefinedInstance());
		}
		return modeloDocumento;
	}

	@Override
	public String remove() {
		TipoModeloDocumentoHome tipoModeloDocumento = (TipoModeloDocumentoHome) Component
				.getInstance("tipoModeloDocumentoHome", false);
		if (tipoModeloDocumento != null) {
			tipoModeloDocumento.getInstance().getModeloDocumentoList().remove(
					instance);
		}
		return super.remove();
	}

	@Override
	public String remove(ModeloDocumento obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		return ret;
	}

	@Override
	public String persist() {
		String action = super.persist();
		if (getInstance().getTipoModeloDocumento() != null) {
			List<ModeloDocumento> tipoModeloDocumentoList = getInstance()
					.getTipoModeloDocumento().getModeloDocumentoList();
			if (!tipoModeloDocumentoList.contains(instance)) {
				getEntityManager().refresh(
						getInstance().getTipoModeloDocumento());
			}
		}
		return action;
	}

}