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