package br.com.infox.epa.entity;

import java.util.ArrayList;
import java.util.List;

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
@Table(name=Natureza.TABLE_NAME, schema="public")
public class Natureza implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "tb_natureza";

	private int idNatureza;
	private String natureza;
	private Boolean hasPartes;
	private Boolean ativo;
	
	private List<NaturezaLocalizacao> naturezaLocalizacaoList = 
		new ArrayList<NaturezaLocalizacao>(0);
	private List<NaturezaCategoriaFluxo> natCatFluxoList = 
		new ArrayList<NaturezaCategoriaFluxo>(0);
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_natureza")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_natureza", unique = true, nullable = false)
	public int getIdNatureza() {
		return idNatureza;
	}
	public void setIdNatureza(int idNatureza) {
		this.idNatureza = idNatureza;
	}
	
	@Column(name="ds_natureza", length=30, nullable=false)
	public String getNatureza() {
		return natureza;
	}
	
	public void setNatureza(String natureza) {
		this.natureza = natureza;
	}
	
	@Column(name="in_partes", nullable=false)
	public Boolean getHasPartes() {
		return hasPartes;
	}
	public void setHasPartes(Boolean hasPartes) {
		this.hasPartes = hasPartes;
	}
	@Column(name="in_ativo", nullable=false)
	public Boolean getAtivo() {
		return ativo;
	}
	
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	public void setNatCatFluxoList(List<NaturezaCategoriaFluxo> natCatFluxoList) {
		this.natCatFluxoList = natCatFluxoList;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "natureza")
	public List<NaturezaCategoriaFluxo> getNatCatFluxoList() {
		return natCatFluxoList;
	}

	@Override
	public String toString() {
		return natureza;
	}
	
	public void setNaturezaLocalizacaoList(List<NaturezaLocalizacao> naturezaLocalizacaoList) {
		this.naturezaLocalizacaoList = naturezaLocalizacaoList;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "natureza")	
	public List<NaturezaLocalizacao> getNaturezaLocalizacaoList() {
		return naturezaLocalizacaoList;
	}
	
}