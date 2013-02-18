package br.com.infox.ibpm.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import javax.validation.constraints.Size;

import br.com.infox.annotations.HierarchicalPath;
import br.com.infox.annotations.Parent;
import br.com.infox.annotations.PathDescriptor;
import br.com.infox.annotations.Recursive;

@Entity
@Table(schema="public", name=LocalizacaoFisica.TABLE_NAME)
@Recursive
public class LocalizacaoFisica implements Serializable {

	public static final String TABLE_NAME = "tb_localizacao_fisica";
	private static final long serialVersionUID = 1L;

	private int idLocalizacaoFisica;
	private LocalizacaoFisica localizacaoFisicaPai;
	private String descricao;
	private String caminhoCompleto;
	private Boolean ativo;
	
	@SequenceGenerator(name="generator", sequenceName="sq_tb_localizacao_fisica")
	@Id
	@GeneratedValue(generator="generator")
	@Column(name="id_localizacao_fisica", unique=true, nullable=false)
	public int getIdLocalizacaoFisica() {
		return idLocalizacaoFisica;
	}
	public void setIdLocalizacaoFisica(int idLocalizacaoFisica) {
		this.idLocalizacaoFisica = idLocalizacaoFisica;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localizacao_fisica_pai")
	@Parent
	public LocalizacaoFisica getLocalizacaoFisicaPai() {
		return localizacaoFisicaPai;
	}
	public void setLocalizacaoFisicaPai(LocalizacaoFisica localizacaoFisicaPai) {
		this.localizacaoFisicaPai = localizacaoFisicaPai;
	}
	@Column(name="ds_localizacao_fisica", nullable=false, length=150)
	@Size(max=150)
	@PathDescriptor
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name="ds_caminho_completo", unique=true)
	@HierarchicalPath
	public String getCaminhoCompleto() {
		return caminhoCompleto;
	}
	public void setCaminhoCompleto(String caminhoCompleto) {
		this.caminhoCompleto = caminhoCompleto;
	}
	@Column(name="in_ativo", nullable=false)
	public Boolean getAtivo() {
		return this.ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@Override
	public String toString() {
		return descricao;
	}
	
	public String caminhoCompletoToString()	{
		return caminhoCompleto.replace('|', '/').substring(0, caminhoCompleto.length()-1);
	}
	
	@Transient
	public List<LocalizacaoFisica> getListLocalizacaoFisicaAtePai() {
		List<LocalizacaoFisica> list = new ArrayList<LocalizacaoFisica>();
		LocalizacaoFisica pai = getLocalizacaoFisicaPai();
		while (pai != null) {
			list.add(pai);
			pai = pai.getLocalizacaoFisicaPai();
		}
		return list;
	}
	
}
