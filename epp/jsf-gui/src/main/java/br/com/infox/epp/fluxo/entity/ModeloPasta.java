package br.com.infox.epp.fluxo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = ModeloPasta.TABLE_NAME)
public class ModeloPasta {
    protected static final String TABLE_NAME = "tb_modelo_pasta";
    private static final String SEQUENCE_NAME = "sq_modelo_pasta";
    private static final String GENERATOR_NAME = "ModeloPastaGenerator";
    
    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name=GENERATOR_NAME, sequenceName=SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_modelo_pasta", nullable = false, unique = true)
    private Integer id;

    @NotNull
    @Column(name = "cd_modelo_pasta", nullable = false)
    @Size(min = 1, max = 250)
    private String codigo;
    
    @NotNull
    @Column(name = "nm_modelo_pasta", nullable = false)
    @Size(max = 250)
    private String nome;

    @Column(name = "ds_modelo_pasta")
    @Size(max = 250)
    private String descricao;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fluxo", nullable = false)
    private Fluxo fluxo;
    
    @NotNull
    @Column(name = "in_removivel", nullable = false)
    private Boolean removivel;
    
    @NotNull
    @Column(name = "in_sistema", nullable = false)
    private Boolean sistema;
    
    @NotNull
    @Column(name = "in_editavel", nullable = false)
    private Boolean editavel;
    
    @Column(name = "nr_ordem")
    private Integer ordem;

    @NotNull
    @Column(name = "in_padrao", nullable = false)
    private Boolean padrao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Fluxo getFluxo() {
        return fluxo;
    }

    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
    }

    public Boolean getRemovivel() {
        return removivel;
    }

    
	public void setRemovivel(Boolean removivel) {
        this.removivel = removivel;
    }

    public Boolean getSistema() {
        return sistema;
    }

    public void setSistema(Boolean sistema) {
        this.sistema = sistema;
    }

    public Boolean getEditavel() {
        return editavel;
    }

    public void setEditavel(Boolean editavel) {
        this.editavel = editavel;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public Boolean getPadrao() {
        return padrao;
    }

	public void setPadrao(Boolean padrao) {
        this.padrao = padrao;
    }

	@Override
	public String toString() {
		return getNome();
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ModeloPasta))
            return false;
        ModeloPasta other = (ModeloPasta) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }
}