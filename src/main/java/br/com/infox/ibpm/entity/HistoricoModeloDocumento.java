package br.com.infox.ibpm.entity;

import java.text.DateFormat;
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
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.util.constants.LengthConstants;

@Entity
@Table(name = HistoricoModeloDocumento.TABLE_NAME, schema="public")
public class HistoricoModeloDocumento implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_modelo_documento_historico";

	private static final long serialVersionUID = 1L;
	
	private int idHistoricoModeloDocumento;
	private ModeloDocumento modeloDocumento;
	private UsuarioLogin usuarioAlteracao;
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
	public UsuarioLogin getUsuarioAlteracao() {
		return usuarioAlteracao;
	}
	public void setUsuarioAlteracao(UsuarioLogin usuarioAlteracao) {
		this.usuarioAlteracao = usuarioAlteracao;
	}

	@Column(name="dt_alteracao", nullable=false)
	public Date getDataAlteracao() {
		return dataAlteracao;
	}
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	@Transient
	public String getDataAlteracaoFormatada() {
	    if (dataAlteracao == null) {
	        return "";
	    }
	    return DateFormat.getDateInstance().format(dataAlteracao);
	}
	
	@Column(name="ds_titulo_modelo_documento", nullable=false, length=LengthConstants.DESCRICAO_TITULO)
	@Size(max=LengthConstants.DESCRICAO_TITULO)
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