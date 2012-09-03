package br.com.infox.ibpm.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import org.hibernate.validator.Length;

import com.sun.istack.internal.NotNull;

@Entity
@Table(schema="core", name=ProtocoloDocumento.NAME)
public class ProtocoloDocumento implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "tb_protocolo_documento";
	
	private Integer idProtocoloDocumento;
	private DocumentoFisico documentoFisico;
	private String nomePessoa;
	private Date dataEntrada;
	private Date dataSaida;
	private Boolean ativo;
	
	@SequenceGenerator(name="generator", sequenceName="core.sq_tb_protocolo_documento")
	@Id
	@GeneratedValue(generator="generator")
	@Column(name="id_protocolo_documento")
	public Integer getIdProtocoloDocumento() {
		return idProtocoloDocumento;
	}
	public void setIdProtocoloDocumento(Integer idProtocoloDocumento) {
		this.idProtocoloDocumento = idProtocoloDocumento;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_documento_fisico")
	@NotNull
	public DocumentoFisico getDocumentoFisico() {
		return documentoFisico;
	}	
	public void setDocumentoFisico(DocumentoFisico documentoFisico) {
		this.documentoFisico = documentoFisico;
	}
	
	@Column(name="nm_pessoa", nullable=false)
	@Length(max=100)
	public String getNomePessoa() {
		return nomePessoa;
	}
	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}
	
	@Column(name="dt_entrada", nullable=false)
	public Date getDataEntrada() {
		return dataEntrada;
	}
	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}
	
	@Column(name="dt_saida", nullable=false)
	public Date getDataSaida() {
		return dataSaida;
	}
	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}
	
	@Column(name="in_ativo", nullable=false)
	public Boolean getAtivo() {
		return ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
}
