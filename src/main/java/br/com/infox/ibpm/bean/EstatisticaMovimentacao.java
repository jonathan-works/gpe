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
package br.com.infox.ibpm.bean;

import java.util.Date;


//TODO VErificar se esta classe é usada pra alguma coisa ainda

public class EstatisticaMovimentacao {
	private Date dataInicio;
	private Date dataFim;
	private String fluxo;
	private String tumpInicial;
	private String tumpFinal;
	private String nivelLocalizacao;
	private Boolean inPesquisa = false;
	
	
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
	
	public String getFluxo() {
		return fluxo;
	}
	public void setFluxo(String fluxo) {
		this.fluxo = fluxo;
	}
	public String getTumpInicial() {
		return tumpInicial;
	}
	public void setTumpInicial(String tumpInicial) {
		this.tumpInicial = tumpInicial;
	}
	public String getTumpFinal() {
		return tumpFinal;
	}
	public void setTumpFinal(String tumpFinal) {
		this.tumpFinal = tumpFinal;
	}
	public String getNivelLocalizacao() {
		return nivelLocalizacao;
	}
	public void setNivelLocalizacao(String nivelLocalizacao) {
		this.nivelLocalizacao = nivelLocalizacao;
	}
	
	public Boolean getInPesquisa() {
		return inPesquisa;
	}
	
	public void setInPesquisa(Boolean inPesquisa) {
		this.inPesquisa = inPesquisa;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
}