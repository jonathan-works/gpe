package br.com.infox.ibpm.entity;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import br.com.infox.util.constants.LengthConstants;

@Entity
@Table(name = Caixa.TABLE_NAME, schema="public")
@Inheritance(strategy=InheritanceType.JOINED)
public class Caixa implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_caixa";

	private static final long serialVersionUID = 1L;
	
	private int idCaixa;
	private String nomeCaixa;
	private String dsCaixa;
	private String nomeIndice;
	private Tarefa tarefa;
	private Integer idNodeAnterior;
	private List<Processo> processoList = new ArrayList<Processo>(0);
	
	public Caixa() {
		
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_caixa")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_caixa", unique = true, nullable = false)
	public int getIdCaixa() {
		return idCaixa;
	}

	public void setIdCaixa(int idCaixa) {
		this.idCaixa = idCaixa;
	}

	@Column(name="nm_caixa", length=LengthConstants.NOME_PADRAO)
	@Size(max=LengthConstants.NOME_PADRAO)
	public String getNomeCaixa() {
		return nomeCaixa;
	}
	
	public void setNomeCaixa(String nomeCaixa) {
		this.nomeCaixa = nomeCaixa;
	}

	@Column(name="ds_caixa")
	public String getDsCaixa() {
		return dsCaixa;
	}

	public void setDsCaixa(String dsCaixa) {
		this.dsCaixa = dsCaixa;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tarefa")
	public Tarefa getTarefa() {
		return tarefa;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
	}
	
	@Column(name="nm_caixa_idx", length=LengthConstants.NOME_PADRAO, nullable=false)
    @Size(max=LengthConstants.NOME_PADRAO)
	public String getNomeIndice() {
        return nomeIndice;
    }

    public void setNomeIndice(String nomeIndice) {
        this.nomeIndice = nomeIndice;
    }

    @Column(name="id_node_anterior")
	public Integer getIdNodeAnterior() {
        return idNodeAnterior;
    }
    public void setIdNodeAnterior(Integer idNodeAnterior) {
        this.idNodeAnterior = idNodeAnterior;
    }

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "caixa")
	public List<Processo> getProcessoList() {
		return processoList;
	}

	public void setProcessoList(List<Processo> processoList) {
		this.processoList = processoList;
	}
	
	@Override
	public String toString() {
		return nomeCaixa;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Caixa)) {
			return false;
		}
		Caixa other = (Caixa) obj;
		if (getIdCaixa() != other.getIdCaixa()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdCaixa();
		return result;
	}
	
	@PreUpdate
	@PrePersist
	public void normalize() {
		String normalized = Normalizer.normalize(getNomeCaixa(), Normalizer.Form.NFD)
				.replaceAll("[^\\p{ASCII}]", "").toLowerCase();
		setNomeIndice(normalized);
	}
}