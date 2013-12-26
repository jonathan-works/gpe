package br.com.infox.epp.documento.entity;

import static br.com.infox.core.persistence.ORConstants.*;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.*;

import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Entity
@Table(name = TABLE_HISTORICO_MODELO_DOCUMENTO, schema=PUBLIC)
@NamedQueries(value={
    @NamedQuery(name=LIST_MODELO, query=LIST_MODELO_QUERY),
    @NamedQuery(name=LIST_USUARIO, query=LIST_USUARIO_QUERY)
})
public class HistoricoModeloDocumento implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private int idHistoricoModeloDocumento;
	private ModeloDocumento modeloDocumento;
	private UsuarioLogin usuarioAlteracao;
	private Date dataAlteracao;
	private String tituloModeloDocumento;
	private String descricaoModeloDocumento;
	private Boolean ativo;
	
	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_HISTORICO_MODELO_DOCUMENTO)
	@Id
	@GeneratedValue(generator = GENERATOR)
	@Column(name = ID_HISTORICO_MODELO_DOCUMENTO, unique = true, nullable = false)
	public int getIdHistoricoModeloDocumento() {
		return idHistoricoModeloDocumento;
	}

	public void setIdHistoricoModeloDocumento(int idHistoricoModeloDocumento) {
		this.idHistoricoModeloDocumento = idHistoricoModeloDocumento;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name=ID_MODELO_DOCUMENTO)
	public ModeloDocumento getModeloDocumento() {
		return modeloDocumento;
	}
	public void setModeloDocumento(ModeloDocumento modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name=ID_USUARIO_ALTERACAO)
	public UsuarioLogin getUsuarioAlteracao() {
		return usuarioAlteracao;
	}
	public void setUsuarioAlteracao(UsuarioLogin usuarioAlteracao) {
		this.usuarioAlteracao = usuarioAlteracao;
	}

	@Column(name=DATA_ALTERACAO, nullable=false)
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
	
	@Column(name=TITULO_MODELO_DOCUMENTO, nullable=false, length=LengthConstants.DESCRICAO_TITULO)
	@Size(max=LengthConstants.DESCRICAO_TITULO)
	public String getTituloModeloDocumento() {
		return tituloModeloDocumento;
	}
	public void setTituloModeloDocumento(String tituloModeloDocumento) {
		this.tituloModeloDocumento = tituloModeloDocumento;
	}

	@Column(name=CONTEUDO_MODELO_DOCUMENTO, nullable=false)
	public String getDescricaoModeloDocumento() {
		return descricaoModeloDocumento;
	}
	public void setDescricaoModeloDocumento(String descricaoModeloDocumento) {
		this.descricaoModeloDocumento = descricaoModeloDocumento;
	}

	@Column(name=ATIVO, nullable=false)
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