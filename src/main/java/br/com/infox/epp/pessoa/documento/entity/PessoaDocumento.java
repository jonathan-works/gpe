package br.com.infox.epp.pessoa.documento.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.pessoa.documento.type.TipoDocumentoPessoaEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;

@Entity
@Table(name = PessoaDocumento.TABLE_NAME)
public class PessoaDocumento implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_pessoa_documento";

	private Integer idPessoaDocumento;
	private Pessoa pessoa;
	private String documento;
	private String orgaoEmissor;
	private Date dataEmissao;
	private TipoDocumentoPessoaEnum tipoDocumento;

	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "generator", sequenceName = "sq_pessoa_documento")
	@Id
	@GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_pessoa_documento", nullable = false, unique = true)
	public Integer getIdPessoaDocumento() {
		return idPessoaDocumento;
	}

	public void setIdPessoaDocumento(Integer idPessoaDocumento) {
		this.idPessoaDocumento = idPessoaDocumento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa", nullable = false)
	@NotNull
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@Column(name = "vl_documento", nullable = false)
	@NotNull
	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	@Column(name = "ds_orgao_emissor", nullable = false)
	@NotNull
	public String getOrgaoEmissor() {
		return orgaoEmissor;
	}

	public void setOrgaoEmissor(String orgaoEmissor) {
		this.orgaoEmissor = orgaoEmissor;
	}

	@Column(name = "dt_emissao", nullable = false)
	@NotNull
	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	@Column(name = "tp_documento", nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	public TipoDocumentoPessoaEnum getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumentoPessoaEnum tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
}