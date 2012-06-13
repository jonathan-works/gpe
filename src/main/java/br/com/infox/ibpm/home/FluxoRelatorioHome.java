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


import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.bean.FluxoRelatorio;


@Scope(ScopeType.CONVERSATION)
@Name("fluxoRelatorioHome")
@BypassInterceptors
public class FluxoRelatorioHome implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private FluxoRelatorio instance = new FluxoRelatorio();
	   
	private Integer idPesquisa;

	public Integer getIdPesquisa() {
		return idPesquisa;
	}
	public void setIdPesquisa(Integer idFluxoPesquisa) {
		this.idPesquisa = idFluxoPesquisa;
	}	
	
	public FluxoRelatorio getInstance() {
		return instance;
	}
	
	public void setInstance(FluxoRelatorio instance) {
		this.instance = instance;
	}
	

	public boolean isEditable() {
		return true;
	}	
	
	public void limparTela() {
		setIdPesquisa(null);
		instance = new FluxoRelatorio();
	}	
}