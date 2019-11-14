package br.com.infox.epp.pessoa.documento.entity;

import static br.com.infox.epp.pessoa.documento.query.PessoaDocumentoQuery.PESSOA_DOCUMENTO_BY_PESSOA_TPDOCUMENTO;
import static br.com.infox.epp.pessoa.documento.query.PessoaDocumentoQuery.PESSOA_DOCUMENTO_BY_PESSOA_TPDOCUMENTO_QUERY;
import static br.com.infox.epp.pessoa.documento.query.PessoaDocumentoQuery.USUARIO_POR_DOCUMENTO_E_TIPO;
import static br.com.infox.epp.pessoa.documento.query.PessoaDocumentoQuery.USUARIO_POR_DOCUMENTO_E_TIPO_QUERY;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.pessoa.documento.type.TipoPesssoaDocumentoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;

@Entity
@Table(name = PessoaDocumento.TABLE_NAME)
@NamedQueries(value = {
		@NamedQuery(name = PESSOA_DOCUMENTO_BY_PESSOA_TPDOCUMENTO, query = PESSOA_DOCUMENTO_BY_PESSOA_TPDOCUMENTO_QUERY),
		@NamedQuery(name = USUARIO_POR_DOCUMENTO_E_TIPO, query = USUARIO_POR_DOCUMENTO_E_TIPO_QUERY)
})
public class PessoaDocumento implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_pessoa_documento";

	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "PessoaDocumentoGenerator", sequenceName = "sq_pessoa_documento")
	@GeneratedValue(generator = "PessoaDocumentoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_pessoa_documento", nullable = false, unique = true)
	private Integer idPessoaDocumento;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa", nullable = false)
	private Pessoa pessoa;
	
	@NotNull
	@Column(name = "vl_documento", nullable = false)
	private String documento;
	
	@Column(name = "ds_orgao_emissor")
	private String orgaoEmissor;
	
	@Column(name = "dt_emissao")
	private Date dataEmissao;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "tp_documento", nullable = false)
	private TipoPesssoaDocumentoEnum tipoDocumento;
	
	public Integer getIdPessoaDocumento() {
		return idPessoaDocumento;
	}

	public void setIdPessoaDocumento(Integer idPessoaDocumento) {
		this.idPessoaDocumento = idPessoaDocumento;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getOrgaoEmissor() {
		return orgaoEmissor;
	}

	public void setOrgaoEmissor(String orgaoEmissor) {
		this.orgaoEmissor = orgaoEmissor;
	}

	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public TipoPesssoaDocumentoEnum getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoPesssoaDocumentoEnum tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((getIdPessoaDocumento() == null) ? 0 : getIdPessoaDocumento()
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PessoaDocumento))
			return false;
		PessoaDocumento other = (PessoaDocumento) obj;
		if (getIdPessoaDocumento() == null) {
			if (other.getIdPessoaDocumento() != null)
				return false;
		} else if (!getIdPessoaDocumento().equals(other.getIdPessoaDocumento()))
			return false;
		return true;
	}
	
}