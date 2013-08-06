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

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.ibpm.entity.Cep;
import br.com.infox.ibpm.entity.Estado;
import br.com.infox.list.CepList;
import br.com.itx.component.AbstractHome;

@Name(CepHome.NAME)
@Scope(ScopeType.PAGE)
@BypassInterceptors
public class CepHome extends AbstractHome<Cep> {
	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/CEP/CepTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "CEP.xls";
	
	public static final String NAME = "cepHome";
	
	private Estado estado = null;
	
	@Override
	public EntityList<Cep> getBeanList() {
		return CepList.instance();
	}
	
	@Override
	public String getTemplate() {
		return TEMPLATE;
	}
	
	@Override
	public String getDownloadXlsName() {
		return DOWNLOAD_XLS_NAME;
	}
	
	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	@Override
	public void setId(Object id) {
		boolean changed = id != null && !id.equals(getId());
		super.setId(id);
		if (changed && instance.getMunicipio() != null) {
			estado = instance.getMunicipio().getEstado();
		}
	}

	@Override
	public void newInstance() {
		estado = null;
		super.newInstance();
	}
	
	public static CepHome instance() {
		return (CepHome) Component.getInstance(NAME);
	}
	
}