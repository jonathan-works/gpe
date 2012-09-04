package br.com.infox.ibpm.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = HistoricoModeloDocumento.TABLE_NAME, schema="public")
public class HistoricoModeloDocumento implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_modelo_documento_historico";

	private static final long serialVersionUID = 1L;
	
	private int idHistoricoModeloDocumento;
	private ModeloDocumento modeloDocumento;
	private Usuario usuarioAlteracao;
	private Date dataAlteracao;
	private String tituloModeloDocumento;
	private String descricaoModeloDocumento;
	private Boolean ativo;
	
	@SequenceGenerator(name = "generator", sequenceName = "sq_tb_modelo_documento_historico")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_modelo_documento_historico", unique = true, nullable = false)
	public int getIdistoricoModeloDocumento() {
		return idHistoricoModeloDocumento;
	}

	public void setIdistoricoModeloDocumento(int idistoricoModeloDocumento) {
		this.idHistoricoModeloDocumento = idistoricoModeloDocumento;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_modelo_documento")
	public ModeloDocumento getModeloDocumento() {
		return modeloDocumento;
	}
	public void setModeloDocumento(ModeloDocumento modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_usuario_alteracao")
	public Usuario getUsuarioAlteracao() {
		return usuarioAlteracao;
	}
	public void setUsuarioAlteracao(Usuario usuarioAlteracao) {
		this.usuarioAlteracao = usuarioAlteracao;
	}

	@Column(name="dt_alteracao", nullable=false)
	public Date getDataAlteracao() {
		return dataAlteracao;
	}
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	@Column(name="ds_titulo_modelo_documento", nullable=false, length=60)
	public String getTituloModeloDocumento() {
		return tituloModeloDocumento;
	}
	public void setTituloModeloDocumento(String tituloModeloDocumento) {
		this.tituloModeloDocumento = tituloModeloDocumento;
	}

	@Column(name="ds_modelo_documento", nullable=false)
	public String getDescricaoModeloDocumento() {
		return descricaoModeloDocumento;
	}
	public void setDescricaoModeloDocumento(String descricaoModeloDocumento) {
		this.descricaoModeloDocumento = descricaoModeloDocumento;
	}

	@Column(name="in_ativo", nullable=false)
	public Boolean getAtivo() {
		return ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public String toString() {
		return this.tituloModeloDocumento;
	}

}