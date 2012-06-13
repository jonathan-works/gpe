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

/**
 * @author Rodrigo Menezes
 * 
 * Bean utilizado para os formularios de pesquisa, que utilizam Fluxo.
 * 
 * UC-11 e UC-12
 */
package br.com.infox.ibpm.bean;

import java.util.Date;


public class FluxoRelatorio {
	private String fluxo;
	private Date dataInicio;
	private Date dataFim;
	private String inAtivo;
	private Boolean inPesquisa = false;
	
	public String getFluxo() {
		return fluxo;
	}
	public void setFluxo(String fluxo) {
		this.fluxo = fluxo;
	}
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = br.com.infox.util.DateUtil.getEndOfDay(dataFim);
	}
	public String getInAtivo() {
		return inAtivo;
	}
	public void setInAtivo(String inAtivo) {
		this.inAtivo = inAtivo;
	}
	
	public Boolean getInPesquisa() {
		return inPesquisa;
	}
	
	public void setInPesquisa(Boolean inPesquisa) {
		this.inPesquisa = inPesquisa;
	}
}