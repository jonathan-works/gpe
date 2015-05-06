package br.com.infox.epp.fluxo.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.processo.documento.entity.PastaRestricao;

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
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "modeloPasta")
    private List<ModeloPastaRestricao> restricoes;
    
    // TODO adicionar ordenação conforme sugerido por Nuno
    
    public ModeloPasta() {
    	restricoes = new ArrayList<>();
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    
	public List<ModeloPastaRestricao> getRestricoes() {
		return restricoes;
	}

	public void setRestricoes(List<ModeloPastaRestricao> restricoes) {
		this.restricoes = restricoes;
	}
}