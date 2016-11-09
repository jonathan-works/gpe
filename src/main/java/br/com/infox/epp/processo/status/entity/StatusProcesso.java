package br.com.infox.epp.processo.status.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.marcador.Marcador;

@Entity
@Table(name = StatusProcesso.TABLE_NAME)
public class StatusProcesso implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_status_processo";
	public static final String STATUS_PROCESSO_ACTION_NAME = "setStatusProcessoAction";
	public static final String STATUS_NAO_INICIADO = "Não Iniciado";

	@Id
	@SequenceGenerator(allocationSize=1, initialValue=1, name="generator", sequenceName="sq_status_processo")
	@GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_status_processo", unique = true, nullable = false)
	private Integer idStatusProcesso; 
	
	@NotNull
	@Size(max = LengthConstants.NOME_PADRAO)
	@Column(name = "nm_status_processo", nullable = false, length = LengthConstants.NOME_PADRAO)
	private String nome;
	
	@NotNull
	@Size(max = LengthConstants.NOME_PADRAO)
	@Column(name = "ds_status_processo", nullable = false, length = LengthConstants.NOME_PADRAO)
	private String descricao;
	
	@NotNull
    @Column(name = "in_ativo", nullable = false)
    private Boolean ativo;

	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinTable(name = "tb_fluxo_status_processo", 
            joinColumns=@JoinColumn(name="id_status_processo", referencedColumnName="id_status_processo"),
            inverseJoinColumns=@JoinColumn(name="id_fluxo", referencedColumnName="id_fluxo"))
	private Set<Fluxo> fluxos = new HashSet<>(1);
	
	public Integer getIdStatusProcesso() {
		return idStatusProcesso;
	}

	public void setIdStatusProcesso(Integer idStatusProcesso) {
		this.idStatusProcesso = idStatusProcesso;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String toString() {
		return this.descricao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((getIdStatusProcesso() == null) ? 0 : getIdStatusProcesso().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof StatusProcesso))
			return false;
		StatusProcesso other = (StatusProcesso) obj;
		if (getIdStatusProcesso() == null) {
			if (other.getIdStatusProcesso() != null)
				return false;
		} else if (!getIdStatusProcesso().equals(other.getIdStatusProcesso()))
			return false;
		return true;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
}