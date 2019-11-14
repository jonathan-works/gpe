package br.com.infox.epp.webservice.log.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.system.annotation.Ignore;
import br.com.infox.epp.webservice.log.query.LogWebserviceClientQuery;

@Table(name = "tb_log_ws_client")
@Entity
@NamedQueries({
	@NamedQuery(name = LogWebserviceClientQuery.GET_REQUISICAO_FROM_LOG, query = LogWebserviceClientQuery.GET_REQUISICAO_FROM_LOG_QUERY),
	@NamedQuery(name = LogWebserviceClientQuery.GET_INFORMACOES_ADICIONAIS_FROM_LOG, query = LogWebserviceClientQuery.GET_INFORMACOES_ADICIONAIS_FROM_LOG_QUERY),
	@NamedQuery(name = LogWebserviceClientQuery.GET_RESPOSTA_FROM_LOG, query = LogWebserviceClientQuery.GET_RESPOSTA_FROM_LOG_QUERY)
})
@Ignore
public class LogWebserviceClient implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "LogWebserviceClientGenerator", sequenceName = "sq_log_ws_client", allocationSize = 1, initialValue = 1)
	@GeneratedValue(generator = "LogWebserviceClientGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_log_ws_client")
	private Long id;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_inicio_requisicao", nullable = false)
	private Date dataInicioRequisicao;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_fim_requisicao")
	private Date dataFimRequisicao;
	
	@NotNull
	@Column(name = "cd_webservice", nullable = false, length = 10)
	private String codigoWebService;
	
	@NotNull
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "ds_requisicao", nullable = false)
	private String requisicao;
	
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "ds_resposta")
	private String resposta;
	
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "ds_info_adicional")
	private String informacoesAdicionais;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataInicioRequisicao() {
		return dataInicioRequisicao;
	}

	public void setDataInicioRequisicao(Date dataInicioRequisicao) {
		this.dataInicioRequisicao = dataInicioRequisicao;
	}

	public Date getDataFimRequisicao() {
		return dataFimRequisicao;
	}

	public void setDataFimRequisicao(Date dataFimRequisicao) {
		this.dataFimRequisicao = dataFimRequisicao;
	}

	public String getCodigoWebService() {
		return codigoWebService;
	}

	public void setCodigoWebService(String codigoWebService) {
		this.codigoWebService = codigoWebService;
	}

	public String getRequisicao() {
		return requisicao;
	}

	public void setRequisicao(String requisicao) {
		this.requisicao = requisicao;
	}

	public String getResposta() {
		return resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}
	
	public String getInformacoesAdicionais() {
		return informacoesAdicionais;
	}
	
	public void setInformacoesAdicionais(String informacoesAdicionais) {
		this.informacoesAdicionais = informacoesAdicionais;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LogWebserviceClient))
			return false;
		LogWebserviceClient other = (LogWebserviceClient) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
