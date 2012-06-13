package br.com.infox.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table(name="tb_pesquisa_campo", schema="public")
public class PesquisaCampo implements Serializable {

	private static final long serialVersionUID = 1L;
	private int idPesquisaCampo;
	private String nome;
	private String valor;
	private Pesquisa pesquisa;

	public void setIdPesquisaCampo(int idPesquisaCampo) {
		this.idPesquisaCampo = idPesquisaCampo;
	}

	@Id
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_pesquisa_campo")
	@GeneratedValue(generator = "generator")
	@Column(name = "id_pesquisa_campo", unique = true, nullable = false)
	public int getIdPesquisaCampo() {
		return idPesquisaCampo;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "ds_nome", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getNome() {
		return nome;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	@Column(name = "ds_valor", nullable = false, length = 200)
	@NotNull
	@Length(max = 200)
	public String getValor() {
		return valor;
	}

	public void setPesquisa(Pesquisa pesquisa) {
		this.pesquisa = pesquisa;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_pesquisa", nullable=false)
	@NotNull
	public Pesquisa getPesquisa() {
		return pesquisa;
	}

}
