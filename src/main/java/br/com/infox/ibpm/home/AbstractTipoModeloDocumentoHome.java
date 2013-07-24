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
import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.itx.component.AbstractHome;


public abstract class AbstractTipoModeloDocumentoHome<T>
		extends
			AbstractHome<TipoModeloDocumento> {

	private static final long serialVersionUID = 1L;

	public void setTipoModeloDocumentoIdTipoModeloDocumento(Integer id) {
		setId(id);
	}

	public Integer getTipoModeloDocumentoIdTipoModeloDocumento() {
		return (Integer) getId();
	}

	@Override
	protected TipoModeloDocumento createInstance() {
		TipoModeloDocumento tipoModeloDocumento = new TipoModeloDocumento();
		GrupoModeloDocumentoHome grupoModeloDocumentoHome = (GrupoModeloDocumentoHome) Component
				.getInstance("grupoModeloDocumentoHome", false);
		if (grupoModeloDocumentoHome != null) {
			tipoModeloDocumento
					.setGrupoModeloDocumento(grupoModeloDocumentoHome
							.getDefinedInstance());
		}
		return tipoModeloDocumento;
	}

	@Override
	public String remove() {
		GrupoModeloDocumentoHome grupoModeloDocumento = (GrupoModeloDocumentoHome) Component
				.getInstance("grupoModeloDocumentoHome", false);
		if (grupoModeloDocumento != null) {
			grupoModeloDocumento.getInstance().getTipoModeloDocumentoList()
					.remove(instance);
		}
		return super.remove();
	}

	@Override
	public String remove(TipoModeloDocumento obj) {
		setInstance(obj);
		getInstance().setAtivo(Boolean.FALSE);
		String ret = super.update();
		newInstance();
		return ret;
	}

	@Override
	public String persist() {
		String action = super.persist();
		if (getInstance().getGrupoModeloDocumento() != null) {
			List<TipoModeloDocumento> grupoModeloDocumentoList = getInstance()
					.getGrupoModeloDocumento().getTipoModeloDocumentoList();
			if (!grupoModeloDocumentoList.contains(instance)) {
				getEntityManager().refresh(
						getInstance().getGrupoModeloDocumento());
			}
		}
		return action;
	}

	public List<ModeloDocumento> getModeloDocumentoList() {
		return getInstance() == null ? null : getInstance()
				.getModeloDocumentoList();
	}
	
}