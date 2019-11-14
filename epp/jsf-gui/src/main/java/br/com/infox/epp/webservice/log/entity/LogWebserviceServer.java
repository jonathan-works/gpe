package br.com.infox.epp.webservice.log.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import br.com.infox.epp.system.annotation.Ignore;

@Entity
@Table(name=LogWebserviceServer.TABLE_NAME)
@Ignore
public class LogWebserviceServer implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_log_ws_server";
	
	@Id
	@SequenceGenerator(initialValue=1, allocationSize=1, name="LogWsServerGenerator" ,sequenceName="sq_log_ws_server")
	@GeneratedValue(generator="LogWsServerGenerator", strategy=GenerationType.SEQUENCE)
	@Column(name="id_log_ws_server", unique=true, nullable=false)
	private BigInteger idLogWsServer;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_inicio_requisicao", nullable=false)
	private Date dataInicio;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_fim_requisicao")
	private Date dataFim;
	
	@Column(name="cd_mensagem_retorno")
	private String mensagemRetorno;
	
	@Column(name="cd_webservice", nullable=true)
	@Size(max=2000)
	private String webService;
	
	@Column(name="vl_token", nullable=true)
	@Size(max=30)
	private String token;
	
	@Column(name="ds_requisicao", nullable=true)
	@Basic(fetch=FetchType.LAZY)
	private String requisicao;

	public BigInteger getIdLogWsServer() {
		return idLogWsServer;
	}

	public void setIdLogWsServer(BigInteger idLogWsServer) {
		this.idLogWsServer = idLogWsServer;
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
		this.dataFim = dataFim;
	}

	public String getMensagemRetorno() {
		return mensagemRetorno;
	}

	public void setMensagemRetorno(String mensagemRetorno) {
		this.mensagemRetorno = mensagemRetorno;
	}

	public String getWebService() {
		return webService;
	}

	public void setWebService(String webService) {
		this.webService = webService;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public String getRequisicao() {
		return requisicao;
	}

	public void setRequisicao(String requisicao) {
		this.requisicao = requisicao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idLogWsServer == null) ? 0 : idLogWsServer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LogWebserviceServer))
			return false;
		LogWebserviceServer other = (LogWebserviceServer) obj;
		if (idLogWsServer == null) {
			if (other.idLogWsServer != null)
				return false;
		} else if (!idLogWsServer.equals(other.idLogWsServer))
			return false;
		return true;
	}
	
}
