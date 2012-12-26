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
package br.com.infox.ibpm.bean;

import java.io.Serializable;
import java.util.Date;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.type.TipoOperacaoLogEnum;
import br.com.infox.util.DateUtil;


public class ConsultaEntidadeLog implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String ip;
	private String nomeEntidade;
	private Date dataInicio;
	private Date dataFim = new Date();
	private UsuarioLogin usuario;
	private TipoOperacaoLogEnum tipoOperacaoLogEnum = null;
	private Boolean inPesquisa = false;
	
	public ConsultaEntidadeLog() {
		dataFim = new Date();
		dataInicio = DateUtil.getBeginningOfDay(dataFim);
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getNomeEntidade() {
		return nomeEntidade;
	}
	public void setNomeEntidade(String nomeEntidade) {
		this.nomeEntidade = nomeEntidade;
	}
	public UsuarioLogin getUsuario() {
		return usuario;
	}
	public void setUsuario(UsuarioLogin usuario) {
		this.usuario = usuario;
	}
	
	public void setTipoOperacaoLogEnum(TipoOperacaoLogEnum tipoOperacaoLogEnum) {
		this.tipoOperacaoLogEnum = tipoOperacaoLogEnum;
	}
	
	public TipoOperacaoLogEnum getTipoOperacaoLogEnum() {
		return tipoOperacaoLogEnum;
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
		this.dataFim = DateUtil.getEndOfDay(dataFim);
	}
	
	public Boolean getInPesquisa() {
		return inPesquisa;
	}
	
	public void setInPesquisa(Boolean inPesquisa) {
		this.inPesquisa = inPesquisa;
	}
	
	@Override
	public String toString() {
		return nomeEntidade;
	}
}