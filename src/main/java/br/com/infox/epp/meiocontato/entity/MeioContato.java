package br.com.infox.epp.meiocontato.entity;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;

@Entity
@Table(name = MeioContato.TABLE_NAME)
public class MeioContato implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_meio_contato";

	private Integer idMeioContato;
	private String meioCotnato;
	private TipoMeioContatoEnum tipoMeioContato;
	private Pessoa pessoa;

	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "generator", sequenceName = "sq_meio_contato")
	@Id
	@GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_meio_contato", nullable = false, unique = true)
	public Integer getIdMeioContato() {
		return idMeioContato;
	}

	public void setIdMeioContato(Integer idMeioContato) {
		this.idMeioContato = idMeioContato;
	}

	@Column(name = "vl_meio_contato", nullable = false)
	@NotNull
	public String getMeioCotnato() {
		return meioCotnato;
	}

	public void setMeioCotnato(String meioCotnato) {
		this.meioCotnato = meioCotnato;
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
}