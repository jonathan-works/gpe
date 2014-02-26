package br.com.infox.epp.processo.documento.sigilo.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.COLUMN_ATIVO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.COLUMN_DATA_INCLUSAO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.COLUMN_ID;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.COLUMN_ID_PROCESSO_DOCUMENTO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.COLUMN_ID_USUARIO_LOGIN;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.COLUMN_MOTIVO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.NAMED_QUERY_DOCUMENTO_SIGILOSO_POR_ID_DOCUMENTO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.NAMED_QUERY_INATIVAR_SIGILOS;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.NAMED_QUERY_SIGILO_DOCUMENTO_ATIVO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.NAMED_QUERY_SIGILO_DOCUMENTO_ATIVO_POR_ID_DOCUMENTO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.QUERY_DOCUMENTO_SIGILOSO_POR_ID_DOCUMENTO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.QUERY_INATIVAR_SIGILOS;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.QUERY_SIGILO_DOCUMENTO_ATIVO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.QUERY_SIGILO_DOCUMENTO_ATIVO_POR_ID_DOCUMENTO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.SEQUENCE_NAME;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery.TABLE_NAME;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;

@Entity
@Table(name = TABLE_NAME)
@NamedQueries({
	@NamedQuery(name = NAMED_QUERY_SIGILO_DOCUMENTO_ATIVO, query = QUERY_SIGILO_DOCUMENTO_ATIVO),
	@NamedQuery(name = NAMED_QUERY_SIGILO_DOCUMENTO_ATIVO_POR_ID_DOCUMENTO, query = QUERY_SIGILO_DOCUMENTO_ATIVO_POR_ID_DOCUMENTO),
	@NamedQuery(name = NAMED_QUERY_DOCUMENTO_SIGILOSO_POR_ID_DOCUMENTO, query = QUERY_DOCUMENTO_SIGILOSO_POR_ID_DOCUMENTO),
	@NamedQuery(name = NAMED_QUERY_INATIVAR_SIGILOS, query = QUERY_INATIVAR_SIGILOS)
})
public class SigiloDocumento implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_NAME)
	@GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
	@Column(name = COLUMN_ID)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = COLUMN_ID_PROCESSO_DOCUMENTO, nullable = false)
	private ProcessoDocumento documento;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = COLUMN_ID_USUARIO_LOGIN, nullable = false)
	private UsuarioLogin usuario;
	
	@Column(name = COLUMN_MOTIVO, nullable = false, columnDefinition = "TEXT")
	private String motivo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_DATA_INCLUSAO, nullable = false)
	private Date dataInclusao;
	
	@Column(name = COLUMN_ATIVO, nullable = false)
	private Boolean ativo;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ProcessoDocumento getDocumento() {
		return documento;
	}
	
	public void setDocumento(ProcessoDocumento documento) {
		this.documento = documento;
	}
	
	public UsuarioLogin getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioLogin usuario) {
		this.usuario = usuario;
	}

	public String getMotivo() {
		return motivo;
	}
	
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	
	public Date getDataInclusao() {
		return dataInclusao;
	}
	
	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}
	
	public Boolean getAtivo() {
		return ativo;
	}
	
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}
