package br.com.infox.epp.meiocontato.entity;

import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.EXISTE_MEIO_CONTATO_BY_PESSOA_TIPO_VALOR;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.EXISTE_MEIO_CONTATO_BY_PESSOA_TIPO_VALOR_QUERY;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.MEIO_CONTATO_BY_PESSOA;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.MEIO_CONTATO_BY_PESSOA_AND_TIPO;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.MEIO_CONTATO_BY_PESSOA_AND_TIPO_QUERY;
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

@Entity
@Table(name = MeioContato.TABLE_NAME)
@NamedQueries(value = {
		@NamedQuery(name = MEIO_CONTATO_BY_PESSOA, query = MEIO_CONTATO_BY_PESSOA_QUERY),
		@NamedQuery(name = MEIO_CONTATO_BY_PESSOA_AND_TIPO, query = MEIO_CONTATO_BY_PESSOA_AND_TIPO_QUERY),
		@NamedQuery(name = EXISTE_MEIO_CONTATO_BY_PESSOA_TIPO_VALOR, query = EXISTE_MEIO_CONTATO_BY_PESSOA_TIPO_VALOR_QUERY)
})
public class MeioContato implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_meio_contato";

	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "MeioContatoGenerator", sequenceName = "sq_meio_contato")
	@GeneratedValue(generator = "MeioContatoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_meio_contato", nullable = false, unique = true)
	private Integer idMeioContato;
	
	@NotNull
	@Column(name = "vl_meio_contato", nullable = false)
	private String meioContato;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "tp_meio_contato", nullable = false)
	private TipoMeioContatoEnum tipoMeioContato;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa", nullable = false)
	private Pessoa pessoa;
	
	public MeioContato() {
	}
	
	public MeioContato(TipoMeioContatoEnum tipoMeioContato) {
	    this.tipoMeioContato = tipoMeioContato;
    }
	
	public Integer getIdMeioContato() {
		return idMeioContato;
	}

	public void setIdMeioContato(Integer idMeioContato) {
		this.idMeioContato = idMeioContato;
	}

	public String getMeioContato() {
		return meioContato;
	}

	public void setMeioContato(String meioContato) {
		this.meioContato = meioContato;
	}

	public TipoMeioContatoEnum getTipoMeioContato() {
		return tipoMeioContato;
	}

	public void setTipoMeioContato(TipoMeioContatoEnum tipoMeioContato) {
		this.tipoMeioContato = tipoMeioContato;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}
	
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	@Override
	public String toString() {
		return getMeioContato();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getIdMeioContato() == null) ? 0 : getIdMeioContato().hashCode());
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
		if (getIdMeioContato() == null) {
			if (other.getIdMeioContato() != null)
				return false;
		} else if (!getIdMeioContato().equals(other.getIdMeioContato()))
			return false;
		return true;
	}
	
}