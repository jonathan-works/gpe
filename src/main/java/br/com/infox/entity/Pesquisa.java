package br.com.infox.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table(name="tb_pesquisa", schema="public")
public class Pesquisa implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idPesquisa;
	private String nome;
	private String descricao;
	private String colunaOrdenacao;
	private String operadorLogico;
	private String entityList;
	private List<PesquisaCampo> pesquisaCampoList = new ArrayList<PesquisaCampo>(0);

	public void setIdPesquisa(int idPesquisa) {
		this.idPesquisa = idPesquisa;
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_pesquisa")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_pesquisa", unique = true, nullable = false)
	public int getIdPesquisa() {
		return idPesquisa;
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

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "ds_descricao", length = 200)
	@Length(max = 200)
	public String getDescricao() {
		return descricao;
	}

	public void setColunaOrdenacao(String colunaOrdenacao) {
		this.colunaOrdenacao = colunaOrdenacao;
	}

	@Column(name = "ds_coluna_ordenacao", length = 50)
	@Length(max = 50)
	public String getColunaOrdenacao() {
		return colunaOrdenacao;
	}

	public void setOperadorLogico(String operadorLogico) {
		this.operadorLogico = operadorLogico;
	}

	@Column(name = "ds_operador_logico", length = 15, nullable=false)
	@NotNull
	@Length(max = 15)
	public String getOperadorLogico() {
		return operadorLogico;
	}

	public void setPesquisaCampoList(List<PesquisaCampo> pesquisaCampoList) {
		this.pesquisaCampoList = pesquisaCampoList;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, 
			   mappedBy = "pesquisa")
	public List<PesquisaCampo> getPesquisaCampoList() {
		return pesquisaCampoList;
	}

	@Override
	public String toString() {
		return nome;
	}

	public void setEntityList(String entityList) {
		this.entityList = entityList;
	}

	@Column(name = "ds_entity_list", length = 30, nullable=false)
	@NotNull
	@Length(max = 30)
	public String getEntityList() {
		return entityList;
	}
	
}
