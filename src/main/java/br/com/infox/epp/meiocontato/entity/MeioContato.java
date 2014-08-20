package br.com.infox.epp.meiocontato.entity;

import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.MEIO_CONTATO_BY_PESSOA;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.MEIO_CONTATO_BY_PESSOA_AND_TIPO_MEIO_CONTATO;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.MEIO_CONTATO_BY_PESSOA_AND_TIPO_MEIO_CONTATO_QUERY;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.MEIO_CONTATO_BY_PESSOA_QUERY;

import java.io.Serializable;

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

import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;

import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.MEIO_CONTATO_BY_PESSOA_AND_TIPO;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.MEIO_CONTATO_BY_PESSOA_AND_TIPO_QUERY;
@Entity
@Table(name = MeioContato.TABLE_NAME)
@NamedQueries(value = {
		@NamedQuery(name = MEIO_CONTATO_BY_PESSOA, query = MEIO_CONTATO_BY_PESSOA_QUERY),
		@NamedQuery(name = MEIO_CONTATO_BY_PESSOA_AND_TIPO, query = MEIO_CONTATO_BY_PESSOA_AND_TIPO_QUERY)
})
public class MeioContato implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_meio_contato";

	private Integer idMeioContato;
	private String meioContato;
	private TipoMeioContatoEnum tipoMeioContato;
	private Pessoa pessoa;

	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "MeioContatoGenerator", sequenceName = "sq_meio_contato")
	@Id
	@GeneratedValue(generator = "MeioContatoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_meio_contato", nullable = false, unique = true)
	public Integer getIdMeioContato() {
		return idMeioContato;
	}

	public void setIdMeioContato(Integer idMeioContato) {
		this.idMeioContato = idMeioContato;
	}

	@Column(name = "vl_meio_contato", nullable = false)
	@NotNull
	public String getMeioContato() {
		return meioContato;
	}

	public void setMeioContato(String meioContato) {
		this.meioContato = meioContato;
	}

	@Column(name = "tp_meio_contato", nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	public TipoMeioContatoEnum getTipoMeioContato() {
		return tipoMeioContato;
	}

	public void setTipoMeioContato(TipoMeioContatoEnum tipoMeioContato) {
		this.tipoMeioContato = tipoMeioContato;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idMeioContato == null) ? 0 : idMeioContato.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MeioContato))
			return false;
		MeioContato other = (MeioContato) obj;
		if (idMeioContato == null) {
			if (other.idMeioContato != null)
				return false;
		} else if (!idMeioContato.equals(other.idMeioContato))
			return false;
		return true;
	}
	
}